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

package org.jraf.klibqonto.model.attachments

interface ProbativeAttachment {
    enum class Status {
        PENDING,
        AVAILABLE,
        UNAVAILABLE,
        CORRUPTED,
    }

    /**
     * Status of probative attachment
     */
    val status: Status

    /**
     * Filename of probative attachment. Non null only when [status] is [Status.AVAILABLE].
     */
    val fileName: String?

    /**
     * Size of the file in bytes (Max size of Qonto files is 10Mo). Non null only when [status] is [Status.AVAILABLE].
     */
    val size: Long?

    /**
     * MIME type of the file. Non null only when [status] is [Status.AVAILABLE].
     */
    val contentType: String?

    /**
     * URL to download the file (Expires after 30 minutes). Non null only when [status] is [Status.AVAILABLE].
     */
    val url: String?
}
