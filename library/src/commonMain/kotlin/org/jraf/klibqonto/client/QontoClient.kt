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

package org.jraf.klibqonto.client

import org.jraf.klibqonto.internal.client.QontoClientImpl
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.attachments.AttachmentByteInput
import org.jraf.klibqonto.model.attachments.AttachmentType
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.oauth.OAuthCodeAndUniqueState
import org.jraf.klibqonto.model.oauth.OAuthCredentials
import org.jraf.klibqonto.model.oauth.OAuthScope
import org.jraf.klibqonto.model.oauth.OAuthTokens
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import kotlin.jvm.JvmStatic

interface QontoClient {
    companion object {
        @JvmStatic
        fun newInstance(configuration: ClientConfiguration): QontoClient = QontoClientImpl(configuration)
    }

    /**
     * OAuth related APIs.
     */
    interface OAuth {
        /**
         * Get the URI used to login to the Qonto service with your application.
         */
        fun getLoginUri(
            oAuthCredentials: OAuthCredentials,
            scopes: List<OAuthScope> = OAuthScope.values().toList(),
            uniqueState: String,
        ): String

        /**
         * Extract the code and unique state from the URI that the user was redirected to after login.
         *
         * @return The code and unique state, or `null` if extraction failed.
         */
        fun extractCodeAndUniqueStateFromRedirectUri(redirectUri: String): OAuthCodeAndUniqueState?

        /**
         * Retrieve OAuth tokens from a code obtained via [getLoginUri] and [extractCodeAndUniqueStateFromRedirectUri].
         */
        suspend fun getTokens(oAuthCredentials: OAuthCredentials, code: String): OAuthTokens

        /**
         * Retrieve fresh OAuth tokens from previously obtained OAuth tokens.
         *
         * Note: for now this must be handled manually. A future version of this library will handle this automatically.
         * See:
         * - https://youtrack.jetbrains.com/issue/KTOR-331
         * - https://github.com/ktorio/ktor/pull/1991/files
         */
        suspend fun refreshTokens(oAuthCredentials: OAuthCredentials, oAuthTokens: OAuthTokens): OAuthTokens
    }

    /**
     * Organization related APIs.
     */
    interface Organizations {
        /**
         * Retrieve the list and details of a company's bank accounts.
         *
         * The response contains the list of bank accounts of the authenticated company.
         *
         * The [balance][org.jraf.klibqonto.model.organizations.BankAccount.balanceCents] represents
         * the actual amount of money on the account, in Euros.
         *
         * The [authorized balance][org.jraf.klibqonto.model.organizations.BankAccount.authorizedBalanceCents]
         * represents the amount available for payments, taking into account transactions that are being processed.
         * More information [here](https://support.qonto.com/hc/en-us/articles/115000493249-How-is-the-balance-of-my-account-calculated-).
         *
         * The bank account's [slug][org.jraf.klibqonto.model.organizations.BankAccount.slug]
         * and [iban][org.jraf.klibqonto.model.organizations.BankAccount.iban] will be required for you to retrieve the
         * list of transactions inside that bank account, using [Transactions.getTransactionList].
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/organizations/show-organization-1)
         */
        suspend fun getOrganization(): Organization
    }

    /**
     * Transaction related APIs.
     */
    interface Transactions {
        enum class SortField {
            UPDATED_DATE,
            SETTLED_DATE
        }

        enum class SortOrder {
            DESCENDING,
            ASCENDING
        }

        /**
         * Retrieve all transactions within a particular bank account.
         *
         * The response contains the list of transactions that contributed to the bank account's balances
         * (e.g., incomes, transfers, cards). All transactions visible in Qonto's UI can be fetched, as of API V2.
         *
         * @param bankAccountSlug the [slug][org.jraf.klibqonto.model.organizations.BankAccount.slug] of the bank account from which to get the transactions
         * @param status filter to get only transactions matching these status (default: no filter)
         * @param updatedDateRange filter to get only transactions matching this update date range (default: no filter)
         * @param settledDateRange filter to get only transactions matching this settled date range (default: no filter)
         * @param sortField sort by this field (default: settled date)
         * @param sortOrder sort order (default: descending)
         * @param pagination pagination settings
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/transactions/list-transactions)
         */
        suspend fun getTransactionList(
            bankAccountSlug: String,
            status: Set<Transaction.Status> = emptySet(),
            updatedDateRange: DateRange? = null,
            settledDateRange: DateRange? = null,
            sortField: SortField = SortField.SETTLED_DATE,
            sortOrder: SortOrder = SortOrder.DESCENDING,
            pagination: Pagination = Pagination(),
        ): Page<Transaction>

        /**
         * Retrieve a transaction by id.
         *
         * Note: you must not pass a [Transaction.id] here but a [Transaction.internalId]!
         *
         * @param internalId the internal id of the Transaction to retrieve - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/transactions/list-transactions-1)
         */
        suspend fun getTransaction(
            internalId: String,
        ): Transaction
    }

    /**
     * Membership related APIs.
     */
    interface Memberships {
        /**
         * Retrieve all memberships within the organization.
         *
         * The response contains the list of memberships that are linked to the authenticated company.
         * A membership is a user who's been granted access to the Qonto account of a company.
         * There is no limit currently to the number of memberships a company can have.
         *
         * The [id][org.jraf.klibqonto.model.memberships.Membership.id] field uniquely identifies the membership
         * and is used to identify the [initiator of a transaction][org.jraf.klibqonto.model.transactions.Transaction.initiatorId].
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/memberships/list-memberships)
         */
        suspend fun getMembershipList(
            pagination: Pagination = Pagination(),
        ): Page<Membership>
    }

    /**
     * Labels related APIs.
     */
    interface Labels {
        /**
         * Retrieve all labels within the organization.
         *
         * The response contains the list of labels that are linked to the authenticated company.
         * The [id][org.jraf.klibqonto.model.labels.Label.id] field uniquely identifies the label and is
         * used to identify the [label ids of a transaction][org.jraf.klibqonto.model.transactions.Transaction.labelIds].
         *
         * ### Parent
         * A label can be linked to another in order to create lists.
         * The parent label can be identified thanks to the [parentId][org.jraf.klibqonto.model.labels.Label.parentId]
         * field.
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/labels/list-labels)
         */
        suspend fun getLabelList(
            pagination: Pagination = Pagination(),
        ): Page<Label>
    }

    /**
     * Attachments related APIs.
     */
    interface Attachments {
        /**
         * Obtain the details (e.g: download URL) for a specific attachment.
         *
         * Inside Qonto, attachments are files uploaded onto transactions by users.
         * Attachments typically correspond to the invoice or receipt, and are used to justify the transactions
         * from a bookkeeping standpoint.
         *
         * You can retrieve the IDs of those attachments inside each Transaction object, by calling
         * [Transactions.getTransactionList].
         *
         * **Important:** for security reasons, the [url][Attachment.url] you retrieve for each [Attachment]
         * is only valid for 30 minutes. If you need to download the file after more than 30 minutes,
         * you will need to perform another authenticated call in order to generate a new download URL.
         *
         * @param id the id of the [Attachment] to retrieve
         *
         * See also [the API documentation](https://api-doc.qonto.com/2.0/attachments/show-attachment)
         */
        suspend fun getAttachment(id: String): Attachment

        /**
         * Retrieve the list of attachments within a transaction.
         *
         * @param transactionInternalId The internal id of the Transaction - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         *
         * See also [the API documentation](https://api-doc.qonto.com/docs/business-api/reference/openapi_v2.yml/paths/~1v2~1transactions~1%7Btransaction_id%7D~1attachments/get)
         */
        suspend fun getAttachmentList(transactionInternalId: String): List<Attachment>

        /**
         * Add an attachment to a transaction.
         *
         * @param transactionInternalId The internal id of the Transaction - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         * @param type The image type of the attachment (currently only png, jpeg, and pdf are supported)
         * @param input The byte data of the attachment.
         *
         * Note: the given input won't be closed by this method.
         *
         * See also [the API documentation](https://api-doc.qonto.com/docs/business-api/reference/openapi_v2.yml/paths/~1v2~1transactions~1%7Btransaction_id%7D~1attachments/post)
         */
        suspend fun addAttachment(transactionInternalId: String, type: AttachmentType, input: AttachmentByteInput)

        /**
         * Remove an attachment from a transaction
         *
         * @param transactionInternalId The internal id of the Transaction - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         * @param attachmentId The id of the attachment to remove - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         *
         * See also [the API documentation](https://api-doc.qonto.com/docs/business-api/reference/openapi_v2.yml/paths/~1v2~1transactions~1%7Btransaction_id%7D~1attachments~1%7Bid%7D/delete)
         */
        suspend fun removeAttachment(transactionInternalId: String, attachmentId: String)

        /**
         * Remove all attachments from a transaction.
         *
         * @param transactionInternalId The internal id of the Transaction - e.g. `4c306508-dac9-410b-9937-e87b02462e42`
         *
         * See also [the API documentation](https://api-doc.qonto.com/docs/business-api/reference/openapi_v2.yml/paths/~1v2~1transactions~1%7Btransaction_id%7D~1attachments/delete)
         */
        suspend fun removeAllAttachments(transactionInternalId: String)
    }


    /**
     * OAuth related APIs.
     */
    val oAuth: OAuth

    /**
     * Organization related APIs.
     */
    val organizations: Organizations

    /**
     * Transaction related APIs.
     */
    val transactions: Transactions

    /**
     * Membership related APIs.
     */
    val memberships: Memberships

    /**
     * Labels related APIs.
     */
    val labels: Labels

    /**
     * Attachments related APIs.
     */
    val attachments: Attachments

    /**
     * Dispose of this client instance.
     * This will release some resources so it is recommended to call it after use.
     *
     * **Note: this client will no longer be usable after this is called.**
     */
    fun close()
}
