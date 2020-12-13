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

package org.jraf.klibqonto.internal.api.model.transactions

import org.jraf.klibqonto.internal.api.model.ApiConverter
import org.jraf.klibqonto.internal.api.model.ApiDateConverter
import org.jraf.klibqonto.internal.model.transactions.TransactionImpl
import org.jraf.klibqonto.model.transactions.Transaction

internal object ApiTransactionConverter : ApiConverter<ApiTransaction, Transaction>() {
    override fun apiToModel(apiModel: ApiTransaction) = TransactionImpl(
        id = apiModel.transaction_id,
        amountCents = apiModel.amount_cents,
        attachmentIds = apiModel.attachment_ids,
        localAmountCents = apiModel.local_amount_cents,
        side = ApiTransactionSideConverter.apiToModel(apiModel.side),
        category = ApiTransactionCategoryConverter.apiToModel(apiModel.category),
        operationType = ApiTransactionOperationTypeConverter.apiToModel(apiModel.operation_type),
        currency = apiModel.currency,
        localCurrency = apiModel.local_currency,
        counterparty = apiModel.label,
        settledDate = ApiDateConverter.apiToModel(apiModel.settled_at),
        emittedDate = ApiDateConverter.apiToModel(apiModel.emitted_at)!!,
        updatedDate = ApiDateConverter.apiToModel(apiModel.updated_at)!!,
        status = ApiTransactionStatusConverter.apiToModel(apiModel.status),
        note = apiModel.note,
        reference = apiModel.reference,
        vatAmountCents = apiModel.vat_amount_cents,
        vatRate = apiModel.vat_rate,
        initiatorId = apiModel.initiator_id,
        labelIds = apiModel.label_ids,
        attachmentLost = apiModel.attachment_lost,
        attachmentRequired = apiModel.attachment_required,
        cardLastDigits = apiModel.card_last_digits,
    )
}