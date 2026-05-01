package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.AccountDao
import com.flowledger.app.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val dao: AccountDao
) {
    fun getAllActive(): Flow<List<AccountEntity>> = dao.getAllActive()

    fun getAll(): Flow<List<AccountEntity>> = dao.getAll()

    suspend fun getById(id: Long): AccountEntity? = dao.getById(id)

    suspend fun insert(account: AccountEntity): Long = dao.insert(account)

    suspend fun update(account: AccountEntity) = dao.update(account)

    suspend fun delete(account: AccountEntity) = dao.delete(account)
}
