package com.flowledger.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flowledger.app.data.local.converter.Converters
import com.flowledger.app.data.local.dao.AccountDao
import com.flowledger.app.data.local.dao.CategoryDao
import com.flowledger.app.data.local.dao.MerchantKeywordDao
import com.flowledger.app.data.local.dao.NotificationTemplateDao
import com.flowledger.app.data.local.dao.PaymentMethodDao
import com.flowledger.app.data.local.dao.RecurringRuleDao
import com.flowledger.app.data.local.dao.TransactionDao
import com.flowledger.app.data.local.entity.AccountEntity
import com.flowledger.app.data.local.entity.AccountType
import com.flowledger.app.data.local.entity.CategoryEntity
import com.flowledger.app.data.local.entity.MerchantKeywordEntity
import com.flowledger.app.data.local.entity.NotificationTemplateEntity
import com.flowledger.app.data.local.entity.PaymentMethodEntity
import com.flowledger.app.data.local.entity.RecurringRuleEntity
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        PaymentMethodEntity::class,
        RecurringRuleEntity::class,
        NotificationTemplateEntity::class,
        MerchantKeywordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlowLedgerDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun notificationTemplateDao(): NotificationTemplateDao
    abstract fun merchantKeywordDao(): MerchantKeywordDao

    companion object {
        @Volatile
        private var INSTANCE: FlowLedgerDatabase? = null

        fun getInstance(context: Context): FlowLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): FlowLedgerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FlowLedgerDatabase::class.java,
                "flowledger.db"
            )
                .addCallback(SeedCallback())
                .build()
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedData(database)
                }
            }
        }

        private suspend fun seedData(db: FlowLedgerDatabase) {
            seedCategories(db)
            seedPaymentMethods(db)
            seedAccounts(db)
            seedNotificationTemplates(db)
            seedMerchantKeywords(db)
        }

        private suspend fun seedCategories(db: FlowLedgerDatabase) {
            val expenseCategories = listOf(
                CategoryEntity(name = "餐饮", type = TransactionType.EXPENSE, icon = "restaurant", color = 0xFFE91E63, sortOrder = 0, isSystem = true),
                CategoryEntity(name = "交通", type = TransactionType.EXPENSE, icon = "directions_car", color = 0xFF2196F3, sortOrder = 1, isSystem = true),
                CategoryEntity(name = "住房", type = TransactionType.EXPENSE, icon = "home", color = 0xFFFF9800, sortOrder = 2, isSystem = true),
                CategoryEntity(name = "购物", type = TransactionType.EXPENSE, icon = "shopping_bag", color = 0xFF9C27B0, sortOrder = 3, isSystem = true),
                CategoryEntity(name = "娱乐", type = TransactionType.EXPENSE, icon = "sports_esports", color = 0xFF00BCD4, sortOrder = 4, isSystem = true),
                CategoryEntity(name = "医疗", type = TransactionType.EXPENSE, icon = "local_hospital", color = 0xFF4CAF50, sortOrder = 5, isSystem = true),
                CategoryEntity(name = "教育", type = TransactionType.EXPENSE, icon = "school", color = 0xFF3F51B5, sortOrder = 6, isSystem = true),
                CategoryEntity(name = "通讯", type = TransactionType.EXPENSE, icon = "phone", color = 0xFF607D8B, sortOrder = 7, isSystem = true),
                CategoryEntity(name = "服饰", type = TransactionType.EXPENSE, icon = "checkroom", color = 0xFF795548, sortOrder = 8, isSystem = true),
                CategoryEntity(name = "其他支出", type = TransactionType.EXPENSE, icon = "more_horiz", color = 0xFF9E9E9E, sortOrder = 99, isSystem = true),
            )
            val incomeCategories = listOf(
                CategoryEntity(name = "工资", type = TransactionType.INCOME, icon = "work", color = 0xFF4CAF50, sortOrder = 0, isSystem = true),
                CategoryEntity(name = "奖金", type = TransactionType.INCOME, icon = "emoji_events", color = 0xFFFFC107, sortOrder = 1, isSystem = true),
                CategoryEntity(name = "投资收益", type = TransactionType.INCOME, icon = "trending_up", color = 0xFF2196F3, sortOrder = 2, isSystem = true),
                CategoryEntity(name = "兼职", type = TransactionType.INCOME, icon = "handyman", color = 0xFFFF9800, sortOrder = 3, isSystem = true),
                CategoryEntity(name = "红包", type = TransactionType.INCOME, icon = "redeem", color = 0xFFE91E63, sortOrder = 4, isSystem = true),
                CategoryEntity(name = "其他收入", type = TransactionType.INCOME, icon = "more_horiz", color = 0xFF9E9E9E, sortOrder = 99, isSystem = true),
            )
            db.categoryDao().insertAll(expenseCategories + incomeCategories)
        }

        private suspend fun seedPaymentMethods(db: FlowLedgerDatabase) {
            val methods = listOf(
                PaymentMethodEntity(name = "微信支付", icon = "wechat", isSystem = true, sortOrder = 0),
                PaymentMethodEntity(name = "支付宝", icon = "alipay", isSystem = true, sortOrder = 1),
                PaymentMethodEntity(name = "云闪付", icon = "unionpay", isSystem = true, sortOrder = 2),
                PaymentMethodEntity(name = "银行卡", icon = "credit_card", isSystem = true, sortOrder = 3),
                PaymentMethodEntity(name = "现金", icon = "payments", isSystem = true, sortOrder = 4),
            )
            db.paymentMethodDao().insertAll(methods)
        }

        private suspend fun seedAccounts(db: FlowLedgerDatabase) {
            db.accountDao().insert(
                AccountEntity(
                    name = "默认账户",
                    type = AccountType.CHECKING,
                    currency = "CNY",
                    initialBalance = 0
                )
            )
        }

        private suspend fun seedNotificationTemplates(db: FlowLedgerDatabase) {
            val templates = listOf(
                NotificationTemplateEntity(
                    appPackage = "com.tencent.mm",
                    appName = "微信",
                    titleRegex = "(微信支付|支付成功|微信支付凭证)",
                    contentRegex = "¥\\s*(?<amount>\\d+(?:\\.\\d{1,2})?)",
                    amountGroup = "amount",
                ),
                NotificationTemplateEntity(
                    appPackage = "com.eg.android.AlipayGphone",
                    appName = "支付宝",
                    titleRegex = "(支付成功|付款成功|交易成功|转账成功)",
                    contentRegex = "[¥￥]\\s*(?<amount>\\d+(?:\\.\\d{1,2})?)",
                    amountGroup = "amount",
                ),
                NotificationTemplateEntity(
                    appPackage = "com.unionpay",
                    appName = "云闪付",
                    titleRegex = "(支付成功|交易提醒|付款成功)",
                    contentRegex = "[¥￥]\\s*(?<amount>\\d+(?:\\.\\d{1,2})?)",
                    amountGroup = "amount",
                ),
            )
            db.notificationTemplateDao().insertAll(templates)
        }

        private suspend fun seedMerchantKeywords(db: FlowLedgerDatabase) {
            val expenseCategoryId = 1L
            val transportCategoryId = 2L
            val housingCategoryId = 3L
            val shoppingCategoryId = 4L
            val entertainmentCategoryId = 5L
            val medicalCategoryId = 6L
            val educationCategoryId = 7L
            val commCategoryId = 8L
            val clothingCategoryId = 9L

            val mappings = listOf(
                MerchantKeywordEntity(keyword = "麦当劳", categoryId = expenseCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "肯德基", categoryId = expenseCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "星巴克", categoryId = expenseCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "美团", categoryId = expenseCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "饿了么", categoryId = expenseCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "滴滴", categoryId = transportCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "铁路", categoryId = transportCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "航空", categoryId = transportCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "地铁", categoryId = transportCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "物业", categoryId = housingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "房租", categoryId = housingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "电费", categoryId = housingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "水费", categoryId = housingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "燃气", categoryId = housingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "京东", categoryId = shoppingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "淘宝", categoryId = shoppingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "拼多多", categoryId = shoppingCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "影院", categoryId = entertainmentCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "猫眼", categoryId = entertainmentCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "游戏", categoryId = entertainmentCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "医院", categoryId = medicalCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "药房", categoryId = medicalCategoryId, priority = 10),
                MerchantKeywordEntity(keyword = "书店", categoryId = educationCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "移动", categoryId = commCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "联通", categoryId = commCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "电信", categoryId = commCategoryId, priority = 8),
                MerchantKeywordEntity(keyword = "优衣库", categoryId = clothingCategoryId, priority = 10),
            )
            db.merchantKeywordDao().insertAll(mappings)
        }
    }
}
