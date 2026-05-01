package com.flowledger.app.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {

    fun format(amountInCents: Long, currencyCode: String = "CNY"): String {
        val amount = amountInCents / 100.0
        return when (currencyCode) {
            "CNY" -> formatCNY(amount)
            "USD" -> formatUSD(amount)
            else -> {
                val currency = Currency.getInstance(currencyCode)
                val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                format.currency = currency
                format.format(amount)
            }
        }
    }

    private fun formatCNY(amount: Double): String {
        return "¥${"%.2f".format(amount)}"
    }

    private fun formatUSD(amount: Double): String {
        return "$${"%.2f".format(amount)}"
    }

    fun formatAmount(amountInCents: Long): String {
        return "%.2f".format(amountInCents / 100.0)
    }
}
