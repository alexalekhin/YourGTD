package ru.alexalekhin.todomanager.domain.viewModels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.alexalekhin.todomanager.data.task.DBTask
import ru.alexalekhin.todomanager.domain.models.InboxTasksRepository
import ru.alexalekhin.todomanager.domain.viewModels.entities.DataLoadingState
import javax.inject.Inject

class InboxViewModel @Inject constructor(private val tasksRepository: InboxTasksRepository) : ViewModel() {

    val tasksLiveData: MutableLiveData<List<DBTask>> = MutableLiveData(emptyList())
    val dataLoadingState: MutableLiveData<DataLoadingState> = MutableLiveData()

    private var currentBiggestWeight = 0
    private var currentBiggestId = 0

    fun loadTasksData() {
        viewModelScope.launch {
            //TODO: filter in adapter
            tasksLiveData.postValue(tasksRepository.loadTasks().sortedByDescending { it.weight }.filter { !it.checked })
            currentBiggestWeight = tasksRepository.getLargestWeight() ?: 0
            currentBiggestId = tasksRepository.getLargestId() ?: 0
            dataLoadingState.postValue(DataLoadingState.LOADED)
        }
        dataLoadingState.postValue(DataLoadingState.LOADING)
    }

    fun updateTasks(tasks: List<DBTask>) {
        viewModelScope.launch {
            when {
                tasks.isEmpty() -> {
                    //TODO: ERROR
                }
                else -> tasksRepository.updateTasks(tasks)
            }
        }
    }

    fun createTask(taskData: Bundle): DBTask {
        return DBTask(
            ++currentBiggestId,
            taskData.getString("taskTitle") ?: "",
            taskData.getBoolean("isChecked"),
            if (taskData.getInt("projectId") == ID_PROJECT_NULL) null else taskData.getInt("projectId"),
            if (taskData.getInt("domainId") == ID_DOMAIN_NULL) null else taskData.getInt("domainId"),
            if (taskData.getInt("folderId") == ID_FOLDER_NULL) null else taskData.getInt("folderId"),
            ++currentBiggestWeight
        )
    }

    fun addCreatedTask(task: DBTask) {
        viewModelScope.launch { tasksRepository.addTask(task.copy()) }
    }

    fun createAndAddTask(taskData: Bundle) {
        viewModelScope.launch {
            val task = with(taskData) {
                DBTask(
                    getInt("taskId"),
                    getString("taskTitle") ?: "",
                    getBoolean("isChecked"),
                    if (getInt("projectId") == ID_PROJECT_NULL) null else getInt("projectId"),
                    if (getInt("domainId") == ID_DOMAIN_NULL) null else getInt("domainId"),
                    if (getInt("folderId") == ID_FOLDER_NULL) null else getInt("folderId"),
                    ++currentBiggestWeight
                )
            }
            tasksRepository.addTask(task)
        }
    }

    fun markTaskAsDone(task: DBTask) {
        viewModelScope.launch { tasksRepository.markTaskAsDone(task) }
    }

    fun deleteTask(task: DBTask) {
        viewModelScope.launch { tasksRepository.deleteTask(task) }
    }

    companion object {
        const val ID_PROJECT_NULL = 0
        const val ID_FOLDER_NULL = 0
        const val ID_DOMAIN_NULL = 0
    }
}
