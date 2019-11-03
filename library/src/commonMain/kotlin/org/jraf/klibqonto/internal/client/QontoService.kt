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
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.jraf.klibqonto.internal.api.model.attachments.ApiAttachmentEnvelope
import org.jraf.klibqonto.internal.api.model.labels.ApiLabelListEnvelope
import org.jraf.klibqonto.internal.api.model.memberships.ApiMembershipListEnvelope
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelope
import org.jraf.klibqonto.internal.api.model.transactions.ApiTransactionListEnvelope

internal class QontoService(private val httpClient: HttpClient) {
    companion object {
        private const val BASE_URL = "https://thirdparty.qonto.eu/v2/"
    }

    suspend fun getOrganization(): ApiOrganizationEnvelope {
        return httpClient.get(BASE_URL + "organizations/0")
    }

    suspend fun getTransactionList(
        slug: String,
        status: Set<String>,
        updatedAtFrom: String?,
        updatedAtTo: String?,
        settledAtFrom: String?,
        settledAtTo: String?,
        sortBy: String,
        pageIndex: Int,
        itemsPerPage: Int
    ): ApiTransactionListEnvelope {
        return httpClient.get(BASE_URL + "transactions") {
            parameter("slug", slug)
            url.parameters.appendAll("status[]", status)
            parameter("updated_at_from", updatedAtFrom)
            parameter("updated_at_to", updatedAtTo)
            parameter("settled_at_from", settledAtFrom)
            parameter("settled_at_to", settledAtTo)
            parameter("sort_by", sortBy)
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
        }
    }

    suspend fun getMembershipList(
        pageIndex: Int,
        itemsPerPage: Int
    ): ApiMembershipListEnvelope {
        return httpClient.get(BASE_URL + "memberships") {
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
        }
    }

    suspend fun getLabelList(
        pageIndex: Int,
        itemsPerPage: Int
    ): ApiLabelListEnvelope {
        return httpClient.get(BASE_URL + "labels") {
            parameter("current_page", pageIndex)
            parameter("per_page", itemsPerPage)
        }
    }

    suspend fun getAttachment(
        id: String
    ): ApiAttachmentEnvelope {
        return httpClient.get(BASE_URL + "attachments/$id")
    }
}
