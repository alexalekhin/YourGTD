package ru.alexalekhin.todomanager.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alexalekhin.todomanager.data.domain.DBDomain
import ru.alexalekhin.todomanager.data.domain.DomainDao
import ru.alexalekhin.todomanager.data.folder.DBFolder
import ru.alexalekhin.todomanager.data.folder.FolderDao
import ru.alexalekhin.todomanager.data.project.DBProject
import ru.alexalekhin.todomanager.data.project.ProjectDao
import ru.alexalekhin.todomanager.data.task.DBTask
import ru.alexalekhin.todomanager.data.task.TaskDao

@Database(entities = [DBTask::class, DBProject::class, DBDomain::class, DBFolder::class], version = 1, exportSchema = false)
abstract class TODODatabase : RoomDatabase() {
    abstract fun domainDao(): DomainDao
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun folderDao(): FolderDao
}
