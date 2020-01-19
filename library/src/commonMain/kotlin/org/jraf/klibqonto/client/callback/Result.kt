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

@file:Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate")

package org.jraf.klibqonto.client.callback

/**
 * **Note: this class is a copy of [Kotlin's Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html),
 * with `inline` removed and simplified to make it compatible with Kotlin/Native targets.**
 *
 * A discriminated union that encapsulates successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 */
class Result<T : Any>(
    internal val value: Any?
) {
    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value !is Failure

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][Result.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        when (value) {
            is Failure -> value.toString()
            else -> "Success($value)"
        }

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T : Any> success(value: T): Result<T> =
            Result(value)

        /**
         * Returns an instance that encapsulates the given [exception] as failure.
         */
        fun <T : Any> failure(exception: Throwable): Result<T> =
            Result(
                createFailure(exception)
            )
    }

    internal class Failure(
        val exception: Throwable
    ) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or throws the encapsulated exception
     * if it is [failure][Result.isFailure].
     *
     * This function is shorthand for `getOrElse { throw it }` (see [getOrElse]).
     */
    fun getOrThrow(): T {
        throwOnFailure()
        return value as T
    }

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
     * result of [onFailure] function for encapsulated exception if it is [failure][Result.isFailure].
     *
     * Note, that an exception thrown by [onFailure] function is rethrown by this function.
     *
     * This function is shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
     */
    fun getOrElse(onFailure: (exception: Throwable) -> T): T {
        return when (val exception = exceptionOrNull()) {
            null -> value as T
            else -> onFailure(exception)
        }
    }

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
     * [defaultValue] if it is [failure][Result.isFailure].
     *
     * This function is shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
     */
    fun getOrDefault(defaultValue: T): T {
        if (isFailure) return defaultValue
        return value as T
    }

    /**
     * Returns the the result of [onSuccess] for encapsulated value if this instance represents [success][Result.isSuccess]
     * or the result of [onFailure] function for encapsulated exception if it is [failure][Result.isFailure].
     *
     * Note, that an exception thrown by [onSuccess] or by [onFailure] function is rethrown by this function.
     */
    fun <R> fold(
        onSuccess: (value: T) -> R,
        onFailure: (exception: Throwable) -> R
    ): R {
        return when (val exception = exceptionOrNull()) {
            null -> onSuccess(value as T)
            else -> onFailure(exception)
        }
    }

    /**
     * Returns the encapsulated result of the given [transform] function applied to encapsulated value
     * if this instance represents [success][Result.isSuccess] or the
     * original encapsulated exception if it is [failure][Result.isFailure].
     *
     * Note, that an exception thrown by [transform] function is rethrown by this function.
     * See [mapCatching] for an alternative that encapsulates exceptions.
     */
    fun <R : Any> map(transform: (value: T) -> R): Result<R> {
        return when {
            isSuccess -> success(transform(value as T))
            else -> Result(value)
        }
    }

    /**
     * Returns the encapsulated result of the given [transform] function applied to encapsulated value
     * if this instance represents [success][Result.isSuccess] or the
     * original encapsulated exception if it is [failure][Result.isFailure].
     *
     * Any exception thrown by [transform] function is caught, encapsulated as a failure and returned by this function.
     * See [map] for an alternative that rethrows exceptions.
     */
    fun <R : Any> mapCatching(transform: (value: T) -> R): Result<R> {
        return when {
            isSuccess -> runCatching { transform(value as T) }
            else -> Result(value)
        }
    }

    /**
     * Returns the encapsulated result of the given [transform] function applied to encapsulated exception
     * if this instance represents [failure][Result.isFailure] or the
     * original encapsulated value if it is [success][Result.isSuccess].
     *
     * Note, that an exception thrown by [transform] function is rethrown by this function.
     * See [recoverCatching] for an alternative that encapsulates exceptions.
     */
    fun recover(transform: (exception: Throwable) -> T): Result<T> {
        return when (val exception = exceptionOrNull()) {
            null -> this
            else -> success(transform(exception))
        }
    }

    /**
     * Returns the encapsulated result of the given [transform] function applied to encapsulated exception
     * if this instance represents [failure][Result.isFailure] or the
     * original encapsulated value if it is [success][Result.isSuccess].
     *
     * Any exception thrown by [transform] function is caught, encapsulated as a failure and returned by this function.
     * See [recover] for an alternative that rethrows exceptions.
     */
    fun recoverCatching(transform: (exception: Throwable) -> T): Result<T> {
        return when (val exception = exceptionOrNull()) {
            null -> this
            else -> runCatching { transform(exception) }
        }
    }

    /**
     * Performs the given [action] on encapsulated exception if this instance represents [failure][Result.isFailure].
     * Returns the original `Result` unchanged.
     */
    fun onFailure(action: (exception: Throwable) -> Unit): Result<T> {
        exceptionOrNull()?.let { action(it) }
        return this
    }

    /**
     * Performs the given [action] on encapsulated value if this instance represents [success][Result.isSuccess].
     * Returns the original `Result` unchanged.
     */
    fun onSuccess(action: (value: T) -> Unit): Result<T> {
        if (isSuccess) action(value as T)
        return this
    }

}

/**
 * Creates an instance of internal marker [Result.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
internal fun createFailure(exception: Throwable): Any =
    Result.Failure(exception)

/**
 * Throws exception if the result is failure. This internal function minimizes
 * inlined bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
internal fun Result<*>.throwOnFailure() {
    if (value is Result.Failure) throw value.exception
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
fun <R : Any> runCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result
 * if invocation was successful, catching and encapsulating any thrown exception as a failure.
 */
fun <T, R : Any> T.runCatching(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
suspend fun <R : Any> suspendRunCatching(block: suspend () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

