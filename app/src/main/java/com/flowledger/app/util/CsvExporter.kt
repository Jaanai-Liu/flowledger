package com.flowledger.app.util

import android.content.Context
import android.net.Uri
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionType
import java.io.OutputStreamWriter

object CsvExporter {

    fun export(context: Context, uri: Uri, transactions: List<TransactionEntity>) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                writer.write("﻿") // BOM for Excel
                writer.write("日期,类型,金额,分类ID,账户ID,支付方式ID,备注,来源,状态\n")
                transactions.forEach { t ->
                    val type = if (t.type == TransactionType.INCOME) "收入" else "支出"
                    val source = when (t.source) {
                        com.flowledger.app.data.local.entity.TransactionSource.MANUAL -> "手动"
                        com.flowledger.app.data.local.entity.TransactionSource.AUTO_NOTIFICATION -> "自动抓取"
                        com.flowledger.app.data.local.entity.TransactionSource.AUTO_RECURRING -> "自动周期"
                    }
                    val status = if (t.status == com.flowledger.app.data.local.entity.TransactionStatus.CONFIRMED) "已确认" else "待确认"
                    val amount = CurrencyFormatter.formatAmount(t.amount)
                    writer.write("${DateUtils.formatDate(t.date)},$type,$amount,${t.categoryId},${t.accountId},${t.paymentMethodId ?: ""},${t.note ?: ""},$source,$status\n")
                }
            }
        }
    }
}
