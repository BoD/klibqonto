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
import org.jraf.klibqonto.model.memberships.Membership
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

    /**
     * Organization related APIs.
     */
    interface Organizations {
        /**
         * Retrieve the list and details of a company's bank accounts.
         *
         * The response contains the list of bank accounts of the authenticated company.
         * There can currently only be one bank account per company.
         *
         * The [balance][org.jraf.klibqonto.model.organizations.BankAccount.balanceCents] represents
         * the actual amount of money on the account, in Euros.
         *
         * The [authorized balance][org.jraf.klibqonto.model.organizations.BankAccount.authorizedBalanceCents]
         * represents the amount available for payments, taking into account transactions that are being processed.
         * More information [here](https://support.qonto.eu/hc/en-us/articles/115000493249-How-is-the-balance-of-my-account-calculated-).
         *
         * The bank account's [slug][org.jraf.klibqonto.model.organizations.BankAccount.slug]
         * and [iban][org.jraf.klibqonto.model.organizations.BankAccount.iban] will be required for you to retrieve the
         * list of transactions inside that bank account, using [Transactions.getTransactionList].
         *
         * @see <a href="https://api-doc.qonto.eu/2.0/organizations/show-organization-1">API documentation</a>
         */
        fun getOrganization(): Flow<Organization>
    }

    /**
     * Transaction related APIs.
     */
    interface Transactions {
        enum class SortField {
            UPDATED_DATE,
            SETTLED_DATE
        }

        enum class SortOrder {
            DESCENDING,
            ASCENDING
        }

        /**
         * Retrieve all transactions within a particular bank account.
         *
         * The response contains the list of transactions that contributed to the bank account's balances
         * (e.g., incomes, transfers, cards). All transactions visible in Qonto's UI can be fetched, as of API V2.
         *
         * @param slug the [slug][org.jraf.klibqonto.model.organizations.BankAccount.slug] of the bank account from which to get the transactions
         * @param status filter to get only transactions matching these status (default: no filter)
         * @param updatedDateRange filter to get only transactions matching this update date range (default: no filter)
         * @param settledDateRange filter to get only transactions matching this settled date range (default: no filter)
         * @param sortField sort by this field (default: settled date)
         * @param sortOrder sort order (default: descending)
         * @param pagination pagination settings
         *
         * @see <a href="https://api-doc.qonto.eu/2.0/transactions/list-transactions">API documentation</a>
         */
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

    /**
     * Membership related APIs.
     */
    interface Memberships {
        /**
         * Retrieve all memberships within the organization.
         *
         * The response contains the list of memberships that are linked to the authenticated company.
         * A membership is a user who's been granted access to the Qonto account of a company.
         * There is no limit currently to the number of memberships a company can have.
         *
         * The [id][org.jraf.klibqonto.model.memberships.Membership.id] field uniquely identifies the membership
         * and is used to identify the [initiator of a transaction][org.jraf.klibqonto.model.transactions.Transaction.initiatorId].
         */
        fun getMembershipList(
            pagination: Pagination = Pagination()
        ): Flow<Page<Membership>>
    }

    /**
     * Organization related APIs.
     */
    val organizations: Organizations

    /**
     * Transaction related APIs.
     */
    val transactions: Transactions

    /**
     * Membership related APIs.
     */
    val memberships: Memberships
}