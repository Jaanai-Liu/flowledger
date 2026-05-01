package com.flowledger.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flowledger.app.data.local.entity.MerchantKeywordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantKeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyword: MerchantKeywordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keywords: List<MerchantKeywordEntity>)

    @Query("SELECT * FROM merchant_keywords ORDER BY priority DESC")
    fun getAll(): Flow<List<MerchantKeywordEntity>>

    @Query("SELECT * FROM merchant_keywords WHERE :merchantName LIKE '%' || keyword || '%' ORDER BY priority DESC LIMIT 1")
    suspend fun matchMerchant(merchantName: String): MerchantKeywordEntity?
}
