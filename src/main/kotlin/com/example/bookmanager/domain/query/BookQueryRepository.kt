package com.example.bookmanager.domain.query

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.BookId

/**
 * 参照専用の書籍リポジトリ。
 * 読み取りに最適化したクエリモデルを返す。
 */
interface BookQueryRepository {
    fun findById(id: BookId): BookView?
    fun findByAuthorIds(authorIds: Collection<AuthorId>): List<BookView>
}
