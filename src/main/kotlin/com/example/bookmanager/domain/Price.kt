package com.example.bookmanager.domain

import java.math.BigDecimal
import kotlin.jvm.JvmInline

/**
 * 価格の値オブジェクト。0以上を保証する。
 */
@JvmInline
value class Price private constructor(val amount: BigDecimal) {
    companion object {
        val ZERO: Price = Price(BigDecimal.ZERO)

        fun of(amount: BigDecimal): Price {
            if (amount < BigDecimal.ZERO) {
                throw DomainException(DomainErrorCode.PRICE_NEGATIVE, "Price must be equal or greater than 0")
            }
            return Price(amount)
        }

        fun of(amount: Long): Price = of(BigDecimal.valueOf(amount))
        fun of(amount: Int): Price = of(amount.toLong())
    }
}
