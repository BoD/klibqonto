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

@file:JvmName("FutureQontoClientUtils")

package org.jraf.klibqonto.client.future

import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.client.future.FutureQontoClientImpl
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
import java.util.concurrent.Future

/**
 * A [Future] based version of a Qonto client.
 *
 * All the methods here are non blocking and return their results as a [Future].
 *
 * This is useful from Java, which doesn't have a notion of `suspend` functions.
 */
interface FutureQontoClient {

    /**
     * See [QontoClient.OAuth].
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
         * See [QontoClient.OAuth.extractCodeAndUniqueStateFromRedirectUri].
         */
        fun extractCodeAndUniqueStateFromRedirectUri(redirectUri: String): OAuthCodeAndUniqueState?

        /**
         * See [QontoClient.OAuth.getTokens].
         */
        fun getTokens(oAuthCredentials: OAuthCredentials, code: String): Future<OAuthTokens>

        /**
         * See [QontoClient.OAuth.refreshTokens].
         */
        fun refreshTokens(oAuthCredentials: OAuthCredentials, oAuthTokens: OAuthTokens): Future<OAuthTokens>
    }

    /**
     * See [QontoClient.Organizations].
     */
    interface Organizations {
        /**
         * See [QontoClient.Organizations.getOrganization].
         */
        fun getOrganization(): Future<Organization>
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
        ): Future<Page<Transaction>>

        /**
         * See [QontoClient.Transactions.getTransaction].
         */
        fun getTransaction(
            internalId: String,
        ): Future<Transaction>
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
        ): Future<Page<Membership>>
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
        ): Future<Page<Label>>
    }

    /**
     * See [QontoClient.Attachments].
     */
    interface Attachments {
        /**
         * See [QontoClient.Attachments.getAttachment].
         */
        fun getAttachment(id: String): Future<Attachment>

        /**
         * See [QontoClient.Attachments.getAttachmentList].
         */
        fun getAttachmentList(transactionInternalId: String): Future<List<Attachment>>

        /**
         * See [QontoClient.Attachments.addAttachment].
         */
        fun addAttachment(
            transactionInternalId: String,
            type: AttachmentType,
            input: AttachmentByteInput,
        ): Future<Void?>
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
 * Get a Future based client from a [QontoClient].
 *
 * This is useful from Java, which doesn't have a notion of `suspend` functions.
 */
fun QontoClient.asFutureQontoClient(): FutureQontoClient {
    return FutureQontoClientImpl(this)
}
