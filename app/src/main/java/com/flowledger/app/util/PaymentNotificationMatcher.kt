package com.flowledger.app.util

import com.flowledger.app.data.local.entity.MerchantKeywordEntity
import com.flowledger.app.data.local.entity.NotificationTemplateEntity

object PaymentNotificationMatcher {

    fun matchTemplate(
        packageName: String,
        templates: List<NotificationTemplateEntity>
    ): NotificationTemplateEntity? {
        return templates.firstOrNull {
            it.appPackage == packageName && it.isActive
        }
    }

    fun matchTitle(
        title: String,
        template: NotificationTemplateEntity
    ): Boolean {
        return try {
            Regex(template.titleRegex).containsMatchIn(title)
        } catch (e: Exception) {
            false
        }
    }

    fun matchContent(
        content: String,
        template: NotificationTemplateEntity
    ): Pair<Long?, String?>? {
        return try {
            val regex = Regex(template.contentRegex)
            val match = regex.find(content) ?: return null

            val amountStr = match.groups[template.amountGroup]?.value
            val amount = amountStr?.let {
                try {
                    (it.toDouble() * 100).toLong()
                } catch (e: NumberFormatException) {
                    null
                }
            }

            val merchant = template.merchantGroup?.let {
                match.groups[it]?.value?.trim()
            }

            if (amount != null && amount > 0) {
                Pair(amount, merchant)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun matchMerchantKeyword(
        merchantName: String,
        keywords: List<MerchantKeywordEntity>
    ): MerchantKeywordEntity? {
        return keywords
            .sortedByDescending { it.priority }
            .firstOrNull { kw ->
                merchantName.contains(kw.keyword, ignoreCase = true)
            }
    }
}
