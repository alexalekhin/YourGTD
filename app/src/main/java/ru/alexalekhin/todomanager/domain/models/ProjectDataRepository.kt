package ru.alexalekhin.todomanager.domain.models

import ru.alexalekhin.todomanager.data.TODODatabase
import ru.alexalekhin.todomanager.data.project.DBProject
import ru.alexalekhin.todomanager.data.task.DBTask

import javax.inject.Inject

class ProjectDataRepository @Inject constructor(private val database: TODODatabase) {

    suspend fun loadProjectData(projectId: Int) = database.projectDao().getById(projectId)

    suspend fun updateProjectData(project: DBProject) {
        database.projectDao().update(project)
    }

    suspend fun getLargestTaskId() = database.taskDao().getLargestId()

    suspend fun getLargestWeight() = database.taskDao().getLargestWeight()

    suspend fun getLargestWeightOfProject(projectId: Int?) =
        database.taskDao().getLargestWeightInProject(projectId)

    suspend fun addTaskToProject(task: DBTask, projectId: Int?) {
        database.taskDao().insert(task)
        //TODO: update project params (tasks counter, progress data)
        database.projectDao().getById(projectId)
    }

    suspend fun markTaskAsDone(task: DBTask, projectId: Int?) {
        task.checked = true
        database.taskDao().update(task)
    }

    suspend fun deleteTaskFromProject(task: DBTask, projectId: Int) {
        database.taskDao().delete(task)
        //TODO: update project params (tasks counter, progress data)
        database.projectDao().getById(projectId)
    }

    suspend fun loadTasksOfProject(projectId: Int?) = database.taskDao().getByProjectId(projectId)

    suspend fun updateTasksOfProject(tasks: List<DBTask>, projectId: Int) {
        tasks.forEach { database.taskDao().update(it) }
        //TODO: update project params
        database.projectDao().getById(projectId)
    }
}