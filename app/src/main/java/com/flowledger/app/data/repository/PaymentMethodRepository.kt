package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.PaymentMethodDao
import com.flowledger.app.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentMethodRepository @Inject constructor(
    private val dao: PaymentMethodDao
) {
    fun getAllActive(): Flow<List<PaymentMethodEntity>> = dao.getAllActive()
    fun getAll(): Flow<List<PaymentMethodEntity>> = dao.getAll()
    suspend fun getById(id: Long): PaymentMethodEntity? = dao.getById(id)
    suspend fun insert(method: PaymentMethodEntity): Long = dao.insert(method)
    suspend fun update(method: PaymentMethodEntity) = dao.update(method)
    suspend fun delete(method: PaymentMethodEntity) = dao.delete(method)
}
