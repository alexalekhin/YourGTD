package ru.alexalekhin.todomanager.presentation.project.projecteditor

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_edit_project.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.presentation.project.ProjectViewModel
import ru.alexalekhin.todomanager.presentation.project.projecteditor.deadline.DatePickerFragment
import ru.alexalekhin.todomanager.utils.hideKeyboard
import javax.inject.Inject

class EditProjectFragment : Fragment(R.layout.fragment_edit_project) {

    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProjectViewModel

    private val projectId: Int? by lazy { arguments?.getInt("projectId") }

    override fun onAttach(context: Context) {
        (context.applicationContext as TODOManagerApp).component.inject(this)

        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else throw IllegalStateException("$context must implement OnFragmentInteractionListener")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[ProjectViewModel::class.java]

        setPredefinedDataOfProject()

        projectId?.let { projectId -> viewModel.loadProjectData(projectId) }

        cancelEditing.setOnClickListener {
            it.hideKeyboard()

            listener?.onBackPressed()

        }
        editingDone.setOnClickListener {
            viewModel.updateProjectData(Bundle().apply {
                putString("projectTitle", projectTitleEditText.text.toString())
                putString("projectDescription", projectDescriptionEditText.text.toString())
                putString("projectDeadline", "")
                //TODO: add domain ID and folder ID
                putInt("domainId", 0)
                putInt("folderId", 0)
            })

            it.hideKeyboard()
            listener?.run {
                projectId?.let { projectId -> updateProjectData(projectId) }
                onBackPressed()
            }
        }

        setDeadline.setOnClickListener {
            projectId?.let { projectId ->
                DatePickerFragment.newInstance(projectId).show(requireActivity().supportFragmentManager, DatePickerFragment.TAG)
            }
        }
    }

    private fun setPredefinedDataOfProject() {
        arguments?.let {
            projectTitleEditText.setText(it.getString("projectTitle", ""))
            projectDescriptionEditText.setText(it.getString("projectDescription", ""))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {

        @JvmStatic
        fun newInstance(projectId: Int, extraData: Bundle? = null) =
            EditProjectFragment().apply {
                arguments = Bundle().apply {
                    putInt("projectId", projectId)
                    extraData?.let { putAll(it) }
                }
            }

        const val TAG = "EditProjectFragment"
    }
}
