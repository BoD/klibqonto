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
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.EnumSet
import kotlin.system.exitProcess

object Sample {

    // !!!!! DO THIS FIRST !!!!!
    // Replace these constants with your login / secret key
    // that you will find in the Qonto web application under Settings, in the API tab.
    private const val LOGIN = "xxx"
    private const val SECRET_KEY = "yyy"

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
            println("\n\nMemberships:")
            val membershipList = client.memberships.getMembershipList()
            println(membershipList.list.joinToString("\n"))

            // Get first page of labels
            println("\n\nLabels:")
            val labels = client.labels.getLabelList()
            println(labels.list.joinToString("\n"))

            // Get first 2 pages of transactions
            println("\n\nTransactions:")
            val transactionList = getTransactionList(organization)
            println(transactionList.joinToString("\n") { transaction -> transaction.toFormattedString() })

            // Get the first attachment from the transaction list
            println("\n\nAttachment:")
            val attachment = getAttachment(transactionList)
            println(attachment)
        }

        // Exit process
        exitProcess(0)
    }

    private suspend fun getTransactionList(organization: Organization): List<Transaction> {
        // 1/ Get first page of transactions
        val slug = organization.bankAccounts[0].slug
        val firstPage = client.transactions.getTransactionList(
            slug = slug,
            status = EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
            updatedDateRange = date("2018-01-01") to date("2019-12-31"),
            sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
            pagination = Pagination(itemsPerPage = 10)
        )
        val list = firstPage.list.toMutableList()

        // 2/ Get next page of transactions (if any)
        firstPage.nextPagination?.let { nextPagination ->
            val secondPage = client.transactions.getTransactionList(
                slug = slug,
                status = EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                updatedDateRange = date("2018-01-01") to date("2019-12-31"),
                sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
                pagination = nextPagination
            )
            list += secondPage.list
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

fun Transaction.toFormattedString(): String =
    "${emittedDate.toFormattedString()}\t\t$counterparty\t\t${amountCents.toFormattedAmount()}\t\t$side"

fun Date.toFormattedString(): String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(this)

fun Long.toFormattedAmount(): String = NumberFormat.getCurrencyInstance()
    .format(this / 100.0)

@Suppress("NOTHING_TO_INLINE")
inline fun date(s: String): Date = SimpleDateFormat("yyyy-MM-dd").parse(s)


fun main() = Sample.main()