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

package org.jraf.klibqonto.internal.client

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.util.InternalAPI
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.writeFully
import org.jraf.klibqonto.client.BaseUri
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.internal.api.model.attachments.ApiAttachmentEnvelope
import org.jraf.klibqonto.internal.api.model.attachments.ApiAttachmentListEnvelope
import org.jraf.klibqonto.internal.api.model.labels.ApiLabelListEnvelope
import org.jraf.klibqonto.internal.api.model.memberships.ApiMembershipListEnvelope
import org.jraf.klibqonto.internal.api.model.oauth.ApiOAuthTokens
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelope
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionEnvelope
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionListEnvelope
import org.jraf.klibqonto.model.attachments.AttachmentByteInput
import org.jraf.klibqonto.model.attachments.AttachmentType

internal class QontoService(
    clientConfiguration: ClientConfiguration,
    private val httpClient: HttpClient,
) {
    private val apiServerBaserUri = clientConfiguration.httpConfiguration.apiServerBaserUri ?: BaseUri(
        scheme = "https",
        host = "thirdparty.qonto.com",
    )
    private val apiBaseUri = "$apiServerBaserUri/v2/"

    private val oAuthServerBaserUri = clientConfiguration.httpConfiguration.oAuthServerBaserUri ?: BaseUri(
        scheme = "https",
        host = "oauth.qonto.com",
    )
    val oAuthBaseScheme = oAuthServerBaserUri.scheme
    val oAuthBaseHost = oAuthServerBaserUri.host
    val oAuthBasePath = "oauth2"
    private val oAuthBaseUri = "$oAuthServerBaserUri/$oAuthBasePath/"

    @OptIn(InternalAPI::class)
    suspend fun getOAuthTokens(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
    ): ApiOAuthTokens {
        return httpClient.post(oAuthBaseUri + "token") {
            header(
                HttpHeaders.Authorization,
                getClientSecretBase64(clientId, clientSecret)
            )
            body = FormDataContent(Parameters.build {
                append("code", code)
                append("redirect_uri", redirectUri)
                append("grant_type", "authorization_code")
            })
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun refreshOAuthTokens(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        refreshToken: String,
    ): ApiOAuthTokens {
        return httpClient.post(oAuthBaseUri + "token") {
            header(
                HttpHeaders.Authorization,
                getClientSecretBase64(clientId, clientSecret)
            )
            body = FormDataContent(Parameters.build {
                append("refresh_token", refreshToken)
                append("redirect_uri", redirectUri)
                append("grant_type", "refresh_token")
            })
        }
    }

    @OptIn(InternalAPI::class)
    private fun getClientSecretBase64(clientId: String, clientSecret: String): String {
        // TODO Don't depend on private encodeBase64 KTOR API
        val clientSecretBase64 = "$clientId:$clientSecret".encodeBase64()
        return "Basic $clientSecretBase64"
    }


    suspend fun getOrganization(): ApiOrganizationEnvelope {
        return httpClient.get(apiBaseUri + "organizations/0")
    }

    suspend fun getTransactionList(
        bankAccountSlug: String,
        status: Set<String>,
        updatedAtFrom: String?,
        updatedAtTo: String?,
        settledAtFrom: String?,
        settledAtTo: String?,
        sortBy: String,
        pageIndex: Int,
        itemsPerPage: Int,
    ): ApiTransactionListEnvelope {
        return httpClient.get(apiBaseUri + "transactions") {
            parameter("slug", bankAccountSlug)
            url.parameters.appendAll("status[]", status)
            parameter("updated_at_from", updatedAtFrom)
            parameter("updated_at_to", updatedAtTo)
            parameter("settled_at_from", settledAtFrom)
            parameter("settled_at_to", settledAtTo)
            parameter("sort_by", sortBy)
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
            parameter("includes[]", "labels")
            parameter("includes[]", "attachments")
        }
    }

    suspend fun getTransaction(internalId: String): ApiTransactionEnvelope {
        return httpClient.get(apiBaseUri + "transactions/$internalId") {
            parameter("includes[]", "labels")
            parameter("includes[]", "attachments")
        }
    }

    suspend fun getMembershipList(
        pageIndex: Int,
        itemsPerPage: Int,
    ): ApiMembershipListEnvelope {
        return httpClient.get(apiBaseUri + "memberships") {
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
        }
    }

    suspend fun getLabelList(
        pageIndex: Int,
        itemsPerPage: Int,
    ): ApiLabelListEnvelope {
        return httpClient.get(apiBaseUri + "labels") {
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
        }
    }

    suspend fun getAttachment(
        id: String,
    ): ApiAttachmentEnvelope {
        return httpClient.get(apiBaseUri + "attachments/$id")
    }

    suspend fun getAttachmentList(
        transactionInternalId: String,
    ): ApiAttachmentListEnvelope {
        return httpClient.get(apiBaseUri + "transactions/$transactionInternalId/attachments")
    }

    suspend fun addAttachment(transactionInternalId: String, type: AttachmentType, input: AttachmentByteInput) {
        httpClient.submitFormWithBinaryData<Unit>(
            url = apiBaseUri + "transactions/$transactionInternalId/attachments",
            formData = formData {
                append(
                    key = "file",
                    filename = "file." + when (type) {
                        AttachmentType.PNG -> "png"
                        AttachmentType.JPEG -> "jpg"
                        AttachmentType.PDF -> "pdf"
                    },
                    contentType = when (type) {
                        AttachmentType.PNG -> ContentType.Image.PNG
                        AttachmentType.JPEG -> ContentType.Image.JPEG
                        AttachmentType.PDF -> ContentType.Application.Pdf
                    }
                ) {
                    val buffer = ByteArray(1024)
                    var read: Int
                    do {
                        read = input.read(buffer)
                        if (read > 0) writeFully(buffer, 0, read)
                    } while (read == buffer.size)
                    input.close()
                }
            }
        )
    }

    suspend fun removeAttachment(transactionInternalId: String, attachmentId: String) {
        httpClient.delete<Unit>(apiBaseUri + "transactions/$transactionInternalId/attachments/$attachmentId")
    }

    suspend fun removeAllAttachments(transactionInternalId: String) {
        httpClient.delete<Unit>(apiBaseUri + "transactions/$transactionInternalId/attachments")
    }
}
