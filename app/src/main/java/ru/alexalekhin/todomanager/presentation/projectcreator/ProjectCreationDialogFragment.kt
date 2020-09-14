package ru.alexalekhin.todomanager.presentation.projectcreator

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_project_creation_dialog.*
import kotlinx.android.synthetic.main.fragment_project_creation_dialog.view.*

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.utils.showKeyboard

class ProjectCreationDialogFragment : DialogFragment() {

    private var listener: ProjectCreationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProjectCreationListener) {
            listener = context
        } else throw IllegalStateException("$context must implement ProjectCreationListener")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = View.inflate(requireContext(), R.layout.fragment_project_creation_dialog, null)

        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle(R.string.title_new_project)
            .setPositiveButton(R.string.label_action_create) { _, _ ->
                listener?.onSuccessfulProjectCreation(
                    Bundle().apply {
                        putInt("projectId", resources.getInteger(R.integer.id_project_null))
                        putString("projectTitle", dialogView.projectTitleEditText.text.toString())
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

    override fun onStart() {
        super.onStart()
        dialog?.projectTitleEditText?.showKeyboard()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(domainId: Int? = null) =
            ProjectCreationDialogFragment()

        const val TAG = "ProjectCreationDialogFragment"
    }

    interface ProjectCreationListener {
        fun onSuccessfulProjectCreation(projectData: Bundle, domainId: Int?)
    }
}
