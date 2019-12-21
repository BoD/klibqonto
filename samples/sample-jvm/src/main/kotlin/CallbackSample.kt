/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.HttpProxy
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.callback.CallbackQontoClient
import org.jraf.klibqonto.client.callback.Result
import org.jraf.klibqonto.client.callback.asCallbackQontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.concurrent.CountDownLatch

// !!!!! DO THIS FIRST !!!!!
// Replace these constants with your login / secret key
// that you will find in the Qonto web application under Settings, in the API tab.
private const val LOGIN = "xxx"
private const val SECRET_KEY = "yyy"

class CallbackSample {
    private val client: CallbackQontoClient by lazy {
        // Create the client
        QontoClient.newInstance(
            ClientConfiguration(
                Authentication(
                    LOGIN,
                    SECRET_KEY
                ),
                HttpConfiguration(
                    // Uncomment to see more logs
                    // loggingLevel = HttpLoggingLevel.BODY,
                    loggingLevel = HttpLoggingLevel.NONE,
                    // This is only needed to debug with, e.g., Charles Proxy
                    httpProxy = HttpProxy("localhost", 8888)
                )
            )
        ).asCallbackQontoClient()
    }

    fun main() {
        // Enable more logging
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

        // Use a countDownLatch to wait between each call
        var countDownLatch = CountDownLatch(1)

        // Get organization
        var organization: Organization? = null
        client.organizations.getOrganization { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { orga ->
                    println("Organization:")
                    println(orga)
                    organization = orga
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Get first page of memberships
        countDownLatch = CountDownLatch(1)
        client.memberships.getMembershipList { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { membershipPage ->
                    println("\n\nMemberships (first page):")
                    println(membershipPage.items.joinToString("\n"))
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Get all memberships (iterate over all pages)
        countDownLatch = CountDownLatch(1)
        client.memberships.getAllMembershipList { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { membershipList ->
                    println("\n\nMemberships (all):")
                    println(membershipList.joinToString("\n"))
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Get first page of labels
        countDownLatch = CountDownLatch(1)
        client.labels.getLabelList { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { labelPage ->
                    println("\n\nLabels:")
                    println(labelPage.items.joinToString("\n"))
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Get first 2 pages of transactions
        var transactionList: List<Transaction>? = null
        countDownLatch = CountDownLatch(1)
        getTransactionList(organization!!) { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { transacList ->
                    println("\n\nTransactions:")
                    println(transacList.joinToString("\n") { transaction -> transaction.toFormattedString() })
                    transactionList = transacList
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Get the first attachment from the transaction list
        countDownLatch = CountDownLatch(1)
        getAttachment(transactionList!!) { result ->
            result.fold(
                onFailure = ::printThrowable,
                onSuccess = { attachment ->
                    println("\n\nAttachment:")
                    println(attachment)
                }
            )
            countDownLatch.countDown()
        }
        countDownLatch.await()

        // Close
        client.close()
    }

    private fun getTransactionList(
        organization: Organization,
        onResult: (Result<List<Transaction>>) -> Unit
    ) {
        val res = mutableListOf<Transaction>()

        // 1/ Get first page of transactions
        val bankAccountSlug = organization.bankAccounts[0].slug
        client.transactions.getTransactionList(
            bankAccountSlug = bankAccountSlug,
            status = setOf(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
            updatedDateRange = DateRange(date("2018-01-01"), date("2019-12-31")),
            sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
            pagination = Pagination(itemsPerPage = 10)
        ) { result ->
            result.fold(
                onFailure = { t ->
                    printThrowable(t)
                    onResult(Result.failure(t))
                },
                onSuccess = { transactionPage ->
                    res += transactionPage.items
                    val nextPagination = transactionPage.nextPagination
                    if (nextPagination == null) {
                        onResult(Result.success(res))
                    } else {
                        // 2/ Get next page of transactions (if any)
                        client.transactions.getTransactionList(
                            bankAccountSlug = bankAccountSlug,
                            status = setOf(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                            updatedDateRange = DateRange(date("2018-01-01"), date("2019-12-31")),
                            sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
                            pagination = nextPagination
                        ) { result ->
                            result.fold(
                                onFailure = { t ->
                                    printThrowable(t)
                                    onResult(Result.failure(t))
                                },
                                onSuccess = { transactionPage ->
                                    res += transactionPage.items
                                    onResult(Result.success(res))
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    private fun getAttachment(transactionList: List<Transaction>, onResult: (Result<Attachment>) -> Unit) {
        // Get the first attachment id of the first transaction that has at least one
        val firstAttachmentId = transactionList.firstOrNull { it.attachmentIds.isNotEmpty() }?.attachmentIds?.first()
        // Call getAttachment from the id
        if (firstAttachmentId == null) {
            onResult(Result.failure(Throwable("No attachment id found in the list")))
        } else {
            client.attachments.getAttachment(firstAttachmentId) { result ->
                result.fold(
                    onFailure = { t ->
                        printThrowable(t)
                        onResult(Result.failure(t))
                    },
                    onSuccess = { attachment ->
                        onResult(Result.success(attachment))
                    }
                )
            }
        }
    }


    private fun printThrowable(t: Throwable) {
        println("An exception occurred")
        t.printStackTrace()
    }

    private fun CallbackQontoClient.Memberships.getAllMembershipList(onResult: (Result<List<Membership>>) -> Unit) {
        addAllMembershipPages(this, Pagination(), mutableListOf(), onResult)
    }

    private fun addAllMembershipPages(
        callbackQontoClientMemberships: CallbackQontoClient.Memberships,
        pagination: Pagination,
        allMembershipList: MutableList<Membership>,
        onResult: (Result<List<Membership>>) -> Unit
    ) {
        callbackQontoClientMemberships.getMembershipList(pagination) { result ->
            result.fold(
                onFailure = { t ->
                    printThrowable(t)
                    onResult(Result.failure(t))
                },
                onSuccess = { membershipPage ->
                    allMembershipList += membershipPage.items
                    val nextPagination = membershipPage.nextPagination
                    if (nextPagination != null) {
                        addAllMembershipPages(
                            callbackQontoClientMemberships,
                            nextPagination,
                            allMembershipList,
                            onResult
                        )
                    } else {
                        onResult(Result.success(allMembershipList))
                    }
                }
            )
        }
    }
}

fun main() = CallbackSample().main()
