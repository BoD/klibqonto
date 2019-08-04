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

package org.jraf.klibqonto.client.flow

import kotlinx.coroutines.flow.Flow
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.client.flow.FlowQontoClientImpl
import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.EnumSet

/**
 * A [Flow] based version of a Qonto client.
 *
 * This version is useful when you want to mix it with other [Flow] based operations.
 */
interface FlowQontoClient {
    /**
     * See [QontoClient.Organizations].
     */
    interface Organizations {
        /**
         * See [QontoClient.Organizations.getOrganization].
         */
        fun getOrganization(): Flow<Organization>
    }

    /**
     * See [QontoClient.Transactions].
     */
    interface Transactions {
        /**
         * See [QontoClient.Transactions.getTransactionList].
         */
        fun getTransactionList(
            slug: String,
            status: EnumSet<Transaction.Status> = EnumSet.noneOf(Transaction.Status::class.java),
            updatedDateRange: DateRange? = null,
            settledDateRange: DateRange? = null,
            sortField: QontoClient.Transactions.SortField = QontoClient.Transactions.SortField.SETTLED_DATE,
            sortOrder: QontoClient.Transactions.SortOrder = QontoClient.Transactions.SortOrder.DESCENDING,
            pagination: Pagination = Pagination()
        ): Flow<Page<Transaction>>
    }

    /**
     * See [QontoClient.Memberships].
     */
    interface Memberships {
        /**
         * See [QontoClient.Memberships.getMembershipList].
         */
        fun getMembershipList(
            pagination: Pagination = Pagination()
        ): Flow<Page<Membership>>
    }

    /**
     * See [QontoClient.Labels].
     */
    interface Labels {
        /**
         * See [QontoClient.Labels.getLabelList].
         */
        fun getLabelList(
            pagination: Pagination = Pagination()
        ): Flow<Page<Label>>
    }

    /**
     * See [QontoClient.Attachments].
     */
    interface Attachments {
        /**
         * See [QontoClient.Attachments.getAttachment].
         */
        fun getAttachment(id: String): Flow<Attachment>
    }

    /**
     * See [QontoClient.organizations].
     */
    val organizations: Organizations

    /**
     * See [QontoClient.transactions].
     */
    val transactions: Transactions

    /**
     * See [QontoClient.memberships].
     */
    val memberships: Memberships

    /**
     * See [QontoClient.labels].
     */
    val labels: Labels

    /**
     * See [QontoClient.attachments].
     */
    val attachments: Attachments
}

/**
 * Get a [Flow] based client from a [QontoClient].
 */
fun QontoClient.asFlowQontoClient(): FlowQontoClient {
    return FlowQontoClientImpl(this)
}