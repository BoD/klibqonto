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

import org.jraf.klibqonto.internal.api.model.labels.ApiLabelListEnvelope
import org.jraf.klibqonto.internal.api.model.memberships.ApiMembershipListEnvelope
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelope
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionListEnvelope
import retrofit2.http.GET
import retrofit2.http.Query

internal interface QontoRetrofitService {
    companion object {
        const val BASE_URL = "https://thirdparty.qonto.eu/v2/"
    }

    @GET("organizations/0")
    suspend fun getOrganization(): ApiOrganizationEnvelope

    @GET("transactions")
    suspend fun getTransactionList(
        @Query("slug") slug: String,
        @Query("status[]") status: Set<String>,
        @Query("updated_at_from") updatedAtFrom: String?,
        @Query("updated_at_to") updatedAtTo: String?,
        @Query("settled_at_from") settledAtFrom: String?,
        @Query("settled_at_to") settledAtTo: String?,
        @Query("sort_by") sortBy: String,
        @Query("current_page") pageIndex: Int,
        @Query("per_page") itemsPerPage: Int
    ): ApiTransactionListEnvelope

    @GET("memberships")
    suspend fun getMembershipList(
        @Query("current_page") pageIndex: Int,
        @Query("per_page") itemsPerPage: Int
    ): ApiMembershipListEnvelope

    @GET("labels")
    suspend fun getLabelList(
        @Query("current_page") pageIndex: Int,
        @Query("per_page") itemsPerPage: Int
    ): ApiLabelListEnvelope
}