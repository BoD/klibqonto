/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2021-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.klibqonto.model.attachments.file

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jraf.klibqonto.model.attachments.AttachmentByteInput
import platform.Foundation.NSData
import platform.Foundation.NSFileHandle
import platform.Foundation.closeFile
import platform.Foundation.fileHandleForReadingAtPath
import platform.Foundation.readDataOfLength
import platform.posix.memcpy

actual class FileAttachmentByteInput actual constructor(filePath: String) : AttachmentByteInput {
    private val nsFileHandle: NSFileHandle by lazy { NSFileHandle.fileHandleForReadingAtPath(filePath)!! }

    override fun read(byteArray: ByteArray): Int {
        val nsData: NSData = nsFileHandle.readDataOfLength(byteArray.size.toULong())
        nsData.toByteArray().copyInto(byteArray)
        return nsData.length.toInt()
    }

    override fun close() {
        nsFileHandle.closeFile()
    }
}

// Taken from https://medium.com/kodein-koders/create-a-kotlin-multiplatform-library-with-swift-1a818b2dc1b0
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return byteArray
}