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

package org.jraf.klibqonto.internal.api.model.pagination

import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination

internal object HasApiMetaConverter {
    fun <MODEL : Any> convert(hasApiMeta: HasApiMeta, list: List<MODEL>): Page<MODEL> {
        return Page(
            items = list,
            pageIndex = hasApiMeta.meta.current_page,
            nextPagination = hasApiMeta.meta.next_page?.let { Pagination(it, hasApiMeta.meta.per_page) },
            previousPagination = hasApiMeta.meta.prev_page?.let { Pagination(it, hasApiMeta.meta.per_page) },
            totalPages = hasApiMeta.meta.total_pages,
            totalItems = hasApiMeta.meta.total_count
        )
    }
}
