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
        apiModel.transaction_id,
        apiModel.amount_cents,
        apiModel.attachment_ids,
        apiModel.local_amount_cents,
        ApiTransactionSideConverter.apiToModel(apiModel.side),
        ApiTransactionOperationTypeConverter.apiToModel(apiModel.operation_type),
        apiModel.currency,
        apiModel.local_currency,
        apiModel.label,
        ApiDateConverter.apiToModel(apiModel.settled_at),
        ApiDateConverter.apiToModel(apiModel.emitted_at)!!,
        ApiDateConverter.apiToModel(apiModel.updated_at)!!,
        ApiTransactionStatusConverter.apiToModel(apiModel.status),
        apiModel.note,
        apiModel.reference,
        apiModel.vat_amount_cents,
        apiModel.vat_rate,
        apiModel.initiator_id,
        apiModel.label_ids,
        apiModel.attachment_lost,
        apiModel.attachment_required
    )
}