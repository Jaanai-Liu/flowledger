package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.RecurringRuleDao
import com.flowledger.app.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringRuleRepository @Inject constructor(
    private val dao: RecurringRuleDao
) {
    fun getAll(): Flow<List<RecurringRuleEntity>> = dao.getAll()

    fun getActiveRules(): Flow<List<RecurringRuleEntity>> = dao.getActiveRules()

    suspend fun getActiveRulesDueBefore(todayEpochDay: Long): List<RecurringRuleEntity> =
        dao.getActiveRulesDueBefore(todayEpochDay)

    suspend fun getById(id: Long): RecurringRuleEntity? = dao.getById(id)

    suspend fun insert(rule: RecurringRuleEntity): Long = dao.insert(rule)

    suspend fun update(rule: RecurringRuleEntity) = dao.update(rule)

    suspend fun delete(rule: RecurringRuleEntity) = dao.delete(rule)

    suspend fun updateLastGenerated(ruleId: Long, date: Long) =
        dao.updateLastGenerated(ruleId, date)
}
