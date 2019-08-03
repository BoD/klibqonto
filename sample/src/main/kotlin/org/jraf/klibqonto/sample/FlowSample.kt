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

@file:UseExperimental(FlowPreview::class)

package org.jraf.klibqonto.sample

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.HttpProxy
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.flow.FlowQontoClient
import org.jraf.klibqonto.client.flow.asFlowQontoClient
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.EnumSet
import kotlin.system.exitProcess

object FlowSample {

    // !!!!! DO THIS FIRST !!!!!
    // Replace these constants with your login / secret key
    // that you will find in the Qonto web application under Settings, in the API tab.
    private const val LOGIN = "xxx"
    private const val SECRET_KEY = "yyy"

    private val client: FlowQontoClient by lazy {
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
        ).asFlowQontoClient()
    }

    fun main() {
        // Enable more logging
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

        runBlocking {

            // Get organization
            println("Organization:")
            getOrganization()

            // Get first page of memberships
            println("\n\nMemberships:")
            getMembershipList()

            // Get first page of labels
            println("\n\nLabels:")
            getLabelList()

            // Get first 2 pages of transactions
            val transactionList = getTransactionList()

            // Get the first attachment from the transaction list
            getAttachment(transactionList)
        }

        // Exit process
        exitProcess(0)
    }

    private suspend fun getOrganization() {
        client.organizations.getOrganization()
            .collect {
                println(it)
            }
    }

    private suspend fun getMembershipList() {
        client.memberships.getMembershipList()
            .collect {
                println(it.list.joinToString("\n"))
            }
    }

    private suspend fun getLabelList() {
        client.labels.getLabelList()
            .collect {
                println(it.list.joinToString("\n"))
            }
    }


    private fun getTransactionList(): Flow<List<Transaction>> {
        // 1/ Get organization
        return client.organizations.getOrganization()
            .flatMapConcat {
                val slug = it.bankAccounts[0].slug
                // 2/ Get first page of transactions
                client.transactions.getTransactionList(
                    slug = slug,
                    status = EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                    updatedDateRange = date("2018-01-01") to date("2019-12-31"),
                    sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
                    pagination = Pagination(itemsPerPage = 10)
                )
                    .map { firstPage ->
                        slug to firstPage
                    }
            }
            .flatMapConcat {
                val (slug, firstPage) = it
                // 3/ Get next page of transactions (if any)
                (firstPage.nextPagination?.let { nextPagination ->
                    client.transactions.getTransactionList(
                        slug = slug,
                        status = EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                        updatedDateRange = date("2018-01-01") to date("2019-12-31"),
                        sortField = QontoClient.Transactions.SortField.UPDATED_DATE,
                        pagination = nextPagination
                    )
                } ?: emptyFlow())
                    .map { nextPage ->
                        firstPage.list + nextPage.list
                    }
            }
            .onEach { transactionList ->
                println("\n\nTransactions:")
                println(transactionList.joinToString("\n") { transaction -> transaction.toFormattedString() })
            }
    }

    private suspend fun getAttachment(transactionListFlow: Flow<List<Transaction>>) {
        transactionListFlow
            // Get the first attachment id of the first transaction that has at least one
            .map { transactionList ->
                transactionList.firstOrNull { it.attachmentIds.isNotEmpty() }?.attachmentIds?.first()
            }
            // Call getAttachment from the id
            .flatMapConcat { firstAttachmentId ->
                firstAttachmentId?.let { client.attachments.getAttachment(it) } ?: emptyFlow()
            }
            .collect { attachment ->
                println("\n\nAttachment:")
                println(attachment)
            }
    }
}

fun main() = FlowSample.main()