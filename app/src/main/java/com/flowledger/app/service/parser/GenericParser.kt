package com.flowledger.app.service.parser

class GenericParser : NotificationParser {

    private val bankPackages = setOf(
        "com.android.bankabc",   // 农业银行
        "com.icbc",              // 工商银行
        "com.chinamworld.boc",   // 中国银行
        "com.chinamworld.bocmbci",
        "cmb.pb",                // 招商银行
        "com.ccb.longjiLife"     // 建设银行
    )

    private val titlePatterns = listOf(
        Regex("交易提醒"),
        Regex("支付成功"),
        Regex("扣款"),
        Regex("消费"),
        Regex("支出")
    )

    override fun supports(packageName: String, title: String, content: String): Boolean {
        if (packageName in bankPackages) return true
        return titlePatterns.any { it.containsMatchIn(title) } &&
                Regex("""[¥￥元]""").containsMatchIn(content)
    }

    override fun parse(packageName: String, text: String): ParsedResult? {
        val amount = NotificationParser.extractAmount(text) ?: return null
        val merchant = NotificationParser.extractMerchant(text)
        return ParsedResult(
            amount = amount,
            merchant = merchant,
            paymentMethod = null
        )
    }
}
