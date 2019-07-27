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

package org.jraf.klibqonto.internal.client

import io.reactivex.Single
import org.jraf.klibqonto.client.ClientConfiguration
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.internal.api.OkHttpHelper
import org.jraf.klibqonto.internal.api.model.organizations.ApiOrganizationEnvelopeConverter
import org.jraf.klibqonto.internal.client.QontoRetrofitService.Companion.BASE_URL
import org.jraf.klibqonto.model.organizations.Organization
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal class QontoClientImpl(
    private val clientConfiguration: ClientConfiguration
) : QontoClient,
    QontoClient.Organizations {
    override val organizations = this

    private val service: QontoRetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpHelper.provideOkHttpClient(clientConfiguration, clientConfiguration.authentication))
            .build()
            .create(QontoRetrofitService::class.java)
    }


    override fun getOrganization(): Single<Organization> {
        return service.getOrganization()
            .map { ApiOrganizationEnvelopeConverter.convert(it) }
    }
}