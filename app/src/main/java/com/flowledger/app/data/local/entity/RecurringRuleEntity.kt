package com.flowledger.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_rules",
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
        )
    ],
    indices = [
        Index("category_id"),
        Index("account_id"),
        Index("payment_method_id")
    ]
)
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "type") val type: TransactionType,
    @ColumnInfo(name = "amount") val amount: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "payment_method_id") val paymentMethodId: Long? = null,
    @ColumnInfo(name = "frequency") val frequency: RecurringFrequency,
    @ColumnInfo(name = "day_of_month") val dayOfMonth: Int? = null,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int? = null,
    @ColumnInfo(name = "note_template") val noteTemplate: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "last_generated_date") val lastGeneratedDate: Long? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

enum class RecurringFrequency { MONTHLY, WEEKLY, YEARLY }
