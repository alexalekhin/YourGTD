package ru.alexalekhin.todomanager.data.task

import androidx.room.*

@Dao
interface TaskDao {
    //insert new task(s)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg tasks: DBTask)

    //update task
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: DBTask)

    //remove task
    @Delete
    suspend fun delete(task: DBTask)

    //get all tasks
    @Query("SELECT * FROM Tasks")
    suspend fun getAll(): List<DBTask>

    //get task by id
    @Query("SELECT * FROM Tasks WHERE id=:id")
    suspend fun getById(id: Int): DBTask

    //get tasks by project
    @Query("SELECT * FROM Tasks WHERE projectId=:projectId")
    suspend fun getByProjectId(projectId: Int?): List<DBTask>

    @Query("SELECT * FROM Tasks WHERE domainId=:domainId")
    suspend fun getByDomainId(domainId: Int?): List<DBTask>

    //get tasks by folder
    @Query("SELECT * FROM Tasks WHERE folderId=:folderId")
    suspend fun getByFolderId(folderId: Int?): List<DBTask>

    //get largest id of task
    @Query("SELECT MAX(id) FROM Tasks")
    suspend fun getLargestId(): Int?

    //get largest weight of task
    @Query("SELECT MAX(weight) FROM Tasks")
    suspend fun getLargestWeight(): Int?

    //get largest weight of tasks in folder
    @Query("SELECT MAX(weight) FROM (SELECT * FROM Tasks WHERE folderId=:folderId)")
    suspend fun getLargestWeightInFolder(folderId: Int?): Int?

    //get largest weight of tasks in project
    @Query("SELECT MAX(weight) FROM (SELECT * FROM Tasks WHERE projectId=:projectId)")
    suspend fun getLargestWeightInProject(projectId: Int?): Int?
}