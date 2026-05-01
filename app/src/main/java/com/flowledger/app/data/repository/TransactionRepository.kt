package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.CategorySum
import com.flowledger.app.data.local.dao.DailySummary
import com.flowledger.app.data.local.dao.TransactionDao
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {
    fun getAll(): Flow<List<TransactionEntity>> = dao.getAll()

    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        dao.getByDateRange(startDate, endDate)

    fun getByType(type: TransactionType): Flow<List<TransactionEntity>> =
        dao.getByType(type.name)

    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>> =
        dao.getByCategory(categoryId)

    fun getByAccount(accountId: Long): Flow<List<TransactionEntity>> =
        dao.getByAccount(accountId)

    fun getPendingTransactions(): Flow<List<TransactionEntity>> =
        dao.getPendingTransactions()

    suspend fun getById(id: Long): TransactionEntity? = dao.getById(id)

    suspend fun insert(transaction: TransactionEntity): Long = dao.insert(transaction)

    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)

    suspend fun getTotalIncome(startDate: Long, endDate: Long): Long =
        dao.getTotalIncome(startDate, endDate) ?: 0L

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Long =
        dao.getTotalExpense(startDate, endDate) ?: 0L

    suspend fun getExpenseByCategory(startDate: Long, endDate: Long): List<CategorySum> =
        dao.getExpenseByCategory(startDate, endDate)

    suspend fun getDailySummary(startDate: Long, endDate: Long): List<DailySummary> =
        dao.getDailySummary(startDate, endDate)

    suspend fun findDuplicates(date: Long, amount: Long, merchant: String?): List<TransactionEntity> =
        dao.findDuplicates(date, amount, merchant)

    suspend fun getAllList(): List<TransactionEntity> = dao.getAllList()
}
