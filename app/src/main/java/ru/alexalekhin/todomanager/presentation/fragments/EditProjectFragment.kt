package ru.alexalekhin.todomanager.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_edit_project.*

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.domain.viewModels.ProjectViewModel
import ru.alexalekhin.todomanager.di.ViewModelFactory

import javax.inject.Inject

class EditProjectFragment : Fragment(R.layout.fragment_edit_project) {

    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: ProjectViewModel

    override fun onAttach(context: Context) {
        (context.applicationContext as TODOManagerApp).component.inject(this)

        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var deadlineDate = ""
        viewModel = ViewModelProvider(this, viewModelFactory)[ProjectViewModel::class.java]

        setPredefinedDataOfProject()
        viewModel.loadProjectData(arguments!!.getInt("projectId"))

        buttonCancelEditing.setOnClickListener {
            listener?.apply {
                hideKeyboard()
                onBackPressed()
            }
        }
        buttonEditingDone.setOnClickListener {
            viewModel.updateProjectData(Bundle().apply {
                //                putInt("projectId", arguments!!.getInt("projectId"))
                putString("projectTitle", editTextProjectCreationTitle.text.toString())
                putString("projectDescription", editTextProjectDescription.text.toString())
                putString("projectDeadline", deadlineDate)
                //TODO: add domain ID and folder ID
                putInt("domainId", 0)
                putInt("folderId", 0)
            })

            listener?.apply {
                updateProjectData(arguments!!.getInt("projectId"))
                hideKeyboard()
                onBackPressed()
            }
        }
        calendarViewDeadlinePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            deadlineDate = "$dayOfMonth/${month + 1}/$year"
        }
    }

    private fun setPredefinedDataOfProject() {
        arguments?.let {
            editTextProjectCreationTitle.setText(it.getString("projectTitle", ""))
            editTextProjectDescription.setText(it.getString("projectDescription", ""))
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
                    extraData?.let {
                        putAll(it)
                    }
                }
            }

        const val TAG = "EditProjectFragment"
    }
}
