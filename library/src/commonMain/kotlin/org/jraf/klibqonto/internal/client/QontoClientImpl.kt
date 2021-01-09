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
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.features.UserAgent
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLBuilder
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.api.model.ApiDateConverter
import org.jraf.klibqonto.internal.api.model.attachments.ApiAttachmentEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.labels.ApiLabelListEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.memberships.ApiMembershipListEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.pagination.HasApiMetaConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiSortFieldConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiSortOrderConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionListEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionStatusConverter
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction

internal class QontoClientImpl(
    clientConfiguration: ClientConfiguration
) : QontoClient,
    QontoClient.Organizations,
    QontoClient.Transactions,
    QontoClient.Memberships,
    QontoClient.Labels,
    QontoClient.Attachments {

    override val organizations = this
    override val transactions = this
    override val memberships = this
    override val labels = this
    override val attachments = this

    @OptIn(KtorExperimentalAPI::class)
    private val httpClient by lazy {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    Json {
                        // XXX Comment this to have API changes make the parsing fail
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                header(
                    "Authorization",
                    "${clientConfiguration.authentication.login}:${clientConfiguration.authentication.secretKey}"
                )
            }
            install(UserAgent) {
                agent = clientConfiguration.userAgent
            }
            engine {
                // Setup a proxy if requested
                clientConfiguration.httpConfiguration.httpProxy?.let { httpProxy ->
                    proxy = ProxyBuilder.http(URLBuilder().apply {
                        host = httpProxy.host
                        port = httpProxy.port
                    }.build())
                }
            }
            // Setup logging if requested
            if (clientConfiguration.httpConfiguration.loggingLevel != HttpLoggingLevel.NONE) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = when (clientConfiguration.httpConfiguration.loggingLevel) {
                        HttpLoggingLevel.NONE -> LogLevel.NONE
                        HttpLoggingLevel.INFO -> LogLevel.INFO
                        HttpLoggingLevel.HEADERS -> LogLevel.HEADERS
                        HttpLoggingLevel.BODY -> LogLevel.BODY
                        HttpLoggingLevel.ALL -> LogLevel.ALL
                    }
                }
            }
        }
    }

    private val service: QontoService by lazy {
        QontoService(httpClient)
    }

    override suspend fun getOrganization(): Organization {
        return service.getOrganization()
            .let { ApiOrganizationEnvelopeConverter.apiToModel(it) }
    }

    override suspend fun getTransactionList(
        bankAccountSlug: String,
        status: Set<Transaction.Status>,
        updatedDateRange: DateRange?,
        settledDateRange: DateRange?,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination
    ): Page<Transaction> {
        val statusStrSet = status.map { ApiTransactionStatusConverter.modelToApi(it) }.toSet()
        val updatedAtFrom = ApiDateConverter.modelToApi(updatedDateRange?.from)
        val updatedAtTo = ApiDateConverter.modelToApi(updatedDateRange?.to)
        val settledAtFrom = ApiDateConverter.modelToApi(settledDateRange?.from)
        val settledAtTo = ApiDateConverter.modelToApi(settledDateRange?.to)
        val sortBy = ApiSortFieldConverter.modelToApi(sortField) + ":" + ApiSortOrderConverter.modelToApi(sortOrder)
        return service.getTransactionList(
            bankAccountSlug,
            statusStrSet,
            updatedAtFrom,
            updatedAtTo,
            settledAtFrom,
            settledAtTo,
            sortBy,
            pagination.pageIndex.coerceAtLeast(Pagination.FIRST_PAGE_INDEX),
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiTransactionListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getMembershipList(pagination: Pagination): Page<Membership> {
        return service.getMembershipList(
            pagination.pageIndex.coerceAtLeast(Pagination.FIRST_PAGE_INDEX),
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiMembershipListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getLabelList(pagination: Pagination): Page<Label> {
        return service.getLabelList(
            pagination.pageIndex.coerceAtLeast(Pagination.FIRST_PAGE_INDEX),
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiLabelListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getAttachment(id: String): Attachment {
        return service.getAttachment(id)
            .let { ApiAttachmentEnvelopeConverter.apiToModel(it) }
    }

    override fun close() = httpClient.close()
}
