package com.flowledger.app.util

import android.content.Context
import android.net.Uri
import com.flowledger.app.data.local.entity.TransactionEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val transactions: List<TransactionBackup> = emptyList()
)

@Serializable
data class TransactionBackup(
    val type: String,
    val amount: Long,
    val currency: String,
    val categoryId: Long,
    val accountId: Long,
    val paymentMethodId: Long? = null,
    val date: Long,
    val note: String? = null,
    val source: String,
    val status: String
)

object JsonBackupManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun exportToJson(transactions: List<TransactionEntity>): String {
        val backupData = BackupData(
            transactions = transactions.map { t ->
                TransactionBackup(
                    type = t.type.name,
                    amount = t.amount,
                    currency = t.currency,
                    categoryId = t.categoryId,
                    accountId = t.accountId,
                    paymentMethodId = t.paymentMethodId,
                    date = t.date,
                    note = t.note,
                    source = t.source.name,
                    status = t.status.name
                )
            }
        )
        return json.encodeToString(backupData)
    }

    fun importFromJson(jsonString: String): List<TransactionEntity> {
        val backupData = json.decodeFromString<BackupData>(jsonString)
        return backupData.transactions.map { t ->
            TransactionEntity(
                type = com.flowledger.app.data.local.entity.TransactionType.valueOf(t.type),
                amount = t.amount,
                currency = t.currency,
                categoryId = t.categoryId,
                accountId = t.accountId,
                paymentMethodId = t.paymentMethodId,
                date = t.date,
                note = t.note,
                source = com.flowledger.app.data.local.entity.TransactionSource.valueOf(t.source),
                status = com.flowledger.app.data.local.entity.TransactionStatus.valueOf(t.status)
            )
        }
    }

    fun writeToUri(context: Context, uri: Uri, jsonString: String) {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            output.write(jsonString.toByteArray(Charsets.UTF_8))
        }
    }

    fun readFromUri(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        } ?: throw IllegalStateException("无法读取文件")
    }
}
