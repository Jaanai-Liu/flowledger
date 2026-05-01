package com.flowledger.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PaymentMethodEntity::class,
            parentColumns = ["id"],
            childColumns = ["payment_method_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RecurringRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurring_rule_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("category_id"),
        Index("account_id"),
        Index("payment_method_id"),
        Index("recurring_rule_id"),
        Index("date"),
        Index("type"),
        Index("source")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "type") val type: TransactionType,
    @ColumnInfo(name = "amount") val amount: Long,
    @ColumnInfo(name = "currency") val currency: String = "CNY",
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "payment_method_id") val paymentMethodId: Long? = null,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "source") val source: TransactionSource = TransactionSource.MANUAL,
    @ColumnInfo(name = "status") val status: TransactionStatus = TransactionStatus.CONFIRMED,
    @ColumnInfo(name = "recurring_rule_id") val recurringRuleId: Long? = null,
    @ColumnInfo(name = "raw_notification") val rawNotification: String? = null,
    @ColumnInfo(name = "merchant_name") val merchantName: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

enum class TransactionType { INCOME, EXPENSE }

enum class TransactionSource { MANUAL, AUTO_NOTIFICATION, AUTO_RECURRING }

enum class TransactionStatus { CONFIRMED, PENDING }
