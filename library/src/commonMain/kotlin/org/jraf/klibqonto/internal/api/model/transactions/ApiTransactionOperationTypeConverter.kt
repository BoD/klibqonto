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
import org.jraf.klibqonto.internal.api.model.ApiConverterException
import org.jraf.klibqonto.model.transactions.Transaction

internal object ApiTransactionOperationTypeConverter : ApiConverter<String, Transaction.OperationType>() {
    override fun apiToModel(apiModel: String): Transaction.OperationType {
        return when (apiModel) {
            "transfer" -> Transaction.OperationType.TRANSFER
            "card" -> Transaction.OperationType.CARD
            "direct_debit" -> Transaction.OperationType.DIRECT_DEBIT
            "income" -> Transaction.OperationType.INCOME
            "qonto_fee" -> Transaction.OperationType.QONTO_FEE
            "cheque" -> Transaction.OperationType.CHECK
            else -> throw ApiConverterException("Unknown transaction operation_type '$apiModel'")
        }
    }
}
