package com.flowledger.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notification_templates",
    indices = [Index("app_package", unique = true)]
)
data class NotificationTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "app_package") val appPackage: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "title_regex") val titleRegex: String,
    @ColumnInfo(name = "content_regex") val contentRegex: String,
    @ColumnInfo(name = "amount_group") val amountGroup: String = "amount",
    @ColumnInfo(name = "merchant_group") val merchantGroup: String? = null,
    @ColumnInfo(name = "payment_method_group") val paymentMethodGroup: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
