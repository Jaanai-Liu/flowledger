package com.flowledger.app.service.parser

data class ParsedResult(
    val amount: Long,       // in cents
    val merchant: String?,  // merchant name
    val paymentMethod: String? // parsed payment method
)

interface NotificationParser {
    fun supports(packageName: String, title: String, content: String): Boolean
    fun parse(packageName: String, text: String): ParsedResult?

    companion object {
        fun extractAmount(text: String): Long? {
            val patterns = listOf(
                Regex("""[¥￥]\s*(\d+(?:\.\d{1,2})?)"""),
                Regex("""(\d+(?:\.\d{1,2})?)\s*元"""),
                Regex("""(\d+(?:\.\d{1,2})?)""")
            )
            for (pattern in patterns) {
                val match = pattern.find(text)
                if (match != null) {
                    val amountStr = match.groupValues[1]
                    val amount = try {
                        (amountStr.toDouble() * 100).toLong()
                    } catch (e: NumberFormatException) {
                        null
                    }
                    if (amount != null && amount > 0 && amount < 1_000_000_00L) {
                        return amount
                    }
                }
            }
            return null
        }

        fun extractMerchant(text: String): String? {
            val patterns = listOf(
                Regex("""商品[：:]\s*(.+?)(?:\n|，|。)"""),
                Regex("""商户[：:名称]*\s*(.+?)(?:\n|，|。)"""),
                Regex("""商品说明[：:]\s*(.+?)(?:\n|，|。)"""),
                Regex("""收款方[：:]\s*(.+?)(?:\n|，|。)"""),
                Regex("""对方[：:]\s*(.+?)(?:\n|，|。)""")
            )
            for (pattern in patterns) {
                val match = pattern.find(text)
                if (match != null) {
                    val merchant = match.groupValues[1].trim()
                    if (merchant.isNotBlank() && merchant.length < 50) {
                        return merchant.take(30)
                    }
                }
            }
            return null
        }
    }
}
