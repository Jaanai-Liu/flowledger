package com.flowledger.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: AccountType = AccountType.CHECKING,
    @ColumnInfo(name = "currency") val currency: String = "CNY",
    @ColumnInfo(name = "initial_balance") val initialBalance: Long = 0,
    @ColumnInfo(name = "icon") val icon: String = "account_balance",
    @ColumnInfo(name = "color") val color: Long = 0xFF1976D2,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

enum class AccountType { CHECKING, CREDIT, SAVINGS, E_WALLET }
