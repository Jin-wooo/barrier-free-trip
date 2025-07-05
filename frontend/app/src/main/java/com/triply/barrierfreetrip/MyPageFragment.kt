package com.triply.barrierfreetrip

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.triply.barrierfreetrip.StayInfoFragment.Companion.CONTENT_TITLE
import com.triply.barrierfreetrip.databinding.FragmentMyPageBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.LoginViewModel


class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: LoginViewModel by activityViewModels()
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }

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
                        viewModel.logout()
                        cancel()
                    }
                })
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }.show()
        }

        binding.tvMypageNickname.text = arguments?.getString(CONTENT_TITLE)

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                loadingProgressBar.show()
            } else {
                loadingProgressBar.dismiss()
            }
        }

        viewModel.logoutResult.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(activity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } else {
                Log.d("로그아웃 결과", "실패하였습니다.")
            }
        }
    }
}