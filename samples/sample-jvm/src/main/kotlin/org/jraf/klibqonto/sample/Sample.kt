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
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.HttpProxy
import org.jraf.klibqonto.client.LoginSecretKeyAuthentication
import org.jraf.klibqonto.client.OAuthAuthentication
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.oauth.OAuthCredentials
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import kotlin.random.Random
import kotlin.system.exitProcess

// !!!!! DO THIS FIRST !!!!!
// Replace these constants with your login / secret key
// that you will find in the Qonto web application under Settings, in the "Integrations (API)" section.
private const val LOGIN = "xxx"
private const val SECRET_KEY = "yyy"

// Or use OAuth if you have registered your app with Qonto.
private const val OAUTH_CLIENT_ID = "aaa"
private const val OAUTH_CLIENT_SECRET = "bbb"
private const val OAUTH_REDIRECT_URI = "https://example.com/callback"

// Set to false to use login / secret key, true to use OAuth
private const val USE_OAUTH = false

// Replace this with a transaction internal id that exists
private const val TRANSACTION_INTERNAL_ID = "00000000-0000-0000-0000-000000000000"

class Sample {
    private val oAuthCredentials = OAuthCredentials(
        clientId = OAUTH_CLIENT_ID,
        clientSecret = OAUTH_CLIENT_SECRET,
        redirectUri = OAUTH_REDIRECT_URI
    )

    private val oAuthAuthentication = OAuthAuthentication(
        oAuthCredentials = oAuthCredentials,
        // OAuthTokens will be set later, after authentication
        oAuthTokens = null
    )

    private val client: QontoClient by lazy {
        // Create the client
        QontoClient.newInstance(
            ClientConfiguration(
                if (USE_OAUTH) {
                    oAuthAuthentication
                } else {
                    LoginSecretKeyAuthentication(
                        LOGIN,
                        SECRET_KEY
                    )
                },
                HttpConfiguration(
                    // Uncomment to see more logs
                    // loggingLevel = HttpLoggingLevel.BODY,
                    loggingLevel = HttpLoggingLevel.INFO,
                    // This is only needed to debug with, e.g., Charles Proxy
                    httpProxy = HttpProxy("localhost", 8888),
                    // Can be useful in certain circumstances, but unwise to use in production
                    bypassSslChecks = true,
                )
            )
        )
    }

    fun main() {
        runBlocking {
            if (USE_OAUTH) {
                // 1/ Authenticate the user / app
                val uniqueState = Random.nextLong().toString()
                println("Navigate to this URL in a browser:")
                println(client.oAuth.getLoginUri(oAuthCredentials = oAuthCredentials, uniqueState = uniqueState))

                // 2/ Extract code
                println("After successful authentication please paste the URL in the browser's bar, and press enter:")
                val redirectUri = readLine()!!
                val codeAndUniqueState = client.oAuth.extractCodeAndUniqueStateFromRedirectUri(redirectUri)
                println(codeAndUniqueState)
                if (codeAndUniqueState == null || codeAndUniqueState.uniqueState != uniqueState) {
                    println("Something is wrong! Giving up.")
                    return@runBlocking
                }

                // 3/ Get tokens from code
                val tokens = client.oAuth.getTokens(
                    oAuthCredentials = oAuthCredentials,
                    code = codeAndUniqueState.code
                )
                println(tokens)

                // 4/ Use obtained tokens for subsequent API calls
                oAuthAuthentication.oAuthTokens = tokens

                // Later: refresh the tokens if needed.
                // Note: for now this must be handled manually. A future version of this library will handle this automatically.
                if (tokens.areAboutToExpire) {
                    val refreshedTokens = client.oAuth.refreshTokens(oAuthCredentials, tokens)
                    println(refreshedTokens)
                    oAuthAuthentication.oAuthTokens = refreshedTokens
                }
            }

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

            // Get a specific transaction
            println("\n\nTransaction $TRANSACTION_INTERNAL_ID:")
            val transaction = client.transactions.getTransaction(TRANSACTION_INTERNAL_ID)
            println(transaction)

            // Get first 2 pages of transactions
            println("\n\nTransactions:")
            val transactionList = getTransactionList(organization)
            println(transactionList.joinToString("\n\n") { it.toFormattedString() })

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
