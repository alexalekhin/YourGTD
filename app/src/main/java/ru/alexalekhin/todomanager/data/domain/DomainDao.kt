package ru.alexalekhin.todomanager.data.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DomainDao {
    //insert domain(s)
    @Insert
    suspend fun insert(vararg domains: DBDomain)

    //remove domain
    @Delete
    suspend fun delete(domain: DBDomain)

    //update domain
    @Update
    suspend fun update(domain: DBDomain)

    //get all domains
    @Query("SELECT * FROM domains")
    suspend fun getAll(): List<DBDomain>

    //get concrete domain
    @Query("SELECT * FROM Domains WHERE id=:id")
    suspend fun getById(id: Int): DBDomain
}