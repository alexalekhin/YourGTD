package ru.alexalekhin.todomanager.presentation.taskcreator

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.task_creation_dialog.view.*

import ru.alexalekhin.todomanager.R

class TaskCreationDialogFragment : DialogFragment() {

    private lateinit var listener: TaskCreationListener
    lateinit var dialogView: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TaskCreationListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement TaskCreationListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val projectId = arguments!!.getSerializable("projectId") as Int?
        val folderId = arguments!!.getSerializable("folderId") as Int?

        dialogView = View.inflate(this.context, R.layout.task_creation_dialog, null)

        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle(R.string.title_new_task)
            .setPositiveButton(R.string.label_action_create) { _, _ ->
                listener.onSuccessfulTaskCreation(
                    Bundle().apply {
                        putInt("taskId", resources.getInteger(R.integer.id_task_null))
                        putString("taskTitle", dialogView.editTextTaskTitle.text.toString())
                        putBoolean("isChecked", false)
                        putInt(
                            "projectId",
                            projectId ?: resources.getInteger(R.integer.id_project_null)
                        )
                        putInt("domainId", resources.getInteger(R.integer.id_domain_null))
                        putInt(
                            "folderId",
                            folderId ?: resources.getInteger(R.integer.id_folder_null)
                        )
                    },
                    projectId,
                    folderId
                )
            }
            .setNegativeButton(R.string.label_action_cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    companion object {
        @JvmStatic
        fun newInstance(projectId: Int? = null, folderId: Int? = null) =
            TaskCreationDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("projectId", projectId)
                    putSerializable("folderId", folderId)
                }
            }

        const val TAG = "TaskCreationDialogFragment"
    }

    interface TaskCreationListener {
        fun onSuccessfulTaskCreation(taskData: Bundle, projectId: Int?, folderId: Int?)
    }
}