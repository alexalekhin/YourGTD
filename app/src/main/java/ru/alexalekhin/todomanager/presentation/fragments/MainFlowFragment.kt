package ru.alexalekhin.todomanager.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener

class MainFlowFragment : Fragment(R.layout.fragment_flow_main) {
    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null)
            openMainScreen()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun showProjectEditingDialog(projectId: Int, extraData: Bundle? = null) {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.constraintLayoutProjectRoot,
                EditProjectFragment.newInstance(projectId, extraData),
                EditProjectFragment.TAG
            )
            .addToBackStack(EditProjectFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    fun openMainScreen() {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutMainContainer, MainFragment.newInstance(), MainFragment.TAG)
            .addToBackStack(MainFragment.TAG)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    fun openFolder(folderId: Int) {
        when (folderId) {
            resources.getInteger(R.integer.id_folder_inbox) -> childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frameLayoutMainContainer,
                    InboxFragment.newInstance(),
                    InboxFragment.TAG
                )
                .addToBackStack(InboxFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    fun openProject(projectId: Int, extraData: Bundle? = null) {
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.frameLayoutMainContainer,
                ProjectFragment.newInstance(projectId, extraData),
                ProjectFragment.TAG
            )
            .addToBackStack(ProjectFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFlowFragment().apply { arguments = Bundle().apply {} }

        const val TAG = "MainFlowFragment"
    }
}
