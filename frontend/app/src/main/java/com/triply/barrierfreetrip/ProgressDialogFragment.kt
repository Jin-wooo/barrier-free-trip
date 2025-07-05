package com.triply.barrierfreetrip

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.FragmentManager
import com.triply.barrierfreetrip.databinding.DialogAllProgressBinding
import com.triply.barrierfreetrip.feature.BaseDialogFragment

class ProgressDialogFragment : BaseDialogFragment<DialogAllProgressBinding>(
    R.layout.dialog_all_progress
) {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show(manager: FragmentManager) {
        super.show(manager, "ProgressDialogFragment")
    }
}