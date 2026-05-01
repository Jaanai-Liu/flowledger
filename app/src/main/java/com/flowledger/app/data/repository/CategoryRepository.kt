package com.flowledger.app.data.repository

import com.flowledger.app.data.local.dao.CategoryDao
import com.flowledger.app.data.local.entity.CategoryEntity
import com.flowledger.app.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    fun getAll(): Flow<List<CategoryEntity>> = dao.getAll()

    fun getByType(type: TransactionType): Flow<List<CategoryEntity>> =
        dao.getByType(type.name)

    fun getCustomCategories(): Flow<List<CategoryEntity>> = dao.getCustomCategories()

    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)

    suspend fun insert(category: CategoryEntity): Long = dao.insert(category)

    suspend fun update(category: CategoryEntity) = dao.update(category)

    suspend fun delete(category: CategoryEntity) = dao.delete(category)
}
