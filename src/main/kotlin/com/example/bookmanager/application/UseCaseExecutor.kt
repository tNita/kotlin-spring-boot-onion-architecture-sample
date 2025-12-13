package com.example.bookmanager.application

import com.example.bookmanager.domain.DomainException

/**
 * ドメイン層の例外をユースケース境界で必ず ApplicationException に変換するラッパー。
 * プレゼンテーション層に DomainException を漏らさないため、各 UseCase の public API はこの関数で包む。
 */
inline fun <T> runUseCase(block: () -> T): T =
    try {
        block()
    } catch (ex: DomainException) {
        throw ApplicationException(ApplicationErrorCode.INVALID_REQUEST, ex.message)
    }
