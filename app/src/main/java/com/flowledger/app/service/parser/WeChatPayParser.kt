package com.flowledger.app.service.parser

class WeChatPayParser : NotificationParser {

    private val titlePatterns = listOf(
        Regex("微信支付"),
        Regex("支付成功"),
        Regex("微信支付凭证")
    )

    override fun supports(packageName: String, title: String, content: String): Boolean {
        if (packageName != "com.tencent.mm") return false
        return titlePatterns.any { it.containsMatchIn(title) }
    }

    override fun parse(packageName: String, text: String): ParsedResult? {
        val amount = NotificationParser.extractAmount(text) ?: return null
        val merchant = NotificationParser.extractMerchant(text)
        return ParsedResult(
            amount = amount,
            merchant = merchant,
            paymentMethod = "微信支付"
        )
    }
}
