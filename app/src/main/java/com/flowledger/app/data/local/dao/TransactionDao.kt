package com.flowledger.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.flowledger.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY date DESC, created_at DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, created_at DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC, created_at DESC")
    fun getByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY date DESC, created_at DESC")
    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE account_id = :accountId ORDER BY date DESC, created_at DESC")
    fun getByAccount(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE source = :source ORDER BY date DESC")
    fun getBySource(source: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = 'PENDING' ORDER BY date DESC")
    fun getPendingTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate AND status = 'CONFIRMED'")
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Long?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate AND status = 'CONFIRMED'")
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Long?

    @Query("""
        SELECT c.name, SUM(t.amount) as total
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate AND t.status = 'CONFIRMED'
        GROUP BY t.category_id
        ORDER BY total DESC
    """)
    suspend fun getExpenseByCategory(startDate: Long, endDate: Long): List<CategorySum>

    @Query("""
        SELECT date, SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income,
        SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense
        FROM transactions
        WHERE date BETWEEN :startDate AND :endDate AND status = 'CONFIRMED'
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailySummary(startDate: Long, endDate: Long): List<DailySummary>

    @Query("SELECT * FROM transactions WHERE date = :date AND amount = :amount AND merchant_name = :merchant")
    suspend fun findDuplicates(date: Long, amount: Long, merchant: String?): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    suspend fun getAllList(): List<TransactionEntity>
}

data class CategorySum(
    val name: String,
    val total: Long
)

data class DailySummary(
    val date: Long,
    val income: Long,
    val expense: Long
)
