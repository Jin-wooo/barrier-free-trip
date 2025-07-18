package com.triply.barrierfreetrip

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.triply.barrierfreetrip.HomeFragment.Companion.CONTENT_TYPE
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.adapter.BFTSpinnerAdapter
import com.triply.barrierfreetrip.adapter.InfoSquareAdapter
import com.triply.barrierfreetrip.adapter.OnItemClickListener
import com.triply.barrierfreetrip.adapter.OnLikeClickListener
import com.triply.barrierfreetrip.adapter.decoration.StayListItemViewHolderDecoration
import com.triply.barrierfreetrip.data.InfoSquareListDto.InfoSquareItemDto
import com.triply.barrierfreetrip.data.RegionListDto.Region
import com.triply.barrierfreetrip.databinding.FragmentStaylistBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel
import com.triply.barrierfreetrip.util.CONTENT_TYPE_STAY
import com.triply.barrierfreetrip.util.CONTENT_TYPE_TOUR

class StaylistFragment : BaseFragment<FragmentStaylistBinding>(R.layout.fragment_staylist) {
    private val viewModel: MainViewModel by activityViewModels()
    private var type: String? = null
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }

    // sido data
    private val sidoCodes = arrayListOf(Region(code = "-1", name = "시도 선택"))
    private val sidoNames = arrayListOf("시도 선택")
    private var sidoPosition = 0

    // sigungu data
    private val sigunguCodes = arrayListOf(Region(code = "-1", name = "구군 선택"))
    private val sigunguNames = arrayListOf("구군 선택")
    private var sigunguPosition = 0

    // facility data
    private val fcltList = ArrayList<InfoSquareItemDto>()

    // scroll position
    private var scrollState: Parcelable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.getString(CONTENT_TYPE)

    }

    override fun initInViewCreated() {
        val navController = findNavController()
        initTitle()
        initSpinner()

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        viewModel.getSidoCode()
        viewModel.sidoCodes.observe(viewLifecycleOwner) { sidoList ->
            with(sidoCodes) {
                clear()
                add(Region(code = "-1", "시도 선택"))
                addAll(sidoList)
            }
            with(sidoNames) {
                clear()
                add("시도 선택")
                addAll(sidoList.map { sido -> sido.name })
            }
        }
        viewModel.sigunguCodes.observe(viewLifecycleOwner) { sigunguList ->
            with(sigunguCodes) {
                clear()
                add(Region(code = "-1", name = "구군 선택"))
                addAll(sigunguList)
            }
            with(sigunguNames) {
                clear()
                add("구군 선택")
                addAll(sigunguList.map { sigungu -> sigungu.name})
            }
        }

        binding.tvRequireSelection.visibility = View.VISIBLE

        binding.spnBigArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    binding.spnSmallArea.setSelection(0)
                    binding.spnSmallArea.isEnabled = false
                    return
                }
                binding.tvRequireSelection.visibility = View.VISIBLE
                binding.spnSmallArea.isEnabled = true
                sidoPosition = position

                fcltList.clear()
                (binding.rvList.adapter as InfoSquareAdapter).setDataList(emptyList())

                viewModel.getSigunguCode(sidoCodes[position].code)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spnSmallArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position2: Int,
                id: Long
            ) {
                if (position2 == 0) return
                binding.tvRequireSelection.visibility = View.GONE
                sigunguPosition = position2

                viewModel.getTourFcltList(type ?: "", sidoCodes[sidoPosition].code, sigunguCodes[sigunguPosition].code)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        with(binding.rvList) {
            adapter = InfoSquareAdapter().apply {
                setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val item = fcltList[position]
                        val bundle = Bundle()
                        bundle.putString(CONTENT_ID, item.contentId)
                        bundle.putString(CONTENT_TYPE, CONTENT_TYPE_STAY)
                        navController.navigate(
                            resId = R.id.stayInfoFragment,
                            args = bundle
                        )
                    }
                })
                setOnLikeClickListener(object : OnLikeClickListener {
                    override fun onLikeClick(position: Int) {
                        val item = fcltList.getOrNull(position) ?: return

                        type?.let {
                            viewModel.postLikes(contentType = it, contentId = item.contentId, likes = if (item.like) 0 else 1)
                        }
                    }

                })
            }
            layoutManager = GridLayoutManager(context, 2)
            if (itemDecorationCount < 1) addItemDecoration(StayListItemViewHolderDecoration())
        }

        viewModel.locationList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                Log.d(TAG, "null near-hotel data")
            }
            fcltList.clear()
            fcltList.addAll(it)
            (binding.rvList.adapter as InfoSquareAdapter).setDataList(fcltList)
            scrollState?.let { state ->
                binding.rvList.layoutManager?.onRestoreInstanceState(state)
            }
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                loadingProgressBar.show()
            } else {
                loadingProgressBar.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getTourFcltList(type ?: "", sidoCodes[sidoPosition].code, sigunguCodes[sigunguPosition].code)
    }

    override fun onPause() {
        super.onPause()
        scrollState = binding.rvList.layoutManager?.onSaveInstanceState()
    }

    private fun initTitle() {
        binding.tvTitle.text = when {
            type.equals(CONTENT_TYPE_STAY) -> "숙박"
            type.equals(CONTENT_TYPE_TOUR) -> "관광지"
            else -> "음식점"
        }
    }

    private fun initSpinner() {
        binding.spnBigArea.adapter = BFTSpinnerAdapter(requireContext(), R.layout.item_spinner_tv, sidoNames)
        binding.spnBigArea.setSelection(0)
        binding.spnBigArea.isEnabled = true

        binding.spnSmallArea.adapter = BFTSpinnerAdapter(requireContext(), R.layout.item_spinner_tv, sigunguNames)
        binding.spnSmallArea.setSelection(0)
        binding.spnSmallArea.isEnabled = false
    }

    companion object {
        private const val TAG = "StayListFragment"
    }
}


