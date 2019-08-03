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

package org.jraf.klibqonto.internal.client.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.flow.FlowQontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.Date
import java.util.EnumSet

internal class FlowQontoClientImpl(
    private val qontoClient: QontoClient
) : FlowQontoClient,
    FlowQontoClient.Organizations,
    FlowQontoClient.Transactions,
    FlowQontoClient.Memberships,
    FlowQontoClient.Labels,
    FlowQontoClient.Attachments {

    override val organizations = this
    override val transactions = this
    override val memberships = this
    override val labels = this
    override val attachments = this

    override fun getOrganization(): Flow<Organization> = qontoClient.organizations::getOrganization.asFlow()

    override fun getTransactionList(
        slug: String,
        status: EnumSet<Transaction.Status>,
        updatedDateRange: Pair<Date?, Date?>,
        settledDateRange: Pair<Date?, Date?>,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination
    ): Flow<Page<Transaction>> = flow {
        emit(
            qontoClient.transactions.getTransactionList(
                slug,
                status,
                updatedDateRange,
                settledDateRange,
                sortField,
                sortOrder,
                pagination
            )
        )
    }

    override fun getMembershipList(pagination: Pagination): Flow<Page<Membership>> = flow {
        emit(qontoClient.memberships.getMembershipList(pagination))
    }

    override fun getLabelList(pagination: Pagination): Flow<Page<Label>> = flow {
        emit(qontoClient.labels.getLabelList(pagination))
    }

    override fun getAttachment(id: String): Flow<Attachment> = flow {
        emit(qontoClient.attachments.getAttachment(id))
    }
}