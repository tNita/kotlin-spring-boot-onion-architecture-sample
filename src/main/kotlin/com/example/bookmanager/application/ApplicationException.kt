package com.example.bookmanager.application

/**
 * アプリケーション層のエラーコード。プレゼンテーション層でのレスポンス変換に利用する。
 */
enum class ApplicationErrorCode {
    AUTHOR_NOT_FOUND,
    BOOK_NOT_FOUND,
    BOOK_ID_MISSING,
    INVALID_UPDATE_REQUEST
}

class ApplicationException(
    val code: ApplicationErrorCode,
    override val message: String
) : RuntimeException(message)
