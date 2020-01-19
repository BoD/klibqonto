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

package org.jraf.klibqonto.model.pagination

/**
 * A page of results.
 */
data class Page<T : Any>(
    /**
     * The actual items of the results.
     */
    val items: List<T>,

    /**
     * Index of this page.
     *
     * Note: page indices are 1 based (the first page is 1).
     */
    val pageIndex: Int,

    /**
     * Next [Pagination] (if any), that can be used to retrieve the next page.
     */
    val nextPagination: Pagination?,

    /**
     * Previous [Pagination] (if any), that can be used to retrieve the previous page.
     */
    val previousPagination: Pagination?,

    /**
     * Total number of pages (depends on the number of items per page).
     */
    val totalPages: Int,

    /**
     * Total number of items.
     */
    val totalItems: Int
)
