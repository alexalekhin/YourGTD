package ru.alexalekhin.todomanager.domain.models

import ru.alexalekhin.todomanager.data.TODODatabase
import ru.alexalekhin.todomanager.data.task.DBTask
import javax.inject.Inject

class InboxTasksRepository @Inject constructor(private val database: TODODatabase) {

    suspend fun loadTasks(): List<DBTask> {
        return database.taskDao().getByFolderId(ID_FOLDER_INBOX)
    }

    suspend fun getLargestWeight() = database.taskDao().getLargestWeight()

    suspend fun getLargestId() = database.taskDao().getLargestId()

    suspend fun getLargestWeightInFolder(): Int? {
        return database.taskDao().getLargestWeightInFolder(ID_FOLDER_INBOX)
    }

    suspend fun updateTasks(tasks: List<DBTask>) {
        tasks.forEach { database.taskDao().update(it)}
    }

    suspend fun addTask(task: DBTask) {
        database.taskDao().insert(task)
    }

    suspend fun markTaskAsDone(task: DBTask) {
        task.checked = true
        database.taskDao().update(task)
    }

    suspend fun deleteTask(task: DBTask) {
        database.taskDao().delete(task)
    }

    companion object {
        const val ID_PROJECT_NULL = 0
        const val ID_FOLDER_NULL = 0
        const val ID_DOMAIN_NULL = 0
        const val ID_FOLDER_INBOX = -1
    }
}