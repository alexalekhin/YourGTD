package ru.alexalekhin.todomanager.domain.models

import ru.alexalekhin.todomanager.data.TODODatabase
import ru.alexalekhin.todomanager.data.project.DBProject

import javax.inject.Inject

class MainModel @Inject constructor(private val database: TODODatabase) {

    suspend fun addProject(project: DBProject) {
        database.projectDao().insert(project)
    }

    suspend fun deleteProject(project: DBProject) {
        database.projectDao().delete(project)
    }

    suspend fun loadProjects() = database.projectDao().getAll()

    suspend fun updateProjects(projects: List<DBProject>) {
        projects.forEach { database.projectDao().update(it) }
    }

    suspend fun getLargestProjectWeight() = database.projectDao().getLargestWeight()

    suspend fun getLargestProjectId() = database.projectDao().getLargestId()
}