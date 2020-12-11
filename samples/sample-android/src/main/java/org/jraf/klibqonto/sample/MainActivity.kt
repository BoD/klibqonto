/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

// !!!!! DO THIS FIRST !!!!!
// Replace these constants with your login / secret key
// that you will find in the Qonto web application under Settings, in the "Integrations (API)" section.
private const val LOGIN = "xxx"
private const val SECRET_KEY = "yyy"

private val TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {


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
                    loggingLevel = HttpLoggingLevel.BODY
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        GlobalScope.launch {
            // Get organization
            Log.d(TAG, "Organization:")
            val organization = client.organizations.getOrganization()
            Log.d(TAG, organization.toString())

            // Get first page of memberships
            Log.d(TAG, "\n\nMemberships (first page):")
            val membershipList = client.memberships.getMembershipList()
            Log.d(TAG, membershipList.items.joinToString("\n"))


            Log.d(TAG, "\n\nMemberships (all):")
            val allMembershipList = client.memberships.getAllMembershipList()
            Log.d(TAG, allMembershipList.joinToString("\n"))

            // Get first page of labels
            Log.d(TAG, "\n\nLabels:")
            val labels = client.labels.getLabelList()
            Log.d(TAG, labels.items.joinToString("\n"))

            // Get first 2 pages of transactions
            Log.d(TAG, "\n\nTransactions:")
            val transactionList = getTransactionList(organization)
            Log.d(
                TAG,
                transactionList.joinToString("\n") { transaction -> transaction.toFormattedString() })

            // Get the first attachment from the transaction list
            Log.d(TAG, "\n\nAttachment:")
            val attachment = getAttachment(transactionList)
            Log.d(TAG, attachment.toString())


        }
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
        val firstAttachmentId =
            transactionList.firstOrNull { it.attachmentIds.isNotEmpty() }?.attachmentIds?.first()
        // Call getAttachment from the id
        return firstAttachmentId?.let { client.attachments.getAttachment(it) }
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

    @SuppressLint("SimpleDateFormat")
    @Suppress("NOTHING_TO_INLINE")
    private inline fun date(s: String): Date = SimpleDateFormat("yyyy-MM-dd").parse(s)!!

    private fun Transaction.toFormattedString(): String =
        "${emittedDate.toFormattedString()}\t\t$counterparty\t\t${amountCents.toFormattedAmount()}\t\t$side"

    @SuppressLint("SimpleDateFormat")
    private fun Date.toFormattedString(): String = SimpleDateFormat("yyyy-MM-dd HH:mm").format(this)

    private fun Long.toFormattedAmount(): String = NumberFormat.getCurrencyInstance()
        .format(this / 100.0)

}
