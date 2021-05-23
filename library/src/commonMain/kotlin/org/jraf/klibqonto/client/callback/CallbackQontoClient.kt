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

@file:JvmName("CallbackQontoClientUtils")

package org.jraf.klibqonto.client.callback

import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.client.callback.CallbackQontoClientImpl
import org.jraf.klibqonto.model.attachments.Attachment
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
import kotlin.jvm.JvmName

/**
 * A callback based version of a Qonto client.
 *
 * All the methods here return immediately and take a lambda argument that will be executed
 * when the result is available (or an exception occurred).
 * The lambda is provided with a [Result] object.
 *
 * This is useful for Java and Swift, which don't have a notion of `suspend` functions.
 */
interface CallbackQontoClient {

    /**
     * See [QontoClient.OAuth].
     */
    interface OAuth {
        /**
         * Get the URI used to login to the Qonto service with your application.
         */
        fun getLoginUri(
            oAuthCredentials: OAuthCredentials,
            scopes: List<OAuthScope> = listOf(
                OAuthScope.OFFLINE_ACCESS,
                OAuthScope.ORGANIZATION_READ,
                OAuthScope.OPENID,
            ),
            uniqueState: String,
        ): String

        /**
         * See [QontoClient.OAuth.extractCodeAndUniqueStateFromRedirectUri].
         */
        fun extractCodeAndUniqueStateFromRedirectUri(redirectUri: String): OAuthCodeAndUniqueState?

        /**
         * See [QontoClient.OAuth.getTokens].
         */
        fun getTokens(oAuthCredentials: OAuthCredentials, code: String, onResult: (Result<OAuthTokens>) -> Unit)

        /**
         * See [QontoClient.OAuth.refreshTokens].
         */
        fun refreshTokens(
            oAuthCredentials: OAuthCredentials,
            oAuthTokens: OAuthTokens,
            onResult: (Result<OAuthTokens>) -> Unit,
        )
    }

    /**
     * See [QontoClient.Organizations].
     */
    interface Organizations {
        /**
         * See [QontoClient.Organizations.getOrganization].
         */
        fun getOrganization(onResult: (Result<Organization>) -> Unit)
    }

    /**
     * See [QontoClient.Transactions].
     */
    interface Transactions {
        /**
         * See [QontoClient.Transactions.getTransactionList].
         */
        fun getTransactionList(
            bankAccountSlug: String,
            status: Set<Transaction.Status> = emptySet(),
            updatedDateRange: DateRange? = null,
            settledDateRange: DateRange? = null,
            sortField: QontoClient.Transactions.SortField = QontoClient.Transactions.SortField.SETTLED_DATE,
            sortOrder: QontoClient.Transactions.SortOrder = QontoClient.Transactions.SortOrder.DESCENDING,
            pagination: Pagination = Pagination(),
            onResult: (Result<Page<Transaction>>) -> Unit,
        )

        /**
         * See [QontoClient.Transactions.getTransaction].
         */
        fun getTransaction(
            internalId: String,
            onResult: (Result<Transaction>) -> Unit,
        )
    }

    /**
     * See [QontoClient.Memberships].
     */
    interface Memberships {
        /**
         * See [QontoClient.Memberships.getMembershipList].
         */
        fun getMembershipList(
            pagination: Pagination = Pagination(),
            onResult: (Result<Page<Membership>>) -> Unit,
        )
    }

    /**
     * See [QontoClient.Labels].
     */
    interface Labels {
        /**
         * See [QontoClient.Labels.getLabelList].
         */
        fun getLabelList(
            pagination: Pagination = Pagination(),
            onResult: (Result<Page<Label>>) -> Unit,
        )
    }

    /**
     * See [QontoClient.Attachments].
     */
    interface Attachments {
        /**
         * See [QontoClient.Attachments.getAttachment].
         */
        fun getAttachment(
            id: String,
            onResult: (Result<Attachment>) -> Unit,
        )

        /**
         * See [QontoClient.Attachments.getAttachmentList].
         */
        fun getAttachmentList(
            transactionInternalId: String,
            onResult: (Result<List<Attachment>>) -> Unit,
        )
    }


    /**
     * See [QontoClient.OAuth].
     */
    val oAuth: OAuth

    /**
     * See [QontoClient.organizations].
     */
    val organizations: Organizations

    /**
     * See [QontoClient.transactions].
     */
    val transactions: Transactions

    /**
     * See [QontoClient.memberships].
     */
    val memberships: Memberships

    /**
     * See [QontoClient.labels].
     */
    val labels: Labels

    /**
     * See [QontoClient.attachments].
     */
    val attachments: Attachments

    /**
     * See [QontoClient.close].
     */
    fun close()
}

/**
 * Get a callback based client from a [QontoClient].
 *
 * This is useful for Java and Swift, which don't have a notion of `suspend` functions.
 */
fun QontoClient.asCallbackQontoClient(): CallbackQontoClient {
    return CallbackQontoClientImpl(this)
}
