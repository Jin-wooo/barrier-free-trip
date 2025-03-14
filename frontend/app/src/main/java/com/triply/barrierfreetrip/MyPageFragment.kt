package com.triply.barrierfreetrip

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.triply.barrierfreetrip.StayInfoFragment.Companion.CONTENT_TITLE
import com.triply.barrierfreetrip.databinding.FragmentMyPageBinding
import com.triply.barrierfreetrip.feature.BaseFragment


class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    override fun initInViewCreated() {
        val navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )

        binding.btnMypageAppinfo.setOnClickListener {
            navController.navigate(
                resId = R.id.appInfoFragment
            )
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