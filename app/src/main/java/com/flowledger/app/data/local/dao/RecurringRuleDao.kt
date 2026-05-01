package com.flowledger.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.flowledger.app.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRuleEntity): Long

    @Update
    suspend fun update(rule: RecurringRuleEntity)

    @Delete
    suspend fun delete(rule: RecurringRuleEntity)

    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    suspend fun getById(id: Long): RecurringRuleEntity?

    @Query("SELECT * FROM recurring_rules ORDER BY created_at DESC")
    fun getAll(): Flow<List<RecurringRuleEntity>>

    @Query("SELECT * FROM recurring_rules WHERE is_active = 1 ORDER BY created_at DESC")
    fun getActiveRules(): Flow<List<RecurringRuleEntity>>

    @Query("""
        SELECT * FROM recurring_rules
        WHERE is_active = 1
        AND (last_generated_date IS NULL OR last_generated_date < :todayEpochDay)
    """)
    suspend fun getActiveRulesDueBefore(todayEpochDay: Long): List<RecurringRuleEntity>

    @Query("UPDATE recurring_rules SET last_generated_date = :date WHERE id = :ruleId")
    suspend fun updateLastGenerated(ruleId: Long, date: Long)
}
