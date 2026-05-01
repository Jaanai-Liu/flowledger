package com.flowledger.app.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionSource
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.data.repository.NotificationTemplateRepository
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.service.parser.NotificationParser
import com.flowledger.app.service.parser.WeChatPayParser
import com.flowledger.app.service.parser.AlipayParser
import com.flowledger.app.service.parser.UnionPayParser
import com.flowledger.app.service.parser.GenericParser
import com.flowledger.app.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationMonitorService : NotificationListenerService() {

    @Inject
    lateinit var transactionRepo: TransactionRepository

    @Inject
    lateinit var templateRepo: NotificationTemplateRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val parsers: List<NotificationParser> by lazy {
        listOf(
            WeChatPayParser(),
            AlipayParser(),
            UnionPayParser(),
            GenericParser()
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val fullText = listOf(title, content).joinToString("\n")

        if (fullText.isBlank()) return

        // Find matching parser
        val parser = parsers.firstOrNull { it.supports(packageName, title, content) }
            ?: return

        // Extract payment info
        val result = parser.parse(packageName, fullText)
        if (result == null || result.amount <= 0) return

        serviceScope.launch {
            // Duplicate detection
            val today = DateUtils.todayEpochDay()
            val duplicates = transactionRepo.findDuplicates(
                date = today,
                amount = result.amount,
                merchant = result.merchant
            )
            if (duplicates.isNotEmpty()) return@launch

            // Determine category via merchant keyword matching
            val merchantKeyword = result.merchant?.let { templateRepo.matchMerchant(it) }
            val categoryId = merchantKeyword?.categoryId ?: 10L // Default: "其他支出" (id = 10 in seed data)

            // Determine payment method from package
            val paymentMethodId = when (packageName) {
                "com.tencent.mm" -> 1L  // 微信支付
                "com.eg.android.AlipayGphone" -> 2L  // 支付宝
                "com.unionpay" -> 3L  // 云闪付
                else -> null
            }

            val transaction = TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = result.amount,
                categoryId = categoryId,
                accountId = 1L, // Default account
                paymentMethodId = paymentMethodId,
                date = today,
                note = result.merchant ?: "自动记账",
                source = TransactionSource.AUTO_NOTIFICATION,
                status = TransactionStatus.CONFIRMED,
                merchantName = result.merchant,
                rawNotification = fullText
            )
            transactionRepo.insert(transaction)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
