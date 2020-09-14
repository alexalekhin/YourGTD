package ru.alexalekhin.todomanager.presentation.project

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.alexalekhin.todomanager.data.project.DBProject
import ru.alexalekhin.todomanager.data.task.DBTask
import ru.alexalekhin.todomanager.domain.models.ProjectDataRepository
import ru.alexalekhin.todomanager.presentation.entities.DataLoadingState
import javax.inject.Inject

class ProjectViewModel @Inject constructor(private val projectDataRepository: ProjectDataRepository) :
    ViewModel() {

    val tasksLiveData: MutableLiveData<List<DBTask>> = MutableLiveData()
    val projectLiveData: MutableLiveData<DBProject> = MutableLiveData()
    val dataLoadingState: MutableLiveData<DataLoadingState> = MutableLiveData()

    private var currentBiggestTaskWeight: Int = 0
    private var currentBiggestTaskId: Int = 0

    fun loadProjectData(projectId: Int) {
        viewModelScope.launch {
            projectLiveData.postValue(projectDataRepository.loadProjectData(projectId))
        }
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
            projectDataRepository.updateProjectData(project)
            projectLiveData.postValue(project)
        }
    }

    fun loadTasksDataOfProject(projectId: Int?) {
        viewModelScope.launch {
            //TODO: filter in adapter
            if (tasksLiveData.value == null) {
                dataLoadingState.postValue(DataLoadingState.LOADING)

                currentBiggestTaskWeight = projectDataRepository.getLargestWeight() ?: 0
                currentBiggestTaskId = projectDataRepository.getLargestTaskId() ?: 0

                tasksLiveData.postValue(
                    projectDataRepository.loadTasksOfProject(projectId)
                        .sortedByDescending { it.weight }
                        .filter { !it.checked }
                )
            }
            dataLoadingState.postValue(DataLoadingState.LOADED)
        }
    }

    fun updateTasksOfProject(tasks: List<DBTask>, projectId: Int) {
        viewModelScope.launch { projectDataRepository.updateTasksOfProject(tasks, projectId) }
    }

    fun markTaskAsDone(task: DBTask, projectId: Int?) {
        viewModelScope.launch { projectDataRepository.markTaskAsDone(task, projectId) }
    }

    fun addCreatedTaskToProject(task: DBTask, projectId: Int?) {
        viewModelScope.launch { projectDataRepository.addTaskToProject(task.copy(), projectId) }
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
            projectDataRepository.addTaskToProject(task, projectId)
        }
    }

    fun deleteTaskFromProject(task: DBTask, projectId: Int) {
        viewModelScope.launch { projectDataRepository.deleteTaskFromProject(task, projectId) }
    }

    companion object {

        const val ID_PROJECT_NULL = 0
        const val ID_FOLDER_NULL = 0
        const val ID_DOMAIN_NULL = 0
    }
}