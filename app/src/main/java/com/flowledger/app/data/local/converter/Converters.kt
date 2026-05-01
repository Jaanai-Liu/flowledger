package com.flowledger.app.data.local.converter

import androidx.room.TypeConverter
import com.flowledger.app.data.local.entity.AccountType
import com.flowledger.app.data.local.entity.RecurringFrequency
import com.flowledger.app.data.local.entity.TransactionSource
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.local.entity.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromTransactionSource(value: TransactionSource): String = value.name

    @TypeConverter
    fun toTransactionSource(value: String): TransactionSource = TransactionSource.valueOf(value)

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String = value.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = TransactionStatus.valueOf(value)

    @TypeConverter
    fun fromAccountType(value: AccountType): String = value.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = AccountType.valueOf(value)

    @TypeConverter
    fun fromRecurringFrequency(value: RecurringFrequency): String = value.name

    @TypeConverter
    fun toRecurringFrequency(value: String): RecurringFrequency = RecurringFrequency.valueOf(value)
}
