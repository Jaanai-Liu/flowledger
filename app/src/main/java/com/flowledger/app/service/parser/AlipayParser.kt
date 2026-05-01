package com.flowledger.app.service.parser

class AlipayParser : NotificationParser {

    private val titlePatterns = listOf(
        Regex("支付成功"),
        Regex("付款成功"),
        Regex("交易成功"),
        Regex("转账成功")
    )

    override fun supports(packageName: String, title: String, content: String): Boolean {
        if (packageName != "com.eg.android.AlipayGphone") return false
        return titlePatterns.any { it.containsMatchIn(title) }
    }

    override fun parse(packageName: String, text: String): ParsedResult? {
        val amount = NotificationParser.extractAmount(text) ?: return null
        val merchant = NotificationParser.extractMerchant(text)
        return ParsedResult(
            amount = amount,
            merchant = merchant,
            paymentMethod = "支付宝"
        )
    }
}
