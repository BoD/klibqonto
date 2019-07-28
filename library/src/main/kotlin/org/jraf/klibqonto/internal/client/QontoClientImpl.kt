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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.api.OkHttpHelper
import org.jraf.klibqonto.internal.api.model.ApiDateConverter
import org.jraf.klibqonto.internal.api.model.memberships.ApiMembershipListEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.pagination.HasApiMetaConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiSortFieldConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiSortOrderConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionListEnvelopeConverter
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionStatusConverter
import org.jraf.klibqonto.internal.client.QontoRetrofitService.Companion.BASE_URL
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
    QontoClient.Memberships {

    override val organizations = this
    override val transactions = this
    override val memberships = this

    private val service: QontoRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpHelper.provideOkHttpClient(clientConfiguration, clientConfiguration.authentication))
            .build()
            .create(QontoRetrofitService::class.java)
    }


    override fun getOrganization(): Flow<Organization> {
        return flow {
            emit(service.getOrganization())
        }
            .map { ApiOrganizationEnvelopeConverter.apiToModel(it) }
    }

    override fun getTransactionList(
        slug: String,
        status: EnumSet<Transaction.Status>,
        updatedDateRange: Pair<Date?, Date?>,
        settledDateRange: Pair<Date?, Date?>,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination
    ): Flow<Page<Transaction>> {
        val statusStrSet = status.map { ApiTransactionStatusConverter.modelToApi(it) }.toSet()
        val updatedAtFrom = ApiDateConverter.modelToApi(updatedDateRange.first)
        val updatedAtTo = ApiDateConverter.modelToApi(updatedDateRange.second)
        val settledAtFrom = ApiDateConverter.modelToApi(settledDateRange.first)
        val settledAtTo = ApiDateConverter.modelToApi(settledDateRange.second)
        val sortBy = ApiSortFieldConverter.modelToApi(sortField) + ":" + ApiSortOrderConverter.modelToApi(sortOrder)
        return flow {
            emit(
                service.getTransactionList(
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
            )
        }
            .map { HasApiMetaConverter.convert(it, ApiTransactionListEnvelopeConverter.apiToModel(it)) }
    }

    override fun getMembershipList(pagination: Pagination): Flow<Page<Membership>> {
        return flow {
            emit(
                service.getMembershipList(
                    pagination.pageIndex,
                    pagination.itemsPerPage
                )
            )
        }
            .map { HasApiMetaConverter.convert(it, ApiMembershipListEnvelopeConverter.apiToModel(it)) }
    }

}