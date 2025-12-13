package com.example.bookmanager.domain.query

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.AuthorName

/**
 * 参照専用の著者リポジトリ。
 */
interface AuthorQueryRepository {
    fun findById(id: AuthorId): AuthorView?
    fun findByName(name: AuthorName): List<AuthorView>
}
