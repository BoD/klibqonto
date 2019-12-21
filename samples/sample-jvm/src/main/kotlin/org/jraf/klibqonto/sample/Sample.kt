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

package org.jraf.klibqonto.sample

import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.HttpProxy
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import kotlin.system.exitProcess

// !!!!! DO THIS FIRST !!!!!
// Replace these constants with your login / secret key
// that you will find in the Qonto web application under Settings, in the API tab.
private const val LOGIN = "xxx"
private const val SECRET_KEY = "yyy"

class Sample {
    private val client: QontoClient by lazy {
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
        )
    }

    fun main() {
        // Enable more logging
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

        runBlocking {
            // Get organization
            println("Organization:")
            val organization = client.organizations.getOrganization()
            println(organization)

            // Get first page of memberships
            println("\n\nMemberships (first page):")
            val membershipList = client.memberships.getMembershipList()
            println(membershipList.items.joinToString("\n"))

            // Get all memberships (iterate over all pages)
            println("\n\nMemberships (all):")
            val allMembershipList = client.memberships.getAllMembershipList()
            println(allMembershipList.joinToString("\n"))

            // Get first page of labels
            println("\n\nLabels:")
            val labels = client.labels.getLabelList()
            println(labels.items.joinToString("\n"))

            // Get first 2 pages of transactions
            println("\n\nTransactions:")
            val transactionList = getTransactionList(organization)
            println(transactionList.joinToString("\n") { transaction -> transaction.toFormattedString() })

            // Get the first attachment from the transaction list
            println("\n\nAttachment:")
            val attachment = getAttachment(transactionList)
            println(attachment)
        }

        // Close
        client.close()

        // Exit process
        exitProcess(0)
    }

    private suspend fun getTransactionList(organization: Organization): List<Transaction> {
        // 1/ Get first page of transactions
        val bankAccountSlug = organization.bankAccounts[0].slug
        val firstPage = client.transactions.getTransactionList(
            bankAccountSlug = bankAccountSlug,
            status = setOf(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
            updatedDateRange = DateRange(
                date("2018-01-01"),
                date("2019-12-31")
            ),
            sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
            pagination = Pagination(itemsPerPage = 10)
        )
        val list = firstPage.items.toMutableList()

        // 2/ Get next page of transactions (if any)
        firstPage.nextPagination?.let { nextPagination ->
            val secondPage = client.transactions.getTransactionList(
                bankAccountSlug = bankAccountSlug,
                status = setOf(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                updatedDateRange = DateRange(
                    date("2018-01-01"),
                    date("2019-12-31")
                ),
                sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
                pagination = nextPagination
            )
            list += secondPage.items
        }
        return list
    }

    private suspend fun getAttachment(transactionList: List<Transaction>): Attachment? {
        // Get the first attachment id of the first transaction that has at least one
        val firstAttachmentId = transactionList.firstOrNull { it.attachmentIds.isNotEmpty() }?.attachmentIds?.first()
        // Call getAttachment from the id
        return firstAttachmentId?.let { client.attachments.getAttachment(it) }
    }
}

private suspend fun QontoClient.Memberships.getAllMembershipList(): List<Membership> {
    val allMembershipList = mutableListOf<Membership>()
    var pagination: Pagination? = Pagination()
    var page: Page<Membership>
    while (pagination != null) {
        page = getMembershipList(pagination)
        allMembershipList.addAll(page.items)
        pagination = page.nextPagination
    }
    return allMembershipList
}


fun main() = Sample().main()