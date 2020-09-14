package ru.alexalekhin.todomanager.presentation.project.projecteditor.deadline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_date_picker.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.presentation.project.ProjectViewModel
import javax.inject.Inject

class DatePickerFragment : BottomSheetDialogFragment() {

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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[ProjectViewModel::class.java]

        projectId?.let { projectId -> viewModel.loadProjectData(projectId) }

        with(setDeadline) {
            setOnClickListener {
                dismiss()

                viewModel.updateProjectDeadline("${datePicker.dayOfMonth}.${datePicker.month + 1}.${datePicker.year}")

                TimePickerFragment.newInstance().show(requireActivity().supportFragmentManager, TimePickerFragment.TAG)
            }
        }
    }

    companion object {

        const val TAG = "DeadlinePickerFragment"

        @JvmStatic
        fun newInstance(projectId: Int, extraData: Bundle? = null) =
            DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putInt("projectId", projectId)
                    extraData?.let { putAll(it) }
                }
            }
    }
}
