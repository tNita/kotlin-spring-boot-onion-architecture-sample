package com.example.bookmanager.domain

/**
 * 書籍の出版状況。
 */
enum class PublishStatus {
    UNPUBLISHED,
    PUBLISHED;

    fun canTransitionTo(target: PublishStatus): Boolean {
        if (this == PUBLISHED && target == UNPUBLISHED) return false
        return true
    }
}
