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

package org.jraf.klibqonto.internal.client.future

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.client.future.FutureQontoClient
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.concurrent.Future

internal class FutureQontoClientImpl(
    private val qontoClient: QontoClient,
) : FutureQontoClient,
    FutureQontoClient.Organizations,
    FutureQontoClient.Transactions,
    FutureQontoClient.Memberships,
    FutureQontoClient.Labels,
    FutureQontoClient.Attachments {

    override val organizations = this
    override val transactions = this
    override val memberships = this
    override val labels = this
    override val attachments = this

    override fun getOrganization(): Future<Organization> = GlobalScope.future {
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
    ): Future<Page<Transaction>> = GlobalScope.future {
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

    override fun getTransaction(internalId: String): Future<Transaction> = GlobalScope.future {
        qontoClient.transactions.getTransaction(internalId)
    }

    override fun getMembershipList(pagination: Pagination): Future<Page<Membership>> = GlobalScope.future {
        qontoClient.memberships.getMembershipList(pagination)
    }

    override fun getLabelList(pagination: Pagination): Future<Page<Label>> = GlobalScope.future {
        qontoClient.labels.getLabelList(pagination)
    }

    override fun getAttachment(id: String): Future<Attachment> = GlobalScope.future {
        qontoClient.attachments.getAttachment(id)
    }

    override fun close() = qontoClient.close()
}
