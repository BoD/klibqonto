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

@file:JvmName("BlockingQontoClientUtils")

package org.jraf.klibqonto.client.blocking

import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.client.blocking.BlockingQontoClientImpl
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
import kotlin.jvm.JvmName

/**
 * A 'blocking' version of a Qonto client.
 *
 * All the methods here are blocking, meaning the calling thread will wait for the
 * result to be available.
 *
 * This is useful from Java, which doesn't have a notion of `suspend` functions.
 */
interface BlockingQontoClient {

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
        fun getTokens(oAuthCredentials: OAuthCredentials, code: String): OAuthTokens

        /**
         * See [QontoClient.OAuth.refreshTokens].
         */
        fun refreshTokens(oAuthCredentials: OAuthCredentials, oAuthTokens: OAuthTokens): OAuthTokens
    }

    /**
     * See [QontoClient.Organizations].
     */
    interface Organizations {
        /**
         * See [QontoClient.Organizations.getOrganization].
         */
        fun getOrganization(): Organization
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
        ): Page<Transaction>


        /**
         * See [QontoClient.Transactions.getTransaction].
         */
        fun getTransaction(
            internalId: String,
        ): Transaction
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
        ): Page<Membership>
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
        ): Page<Label>
    }

    /**
     * See [QontoClient.Attachments].
     */
    interface Attachments {
        /**
         * See [QontoClient.Attachments.getAttachment].
         */
        fun getAttachment(id: String): Attachment

        /**
         * See [QontoClient.Attachments.getAttachmentList].
         */
        fun getAttachmentList(transactionInternalId: String): List<Attachment>

        /**
         * See [QontoClient.Attachments.addAttachment].
         */
        fun addAttachment(transactionInternalId: String, type: AttachmentType, input: AttachmentByteInput)

        /**
         * See [QontoClient.Attachments.removeAttachment].
         */
        fun removeAttachment(transactionInternalId: String, attachmentId: String)

        /**
         * See [QontoClient.Attachments.removeAllAttachments].
         */
        fun removeAllAttachments(transactionInternalId: String)
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
 * Get a blocking client from a [QontoClient].
 *
 * This is useful from Java, which doesn't have a notion of `suspend` functions.
 */
fun QontoClient.asBlockingQontoClient(): BlockingQontoClient {
    return BlockingQontoClientImpl(this)
}
