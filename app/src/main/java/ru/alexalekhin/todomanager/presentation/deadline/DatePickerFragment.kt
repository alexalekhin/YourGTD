package ru.alexalekhin.todomanager.presentation.deadline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.alexalekhin.todomanager.R

class DatePickerFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    companion object {

        const val TAG = "DeadlinePickerFragment"

        fun newInstance() =
            DatePickerFragment()
    }
}
