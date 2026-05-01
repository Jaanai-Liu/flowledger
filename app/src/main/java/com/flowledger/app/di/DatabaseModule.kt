package com.flowledger.app.di

import android.content.Context
import com.flowledger.app.data.local.FlowLedgerDatabase
import com.flowledger.app.data.local.dao.AccountDao
import com.flowledger.app.data.local.dao.CategoryDao
import com.flowledger.app.data.local.dao.MerchantKeywordDao
import com.flowledger.app.data.local.dao.NotificationTemplateDao
import com.flowledger.app.data.local.dao.PaymentMethodDao
import com.flowledger.app.data.local.dao.RecurringRuleDao
import com.flowledger.app.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlowLedgerDatabase {
        return FlowLedgerDatabase.getInstance(context)
    }

    @Provides
    fun provideTransactionDao(db: FlowLedgerDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: FlowLedgerDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideAccountDao(db: FlowLedgerDatabase): AccountDao = db.accountDao()

    @Provides
    fun providePaymentMethodDao(db: FlowLedgerDatabase): PaymentMethodDao = db.paymentMethodDao()

    @Provides
    fun provideRecurringRuleDao(db: FlowLedgerDatabase): RecurringRuleDao = db.recurringRuleDao()

    @Provides
    fun provideNotificationTemplateDao(db: FlowLedgerDatabase): NotificationTemplateDao =
        db.notificationTemplateDao()

    @Provides
    fun provideMerchantKeywordDao(db: FlowLedgerDatabase): MerchantKeywordDao =
        db.merchantKeywordDao()
}
