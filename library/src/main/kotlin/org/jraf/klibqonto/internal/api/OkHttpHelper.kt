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

package org.jraf.klibqonto.internal.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jraf.klibqonto.client.Authentication
import org.jraf.klibqonto.client.ClientConfiguration
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Proxy

internal object OkHttpHelper {
    private var LOGGER = LoggerFactory.getLogger(OkHttpHelper::class.java)

    private const val HEADER_USER_AGENT = "User-Agent"
    private const val HEADER_AUTHORIZATION = "Authorization"

    fun provideOkHttpClient(
        clientConfiguration: ClientConfiguration,
        authentication: Authentication
    ): OkHttpClient {

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                chain.proceed(
                    request.newBuilder().apply {
                        // User agent
                        header(HEADER_USER_AGENT, clientConfiguration.userAgent)

                        // Auth token, if present
                        if (request.header(HEADER_AUTHORIZATION) == null) {
                            header(HEADER_AUTHORIZATION, "${authentication.login}:${authentication.secretKey}")
                        }

                        val urlBuilder = request.url.newBuilder()

                        // Use mock server, if configured
                        clientConfiguration.httpConfiguration.mockServerBaserUri?.let {
                            urlBuilder
                                .scheme(it.scheme)
                                .host(it.host)
                                .port(it.port)
                        }
                    }
                        .build()
                )
            }

            // Logging
            .addInterceptor(
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        LOGGER.trace(message)
                    }
                }).apply { level = clientConfiguration.httpConfiguration.loggingLevel.okHttpLevel }
            )

        // Proxy, if configured
        clientConfiguration.httpConfiguration.httpProxy?.let {
            clientBuilder.proxy(
                Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(it.host, it.port)
                )
            )
        }

        return clientBuilder.build()
    }
}