package com.triply.barrierfreetrip

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.triply.barrierfreetrip.databinding.DialogAllProgressBinding
import com.triply.barrierfreetrip.feature.BaseDialogFragment

class ProgressDialogFragment : BaseDialogFragment<DialogAllProgressBinding>(
    R.layout.dialog_all_progress
) {


    fun show(manager: FragmentManager) {
        super.show(manager, "ProgressDialogFragment")
    }


}