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

package org.jraf.klibqonto.sample

import kotlinx.coroutines.flow.collect
import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.HttpConfiguration
import org.jraf.klibqonto.client.HttpLoggingLevel
import org.jraf.klibqonto.client.HttpProxy
import org.jraf.klibqonto.client.QontoClient
import kotlin.system.exitProcess

const val LOGIN = "xxx"
const val SECRET_KEY = "yyy"

suspend fun main() {
    // Logging
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

    // Create the client
    val client = QontoClient.newInstance(
        ClientConfiguration(
            Authentication(
                LOGIN,
                SECRET_KEY
            ),
            HttpConfiguration(
                loggingLevel = HttpLoggingLevel.BODY,
                // This is only needed to debug with, e.g., Charles Proxy
                httpProxy = HttpProxy("localhost", 8888)
            )
        )
    )

    // Get organization
    client.organizations.getOrganization()
        .collect {
            println(it)
        }

    // Exit process
    exitProcess(0)
}