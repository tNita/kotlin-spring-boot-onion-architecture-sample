package com.example.bookmanager.support.db

import org.jooq.DSLContext

/**
 * テーブル全削除用のユーティリティ。
 */
fun DSLContext.deleteAllTables() {
    // Truncate with CASCADE to handle FK constraints; restart identity for deterministic IDs if sequences exist.
    execute("truncate table book_authors, books, authors restart identity cascade")
}
