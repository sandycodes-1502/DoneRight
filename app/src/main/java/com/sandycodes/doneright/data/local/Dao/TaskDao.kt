package com.sandycodes.doneright.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query(  """
    SELECT * FROM tasks
    WHERE status = 'TODO'
       OR status = 'IN_PROGRESS'
    ORDER BY updatedAt DESC
    """)
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("""
    SELECT * FROM tasks
    WHERE status = 'COMPLETED'
    ORDER BY updatedAt DESC
    """)
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)
}
