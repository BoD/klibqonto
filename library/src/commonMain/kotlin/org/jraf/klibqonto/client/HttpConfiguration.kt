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

package org.jraf.klibqonto.client

import kotlin.jvm.JvmOverloads

data class HttpConfiguration @JvmOverloads constructor(
    val loggingLevel: HttpLoggingLevel = HttpLoggingLevel.NONE,
    val httpProxy: HttpProxy? = null,
    val bypassSslChecks: Boolean = false,

    /**
     * Base url for the api service to use instead of the production one.
     * Used for tests only.
     * Set to `null` to use the production server.
     */
    val apiServerBaserUri: BaseUri? = null,

    /**
     * Base url for the OAuth service to use instead of the production one.
     * Used for tests only.
     * Set to `null` to use the production server.
     */
    val oAuthServerBaserUri: BaseUri? = null,
)
