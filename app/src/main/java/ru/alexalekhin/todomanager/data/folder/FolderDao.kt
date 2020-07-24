package ru.alexalekhin.todomanager.data.folder

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FolderDao {
    //get folder by id
    @Query("SELECT * FROM Folders WHERE id=:id")
    suspend fun getById(id: Int): DBFolder

    //get all projects
    @Query("SELECT * FROM Folders")
    suspend fun getAll(): List<DBFolder>
}
