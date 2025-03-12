package com.triply.barrierfreetrip

import com.triply.barrierfreetrip.databinding.FragmentAppinfoBinding
import com.triply.barrierfreetrip.databinding.FragmentMyPageBinding
import com.triply.barrierfreetrip.feature.BaseFragment


class AppInfoFragment : BaseFragment<FragmentAppinfoBinding>(R.layout.fragment_appinfo) {
    override fun initInViewCreated() {
        binding.btnBack.setOnClickListener {
            backToPrevFragment()
        }
    }

    private fun backToPrevFragment() {
        val frgManager = parentFragmentManager
        frgManager.beginTransaction()
            .remove(this@AppInfoFragment)
            .commit()
    }
}