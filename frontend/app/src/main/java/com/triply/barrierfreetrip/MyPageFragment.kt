package com.triply.barrierfreetrip

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.triply.barrierfreetrip.StayInfoFragment.Companion.CONTENT_TITLE
import com.triply.barrierfreetrip.databinding.FragmentMyPageBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel


class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    override fun initInViewCreated() {
        binding.btnMypageAppinfo.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, AppInfoFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.tvMypageLogout.setOnClickListener {
            BFTRequestDialog(requireContext()).apply {
                setQuestionText("로그아웃하시겠습니까?")
                setAcceptButtonText("네")
                setRejectButtonText("아니요")
                setOnDialogClickListener(object : OnClickDialogButtons {
                    override fun onClickReject() {
                        cancel()
                    }

                    override fun onClickAccept() {
                        // 로그아웃 API
                    }
                })
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }.show()
        }

        binding.tvMypageNickname.text = arguments?.getString(CONTENT_TITLE)
    }
}