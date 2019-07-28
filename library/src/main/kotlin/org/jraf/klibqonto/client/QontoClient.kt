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

package org.jraf.klibqonto.client

import kotlinx.coroutines.flow.Flow
import org.jraf.klibqonto.internal.client.QontoClientImpl
import org.jraf.klibqonto.model.organizations.Organization
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction
import java.util.Date
import java.util.EnumSet

interface QontoClient {
    companion object {
        fun newInstance(configuration: ClientConfiguration): QontoClient = QontoClientImpl(configuration)
    }

    interface Organizations {
        fun getOrganization(): Flow<Organization>
    }

    interface Transactions {
        enum class SortField {
            UPDATED_DATE,
            SETTLED_DATE
        }

        enum class SortOrder {
            DESCENDING,
            ASCENDING
        }

        fun getTransactionList(
            slug: String,
            status: EnumSet<Transaction.Status> = EnumSet.noneOf(Transaction.Status::class.java),
            updatedDateRange: Pair<Date?, Date?> = null to null,
            settledDateRange: Pair<Date?, Date?> = null to null,
            sortField: SortField = SortField.SETTLED_DATE,
            sortOrder: SortOrder = SortOrder.DESCENDING,
            pagination: Pagination = Pagination()
        ): Flow<Page<Transaction>>
    }

    val organizations: Organizations
    val transactions: Transactions
}