package ru.alexalekhin.todomanager.data.project

import androidx.room.*

@Dao
interface ProjectDao {
    //insert project(s)
    @Insert
    suspend fun insert(vararg projects: DBProject)


    //update task
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(project: DBProject)

    //remove project
    @Delete
    suspend fun delete(project: DBProject)

    //get all projects
    @Query("SELECT * FROM Projects")
    suspend fun getAll(): List<DBProject>

    //get project by id
    @Query("SELECT * FROM Projects WHERE id=:id")
    suspend fun getById(id: Int?): DBProject

    //get project(s) by folder
    @Query("SELECT * FROM Projects WHERE folderId=:folderId")
    suspend fun getByFolderId(folderId: Int?): DBProject

    //get project by domain
    @Query("SELECT * FROM Projects WHERE domainId=:domainId")
    suspend fun getByDomainId(domainId: Int?): DBProject

    //get largest weight of project
    @Query("SELECT MAX(weight) FROM Projects")
    suspend fun getLargestWeight(): Int?

    //get largest id of project
    @Query("SELECT MAX(id) FROM Projects")
    suspend fun getLargestId(): Int?
}