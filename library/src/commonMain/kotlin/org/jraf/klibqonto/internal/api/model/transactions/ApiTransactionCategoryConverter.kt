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

internal object ApiTransactionCategoryConverter : ApiConverter<String, Transaction.Category>() {
    override fun apiToModel(apiModel: String): Transaction.Category {
        return when (apiModel) {
            "atm" -> Transaction.Category.ATM
            "fees" -> Transaction.Category.FEES
            "finance" -> Transaction.Category.FINANCE
            "food_and_grocery" -> Transaction.Category.FOOD_AND_GROCERY
            "gas_station" -> Transaction.Category.GAS_STATION
            "hardware_and_equipment" -> Transaction.Category.HARDWARE_AND_EQUIPMENT
            "hotel_and_lodging" -> Transaction.Category.HOTEL_AND_LODGING
            "insurance" -> Transaction.Category.INSURANCE
            "it_and_electronics" -> Transaction.Category.IT_AND_ELECTRONICS
            "legal_and_accounting" -> Transaction.Category.LEGAL_AND_ACCOUNTING
            "logistics" -> Transaction.Category.LOGISTICS
            "manufacturing" -> Transaction.Category.MANUFACTURING
            "marketing" -> Transaction.Category.MARKETING
            "office_rental" -> Transaction.Category.OFFICE_RENTAL
            "office_supply" -> Transaction.Category.OFFICE_SUPPLY
            "online_service" -> Transaction.Category.ONLINE_SERVICE
            "other_expense" -> Transaction.Category.OTHER_EXPENSE
            "other_income" -> Transaction.Category.OTHER_INCOME
            "other_service" -> Transaction.Category.OTHER_SERVICE
            "refund" -> Transaction.Category.REFUND
            "restaurant_and_bar" -> Transaction.Category.RESTAURANT_AND_BAR
            "salary" -> Transaction.Category.SALARY
            "sales" -> Transaction.Category.SALES
            "subscription" -> Transaction.Category.SUBSCRIPTION
            "tax" -> Transaction.Category.TAX
            "transport" -> Transaction.Category.TRANSPORT
            "treasury_and_interco" -> Transaction.Category.TREASURY_AND_INTERCO
            "utility" -> Transaction.Category.UTILITY
            "voucher" -> Transaction.Category.VOUCHER
            else -> throw ApiConverterException("Unknown transaction category '$apiModel'")
        }
    }
}
