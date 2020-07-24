package ru.alexalekhin.todomanager.domain.viewModels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.alexalekhin.todomanager.domain.models.ProjectModel

import ru.alexalekhin.todomanager.data.project.DBProject
import ru.alexalekhin.todomanager.data.task.DBTask

import javax.inject.Inject

class ProjectViewModel @Inject constructor(private val model: ProjectModel) :
    ViewModel() {

    val tasksLiveData: MutableLiveData<List<DBTask>> = MutableLiveData(emptyList())
    val projectLiveData: MutableLiveData<DBProject> = MutableLiveData()
    val dataLoadingState: MutableLiveData<DataLoadingState> = MutableLiveData()

    private var currentBiggestTaskWeight: Int = 0
    private var currentBiggestTaskId: Int = 0

    fun loadProjectData(projectId: Int) {
        viewModelScope.launch { projectLiveData.postValue(model.loadProjectData(projectId)) }
    }

    fun updateProjectData(projectData: Bundle) {
        viewModelScope.launch {
            val project = DBProject(
                projectLiveData.value!!.id,
                projectData.getString("projectTitle", ""),
                projectData.getString("projectDescription", ""),
                projectData.getString("projectDeadline", ""),
                projectData.getInt("domainId"),
                projectData.getInt("folderId"),
                projectLiveData.value!!.weight
            )
            model.updateProjectData(project)
            projectLiveData.postValue(project)
        }
    }

    fun loadTasksDataOfProject(projectId: Int?) {
        dataLoadingState.postValue(DataLoadingState.LOADING)
        viewModelScope.launch {
            //TODO: filter in adapter
            currentBiggestTaskWeight = model.getLargestWeight() ?: 0
            currentBiggestTaskId = model.getLargestTaskId() ?: 0
            tasksLiveData.postValue(
                model.loadTasksOfProject(projectId)
                    .sortedByDescending { it.weight }
                    .filter { !it.checked }
            )
            dataLoadingState.postValue(DataLoadingState.LOADED)
        }
    }

    fun updateTasksOfProject(tasks: List<DBTask>, projectId: Int) {
        viewModelScope.launch { model.updateTasksOfProject(tasks, projectId) }
    }

    fun markTaskAsDone(task: DBTask, projectId: Int?) {
        viewModelScope.launch { model.markTaskAsDone(task, projectId) }
    }

    fun addCreatedTaskToProject(task: DBTask, projectId: Int?) {
        viewModelScope.launch { model.addTaskToProject(task.copy(), projectId) }
    }

    fun createTaskInProject(taskData: Bundle, projectId: Int?): DBTask {
        return DBTask(
            ++currentBiggestTaskId,
            taskData.getString("taskTitle") ?: "",
            taskData.getBoolean("isChecked"),
            projectId,
            if (taskData.getInt("domainId") == ID_DOMAIN_NULL) null else taskData.getInt("domainId"),
            if (taskData.getInt("folderId") == ID_FOLDER_NULL) null else taskData.getInt("folderId"),
            ++currentBiggestTaskWeight
        )
    }

    fun createAndAddTaskToProject(taskData: Bundle, projectId: Int?) {
        viewModelScope.launch {
            val task = DBTask(
                ++currentBiggestTaskId,
                taskData.getString("taskTitle") ?: "",
                taskData.getBoolean("isChecked"),
                if (taskData.getInt("projectId") == ID_PROJECT_NULL) null else taskData.getInt("projectId"),
                if (taskData.getInt("domainId") == ID_DOMAIN_NULL) null else taskData.getInt("domainId"),
                if (taskData.getInt("folderId") == ID_FOLDER_NULL) null else taskData.getInt("folderId"),
                ++currentBiggestTaskWeight
            )
            model.addTaskToProject(task, projectId)
        }
    }

    fun deleteTaskFromProject(task: DBTask, projectId: Int) {
        viewModelScope.launch { model.deleteTaskFromProject(task, projectId) }
    }

    companion object {
        const val ID_PROJECT_NULL = 0
        const val ID_FOLDER_NULL = 0
        const val ID_DOMAIN_NULL = 0
    }

    enum class DataLoadingState {
        LOADED,
        LOADING,
        ERROR,
        IDLE
    }
}