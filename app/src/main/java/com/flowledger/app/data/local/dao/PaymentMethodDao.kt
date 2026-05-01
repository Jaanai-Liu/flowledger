package com.flowledger.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.flowledger.app.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentMethod: PaymentMethodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paymentMethods: List<PaymentMethodEntity>)

    @Update
    suspend fun update(paymentMethod: PaymentMethodEntity)

    @Delete
    suspend fun delete(paymentMethod: PaymentMethodEntity)

    @Query("SELECT * FROM payment_methods WHERE id = :id")
    suspend fun getById(id: Long): PaymentMethodEntity?

    @Query("SELECT * FROM payment_methods WHERE is_active = 1 ORDER BY sort_order ASC")
    fun getAllActive(): Flow<List<PaymentMethodEntity>>

    @Query("SELECT * FROM payment_methods ORDER BY sort_order ASC")
    fun getAll(): Flow<List<PaymentMethodEntity>>
}
