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

package org.jraf.klibqonto.internal.client.blocking

import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.blocking.BlockingQontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.EnumSet

internal class BlockingQontoClientImpl(
    private val qontoClient: QontoClient
) : BlockingQontoClient,
    BlockingQontoClient.Organizations,
    BlockingQontoClient.Transactions,
    BlockingQontoClient.Memberships,
    BlockingQontoClient.Labels,
    BlockingQontoClient.Attachments {

    override val organizations = this
    override val transactions = this
    override val memberships = this
    override val labels = this
    override val attachments = this

    override fun getOrganization(): Organization = runBlocking {
        qontoClient.organizations.getOrganization()
    }

    override fun getTransactionList(
        slug: String,
        status: EnumSet<Transaction.Status>,
        updatedDateRange: DateRange?,
        settledDateRange: DateRange?,
        sortField: QontoClient.Transactions.SortField,
        sortOrder: QontoClient.Transactions.SortOrder,
        pagination: Pagination
    ): Page<Transaction> = runBlocking {
        qontoClient.transactions.getTransactionList(
            slug,
            status,
            updatedDateRange,
            settledDateRange,
            sortField,
            sortOrder,
            pagination
        )
    }

    override fun getMembershipList(pagination: Pagination): Page<Membership> = runBlocking {
        qontoClient.memberships.getMembershipList(pagination)
    }

    override fun getLabelList(pagination: Pagination): Page<Label> = runBlocking {
        qontoClient.labels.getLabelList(pagination)
    }

    override fun getAttachment(id: String): Attachment = runBlocking {
        qontoClient.attachments.getAttachment(id)
    }
}