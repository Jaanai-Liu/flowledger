package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.MerchantKeywordDao
import com.flowledger.app.data.local.dao.NotificationTemplateDao
import com.flowledger.app.data.local.entity.MerchantKeywordEntity
import com.flowledger.app.data.local.entity.NotificationTemplateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTemplateRepository @Inject constructor(
    private val templateDao: NotificationTemplateDao,
    private val merchantKeywordDao: MerchantKeywordDao
) {
    fun getAllTemplates(): Flow<List<NotificationTemplateEntity>> = templateDao.getAll()

    suspend fun getTemplateByPackage(packageName: String): NotificationTemplateEntity? =
        templateDao.getByPackage(packageName)

    suspend fun matchMerchant(merchantName: String): MerchantKeywordEntity? =
        merchantKeywordDao.matchMerchant(merchantName)

    fun getAllMerchantKeywords(): Flow<List<MerchantKeywordEntity>> = merchantKeywordDao.getAll()

    suspend fun insertMerchantKeyword(keyword: MerchantKeywordEntity): Long =
        merchantKeywordDao.insert(keyword)

    suspend fun updateTemplate(template: NotificationTemplateEntity) = templateDao.update(template)
}
