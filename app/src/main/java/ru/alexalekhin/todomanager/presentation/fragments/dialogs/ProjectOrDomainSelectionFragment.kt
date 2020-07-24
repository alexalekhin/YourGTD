package ru.alexalekhin.todomanager.presentation.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_project_or_domain_selection.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener

class ProjectOrDomainSelectionFragment : BottomSheetDialogFragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_or_domain_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintLayoutProjectCreation.setOnClickListener {
            dismiss()
            listener!!.showAddProjectDialog(null)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProjectOrDomainSelectionFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val TAG = "ProjectOrDomainSelectionFragment"
    }
}
