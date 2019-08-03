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

import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.api.OkHttpHelper
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
import org.jraf.klibqonto.internal.client.QontoRetrofitService.Companion.BASE_URL
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.EnumSet

internal class QontoClientImpl(
    private val clientConfiguration: ClientConfiguration
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

    private val service: QontoRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpHelper.provideOkHttpClient(clientConfiguration, clientConfiguration.authentication))
            .build()
            .create(QontoRetrofitService::class.java)
    }


    override suspend fun getOrganization(): Organization {
        return service.getOrganization()
            .let { ApiOrganizationEnvelopeConverter.apiToModel(it) }
    }

    override suspend fun getTransactionList(
        slug: String,
        status: EnumSet<Transaction.Status>,
        updatedDateRange: Pair<Date?, Date?>,
        settledDateRange: Pair<Date?, Date?>,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination
    ): Page<Transaction> {
        val statusStrSet = status.map { ApiTransactionStatusConverter.modelToApi(it) }.toSet()
        val updatedAtFrom = ApiDateConverter.modelToApi(updatedDateRange.first)
        val updatedAtTo = ApiDateConverter.modelToApi(updatedDateRange.second)
        val settledAtFrom = ApiDateConverter.modelToApi(settledDateRange.first)
        val settledAtTo = ApiDateConverter.modelToApi(settledDateRange.second)
        val sortBy = ApiSortFieldConverter.modelToApi(sortField) + ":" + ApiSortOrderConverter.modelToApi(sortOrder)
        return service.getTransactionList(
            slug,
            statusStrSet,
            updatedAtFrom,
            updatedAtTo,
            settledAtFrom,
            settledAtTo,
            sortBy,
            pagination.pageIndex,
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiTransactionListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getMembershipList(pagination: Pagination): Page<Membership> {
        return service.getMembershipList(
            pagination.pageIndex,
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiMembershipListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getLabelList(pagination: Pagination): Page<Label> {
        return service.getLabelList(
            pagination.pageIndex,
            pagination.itemsPerPage
        )
            .let { HasApiMetaConverter.convert(it, ApiLabelListEnvelopeConverter.apiToModel(it)) }
    }

    override suspend fun getAttachment(id: String): Attachment {
        return service.getAttachment(id)
            .let { ApiAttachmentEnvelopeConverter.apiToModel(it) }
    }
}