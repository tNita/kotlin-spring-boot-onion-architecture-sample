package com.example.bookmanager.domain

import kotlin.jvm.JvmInline

/**
 * 著者名の値オブジェクト。
 */
@JvmInline
value class AuthorName private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 255

        fun of(raw: String): AuthorName {
            val normalized = raw.trim()
            if (normalized.isEmpty()) {
                throw DomainException(DomainErrorCode.INVALID_AUTHOR_NAME, "Author name must not be blank")
            }
            if (normalized.length > MAX_LENGTH) {
                throw DomainException(DomainErrorCode.AUTHOR_NAME_TOO_LONG, "Author name must be $MAX_LENGTH characters or less")
            }
            return AuthorName(normalized)
        }
    }
}
