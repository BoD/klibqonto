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

package org.jraf.klibqonto.internal.api.model.attachments

import org.jraf.klibqonto.internal.api.model.ApiConverter
import org.jraf.klibqonto.internal.api.model.ApiDateConverter
import org.jraf.klibqonto.internal.model.attachments.AttachmentImpl
import org.jraf.klibqonto.model.attachments.Attachment

internal object ApiAttachmentEnvelopeConverter : ApiConverter<ApiAttachmentEnvelope, Attachment>() {
    override fun apiToModel(apiModel: ApiAttachmentEnvelope) = AttachmentImpl(
        id = apiModel.attachment.id,
        fileName = apiModel.attachment.file_name,
        createdDate = ApiDateConverter.apiToModel(apiModel.attachment.created_at)!!,
        size = apiModel.attachment.file_size,
        contentType = apiModel.attachment.file_content_type,
        url = apiModel.attachment.url,
        probativeAttachment = ApiProbativeAttachmentConverter.apiToModel(apiModel.attachment.probative_attachment)
    )
}