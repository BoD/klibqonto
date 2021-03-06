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

package org.jraf.klibqonto.internal.client.callback

import kotlinx.coroutines.launch
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.callback.CallbackQontoClient
import org.jraf.klibqonto.internal.client.klibQontoScope
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

internal class CallbackQontoClientImpl(
    private val qontoClient: QontoClient,
) : CallbackQontoClient,
    CallbackQontoClient.OAuth,
    CallbackQontoClient.Organizations,
    CallbackQontoClient.Transactions,
    CallbackQontoClient.Memberships,
    CallbackQontoClient.Labels,
    CallbackQontoClient.Attachments {

    override val oAuth = this
    override val organizations = this
    override val transactions = this
    override val memberships = this
    override val labels = this
    override val attachments = this

    override fun getLoginUri(
        oAuthCredentials: OAuthCredentials,
        scopes: List<OAuthScope>,
        uniqueState: String,
    ): String = qontoClient.oAuth.getLoginUri(
        oAuthCredentials = oAuthCredentials,
        scopes = scopes,
        uniqueState = uniqueState,
    )

    override fun extractCodeAndUniqueStateFromRedirectUri(redirectUri: String): OAuthCodeAndUniqueState? =
        qontoClient.oAuth.extractCodeAndUniqueStateFromRedirectUri(redirectUri)

    override fun getTokens(
        oAuthCredentials: OAuthCredentials,
        code: String,
        onResult: (Result<OAuthTokens>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.oAuth.getTokens(
            oAuthCredentials = oAuthCredentials,
            code = code,
        )
    }

    override fun refreshTokens(
        oAuthCredentials: OAuthCredentials,
        oAuthTokens: OAuthTokens,
        onResult: (Result<OAuthTokens>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.oAuth.refreshTokens(
            oAuthCredentials = oAuthCredentials,
            oAuthTokens = oAuthTokens,
        )
    }

    override fun getOrganization(onResult: (Result<Organization>) -> Unit) = launchAndCallback(onResult) {
        qontoClient.organizations.getOrganization()
    }

    override fun getTransactionList(
        bankAccountSlug: String,
        status: Set<Transaction.Status>,
        updatedDateRange: DateRange?,
        settledDateRange: DateRange?,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination,
        onResult: (Result<Page<Transaction>>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.transactions.getTransactionList(
            bankAccountSlug,
            status,
            updatedDateRange,
            settledDateRange,
            sortField,
            sortOrder,
            pagination
        )
    }

    override fun getTransaction(
        internalId: String,
        onResult: (Result<Transaction>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.transactions.getTransaction(internalId)
    }

    override fun getMembershipList(
        pagination: Pagination,
        onResult: (Result<Page<Membership>>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.memberships.getMembershipList(pagination)
    }

    override fun getLabelList(
        pagination: Pagination,
        onResult: (Result<Page<Label>>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.labels.getLabelList(pagination)
    }

    override fun getAttachment(
        id: String,
        onResult: (Result<Attachment>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.attachments.getAttachment(id)
    }

    override fun getAttachmentList(
        transactionInternalId: String,
        onResult: (Result<List<Attachment>>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.attachments.getAttachmentList(transactionInternalId)
    }

    override fun addAttachment(
        transactionInternalId: String,
        type: AttachmentType,
        input: AttachmentByteInput,
        onResult: (Result<Unit>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.attachments.addAttachment(transactionInternalId, type, input)
    }

    override fun removeAttachment(
        transactionInternalId: String,
        attachmentId: String,
        onResult: (Result<Unit>) -> Unit,
    ) = launchAndCallback(onResult) {
        qontoClient.attachments.removeAttachment(transactionInternalId, attachmentId)
    }

    override fun removeAllAttachments(transactionInternalId: String, onResult: (Result<Unit>) -> Unit) =
        launchAndCallback(onResult) {
            qontoClient.attachments.removeAllAttachments(transactionInternalId)
        }

    override fun close() = qontoClient.close()

    private fun <T : Any> launchAndCallback(
        onResult: (Result<T>) -> Unit,
        block: suspend () -> T,
    ) {
        klibQontoScope.launch {
            onResult(runCatching { block() })
        }
    }
}
