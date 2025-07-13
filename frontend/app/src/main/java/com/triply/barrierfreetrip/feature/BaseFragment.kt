package com.triply.barrierfreetrip.feature

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.triply.barrierfreetrip.LoginActivity
import kotlinx.coroutines.launch

abstract class BaseFragment<TempDataBinding: ViewDataBinding>(
    @LayoutRes private val layoutId : Int
) : Fragment() {
    private var _binding: TempDataBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            LogoutManager.logoutEvent.collect {
                val intent = Intent(activity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
        initInViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun initInViewCreated()
}