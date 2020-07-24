package ru.alexalekhin.todomanager.presentation.fragments.dialogFragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_project_creation_dialog.view.*

import ru.alexalekhin.todomanager.R

class ProjectCreationDialogFragment : DialogFragment() {

    private var listener: ProjectCreationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProjectCreationListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement ProjectCreationListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = View.inflate(this.context, R.layout.fragment_project_creation_dialog, null)

        return MaterialAlertDialogBuilder(this.context)
            .setView(dialogView)
            .setTitle(R.string.title_new_project)
            .setPositiveButton(R.string.label_action_create) { _, _ ->
                listener!!.onSuccessfulProjectCreation(
                    Bundle().apply {
                        putInt("projectId", resources.getInteger(R.integer.id_project_null))
                        putString("projectTitle", dialogView.editTextProjectCreationTitle.text.toString())
                        putString("projectDescription", "")
                        putString("projectDeadline", "")
                        putInt("domainId", resources.getInteger(R.integer.id_domain_null))
                        putInt("folderId", resources.getInteger(R.integer.id_folder_null))
                    },
                    null
                )
                dismiss()
            }
            .setNegativeButton(R.string.label_action_cancel) { _, _ -> dismiss() }
            .create()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(domainId: Int? = null) =
            ProjectCreationDialogFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val TAG = "ProjectCreationDialogFragment"
    }

    interface ProjectCreationListener {
        fun onSuccessfulProjectCreation(projectData: Bundle, domainId: Int?)
    }
}
