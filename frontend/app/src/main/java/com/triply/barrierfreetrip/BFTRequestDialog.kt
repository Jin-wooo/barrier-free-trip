package com.triply.barrierfreetrip

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.triply.barrierfreetrip.databinding.DialogRequestBinding

class BFTRequestDialog(
    context: Context
) : Dialog(context) {

    private val binding by lazy {
        DialogRequestBinding.inflate(LayoutInflater.from(context), null, false)
    }
    private var onClickDialogButtons: OnClickDialogButtons? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCancelable(true)
        setContentView(binding.root)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        with(binding) {
            btnDialogRequestReject.setOnClickListener {
                onClickDialogButtons?.onClickReject()
            }
            btnDialogRequestAccpet.setOnClickListener {
                onClickDialogButtons?.onClickAccept()
            }
        }
    }

    fun setQuestionText(text: String) {
        binding.tvDialogRequestQuestion.text = text
    }

    fun setRejectButtonText(text: String) {
        binding.btnDialogRequestReject.text = text
    }

    fun setAcceptButtonText(text: String) {
        binding.btnDialogRequestAccpet.text = text
    }

    fun setOnDialogClickListener(onClickDialogButtons: OnClickDialogButtons) {
        this.onClickDialogButtons = onClickDialogButtons
    }
}

interface OnClickDialogButtons {
    fun onClickReject()

    fun onClickAccept()
}