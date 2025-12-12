package com.example.bookmanager.domain

@JvmInline
value class BookId private constructor(val value: Long) {
    companion object {
        fun of(value: Long): BookId {
            if (value <= 0) {
                throw DomainException(DomainErrorCode.INVALID_BOOK_ID, "BookId must be positive")
            }
            return BookId(value)
        }
    }
}
