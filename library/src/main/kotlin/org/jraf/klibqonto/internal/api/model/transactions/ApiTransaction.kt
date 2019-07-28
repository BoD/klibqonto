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

internal data class ApiTransaction(
    val transaction_id: String,
    val amount: Float,
    val amount_cents: Long,
    val attachment_ids: List<String>,
    val local_amount: Float,
    val local_amount_cents: Long,
    val side: String,
    val operation_type: String,
    val currency: String,
    val local_currency: String,
    val label: String,
    val settled_at: String?,
    val emitted_at: String,
    val updated_at: String,
    val status: String,
    val note: String?,
    val reference: String?,
    val vat_amount: Float?,
    val vat_amount_cents: Long?,
    val vat_rate: Float?,
    val initiator_id: String?,
    val label_ids: List<String>,
    val attachment_lost: Boolean,
    val attachment_required: Boolean
)