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

package org.jraf.klibqonto.internal.api.model.organizations

import org.jraf.klibqonto.internal.api.model.ApiConverter
import org.jraf.klibqonto.internal.api.model.ApiConverterException
import org.jraf.klibqonto.model.attachments.ProbativeAttachment

internal object ApiProbativeAttachmentStatusConverter : ApiConverter<String, ProbativeAttachment.Status>() {
    override fun apiToModel(apiModel: String): ProbativeAttachment.Status {
        return when (apiModel) {
            "pending" -> ProbativeAttachment.Status.PENDING
            "available" -> ProbativeAttachment.Status.AVAILABLE
            "unavailable" -> ProbativeAttachment.Status.UNAVAILABLE
            "corrupted" -> ProbativeAttachment.Status.CORRUPTED
            else -> throw ApiConverterException("Unknown probative attachment status '$apiModel'")
        }
    }
}
