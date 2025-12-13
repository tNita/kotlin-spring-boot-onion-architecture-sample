package com.example.bookmanager.domain.query

import java.time.LocalDate
import java.util.UUID

/**
 * 参照系で返す読み取り専用の著者モデル。
 */
data class AuthorView(
    val id: UUID,
    val name: String,
    val birthDate: LocalDate
)
