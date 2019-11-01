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

package org.jraf.klibqonto.model.transactions

import org.jraf.klibqonto.model.dates.Date

interface Transaction {
    enum class Side {
        CREDIT,
        DEBIT,
    }

    enum class OperationType {
        TRANSFER,
        CARD,
        DIRECT_DEBIT,
        INCOME,
        QONTO_FEE,
        CHECK,
    }

    enum class Status {
        PENDING,
        REVERSED,
        DECLINED,
        COMPLETED,
    }

    /**
     * ID of the transaction (e.g: acme-corp-1111-1-transaction-123)
     */
    val id: String

    /**
     * Amount of the transaction, in euro cents
     */
    val amountCents: Long

    /**
     * List of attachments' id
     */
    val attachmentIds: List<String>

    /**
     * Amount in cents of the local_currency
     */
    val localAmountCents: Long

    /**
     * Side (debit or credit)
     */
    val side: Side

    /**
     * Operation type
     */
    val operationType: OperationType

    /**
     * ISO 4217 currency code of the bank account (can only be EUR, currently)
     */
    val currency: String

    /**
     * ISO 4217 currency code of the bank account (can be any currency)
     */
    val localCurrency: String

    /**
     * Counterparty of the transaction (e.g: Amazon)
     */
    val counterparty: String

    /**
     * Date the transaction impacted the balance of the account
     */
    val settledDate: Date?

    /**
     * Date at which the transaction impacted the authorized balance of the account
     */
    val emittedDate: Date

    /**
     * Date at which the transaction was last updated
     */
    val updatedDate: Date

    /**
     * Status
     */
    val status: Status

    /**
     * Memo added by the user on the transaction
     */
    val note: String?

    /**
     * Message sent along income, transfer and direct_debit transactions
     */
    val reference: String?

    /**
     * Amount of VAT filled in on the transaction, in euro cents
     */
    val vatAmountCents: Long?

    /**
     * Allowed Values: -1, 0, 2.1, 5.5, 10, 20
     */
    val vatRate: Float?

    /**
     * ID of the membership who initiated the transaction
     */
    val initiatorId: String?

    /**
     * List of labels' id
     */
    val labelIds: List<String>

    /**
     * Indicates if the transaction's attachment was lost (default: false)
     */
    val attachmentLost: Boolean

    /**
     * Indicates if the transaction's attachment is  (default: true)
     */
    val attachmentRequired: Boolean
}
