package com.sanson.digitaldetox.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sanson.digitaldetox.data.db.entity.CustomMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomMessageDao {

    @Query("SELECT * FROM custom_messages ORDER BY createdAt DESC")
    fun getAllMessages(): Flow<List<CustomMessageEntity>>

    @Query("SELECT * FROM custom_messages WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getActiveMessages(): List<CustomMessageEntity>

    @Query("SELECT * FROM custom_messages WHERE id = :id")
    suspend fun getById(id: Long): CustomMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: CustomMessageEntity): Long

    @Update
    suspend fun update(message: CustomMessageEntity)

    @Delete
    suspend fun delete(message: CustomMessageEntity)

    @Query("DELETE FROM custom_messages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM custom_messages")
    suspend fun getCount(): Int
}
