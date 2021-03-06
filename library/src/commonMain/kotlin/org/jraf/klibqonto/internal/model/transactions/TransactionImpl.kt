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

package org.jraf.klibqonto.internal.model.transactions

import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.klibqonto.model.dates.Date
import org.jraf.klibqonto.model.labels.Label
import org.jraf.klibqonto.model.transactions.Transaction

internal data class TransactionImpl(
    override val id: String,
    override val internalId: String,
    override val amountCents: Long,
    override val attachmentIds: List<String>,
    override val attachments: List<Attachment>,
    override val localAmountCents: Long,
    override val side: Transaction.Side,
    override val category: Transaction.Category,
    override val operationType: Transaction.OperationType,
    override val currency: String,
    override val localCurrency: String,
    override val counterparty: String,
    override val settledDate: Date?,
    override val emittedDate: Date,
    override val updatedDate: Date,
    override val status: Transaction.Status,
    override val note: String?,
    override val reference: String?,
    override val vatAmountCents: Long?,
    override val vatRate: Float?,
    override val initiatorId: String?,
    override val labelIds: List<String>,
    override val labels: List<Label>,
    override val attachmentLost: Boolean,
    override val attachmentRequired: Boolean,
    override val cardLastDigits: String?,
) : Transaction
