package com.flowledger.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.flowledger.app.data.local.entity.NotificationTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationTemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: NotificationTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<NotificationTemplateEntity>)

    @Update
    suspend fun update(template: NotificationTemplateEntity)

    @Query("SELECT * FROM notification_templates WHERE app_package = :packageName AND is_active = 1 LIMIT 1")
    suspend fun getByPackage(packageName: String): NotificationTemplateEntity?

    @Query("SELECT * FROM notification_templates ORDER BY app_name ASC")
    fun getAll(): Flow<List<NotificationTemplateEntity>>
}
