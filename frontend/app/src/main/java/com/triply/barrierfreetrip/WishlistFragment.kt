package com.triply.barrierfreetrip

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_TYPE
import com.triply.barrierfreetrip.MainActivity.Companion.PAGE_TITLE
import com.triply.barrierfreetrip.adapter.BFTSpinnerAdapter
import com.triply.barrierfreetrip.adapter.InfoListAdapter
import com.triply.barrierfreetrip.adapter.OnItemClickListener
import com.triply.barrierfreetrip.adapter.OnLikeClickListener
import com.triply.barrierfreetrip.data.InfoListDto
import com.triply.barrierfreetrip.databinding.FragmentStaylistBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel

class WishlistFragment() : BaseFragment<FragmentStaylistBinding>(R.layout.fragment_staylist) {
    private val viewModel: MainViewModel by viewModels()
    private var type: String? = null
    private val infoList = arrayListOf<InfoListDto.InfoListItemDto>()
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }

    // sido data
    private val sidoNames = arrayListOf("시도 선택")
    private var sidoPosition = 0
    private var prevSidoPosition = 0

    // sigungu data
    private val sigunguNames = arrayListOf("구군 선택")
    private var sigunguPosition = 0
    private var prevSigunguPosition = 0

    // scroll position
    private var scrollState: Parcelable? = null

    private var isBigAreaSpinnerTouched = false
    private var isSmallAreaSpinnerTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString(CONTENT_TYPE)
    }

    override fun initInViewCreated() {
        initSpinner()
        val navController = findNavController()
        when {
            type.equals(TYPE_CARE_TOUR) -> {
                binding.tvTitle.text = resources.getString(R.string.all_caretrip)
                sidoNames.clear()
                sidoNames.addAll(resources.getStringArray(R.array.care_sido_nms))
            }

            type.equals(TYPE_CHARGER) -> {
                binding.tvTitle.text = resources.getString(R.string.title_charge)
                sidoNames.clear()
                sidoNames.addAll(resources.getStringArray(R.array.charger_sido_nms))
            }

            else -> {
                binding.tvTitle.text = resources.getString(R.string.home_rental)
                sidoNames.clear()
                sidoNames.addAll(resources.getStringArray(R.array.rental_sido_nms))
            }
        }

        binding.tvRequireSelection.visibility = View.VISIBLE

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.spnBigArea.setOnTouchListener { v, event ->
            isBigAreaSpinnerTouched = true
            false
        }

        binding.spnBigArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    binding.spnSmallArea.setSelection(position)
                    binding.spnSmallArea.isEnabled = false
                    isBigAreaSpinnerTouched = false
                    return
                }
                binding.tvRequireSelection.visibility = View.VISIBLE
                binding.spnSmallArea.isEnabled = true
                prevSidoPosition = sidoPosition
                sidoPosition = position
                if (isBigAreaSpinnerTouched) (binding.rvList.adapter as InfoListAdapter).setInfoList(emptyList())

                // setting sigungu data on second spinner
                if (prevSidoPosition != sidoPosition) {
                    val sigunguArray = when {
                        sidoNames[position] == "강원도" -> R.array.charger_sigungu_gw
                        sidoNames[position] == "광주광역시" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_gj
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_gj
                                else -> R.array.rental_sigungu_gj
                            }
                        }
                        sidoNames[position] == "대구광역시" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_dg
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_dg
                                else -> R.array.rental_sigungu_dg
                            }
                        }
                        sidoNames[position] == "경상북도" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_gb
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_gb
                                else -> R.array.rental_sigungu_gb
                            }
                        }
                        sidoNames[position] == "충청북도" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_cb
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_cb
                                else -> R.array.rental_sigungu_cb
                            }
                        }
                        sidoNames[position] == "부산광역시" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_bs
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_bs
                                else -> R.array.rental_sigungu_bs
                            }
                        }
                        sidoNames[position] == "경상남도" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_gn
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_gn
                                else -> R.array.rental_sigungu_gn
                            }
                        }
                        sidoNames[position] == "대전광역시" -> {
                            when {
                                type.equals(TYPE_CARE_TOUR) -> R.array.care_sigungu_dj
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_dj
                                else -> R.array.rental_sigungu_dj
                            }
                        }
                        sidoNames[position] == "경기도" -> {
                            when {
                                type.equals(TYPE_CHARGER) -> R.array.rental_sigungu_gyeonggi
                                else -> R.array.charger_sigungu_gyeonggi
                            }
                        }
                        sidoNames[position] == "서울특별시" -> {
                            when {
                                type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_seoul
                                else -> R.array.rental_sigungu_seoul
                            }
                        }
                        sidoNames[position] == "세종특별자치시" && type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_sj
                        sidoNames[position] == "울산광역시" && type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_ws
                        sidoNames[position] == "인천광역시" && type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_ic
                        sidoNames[position] == "전라북도" -> {
                            when {
                                type.equals(TYPE_RENTAL) -> R.array.rental_sigungu_jb
                                else -> R.array.charger_sigungu_jb
                            }
                        }
                        sidoNames[position] == "제주특별자치도" && type.equals(TYPE_CHARGER) -> R.array.charger_sigungu_jeju
                        sidoNames[position] == "충청남도" -> {
                            when {
                                type.equals(TYPE_RENTAL) -> R.array.rental_sigungu_cn
                                else -> R.array.charger_sigungu_cn
                            }
                        }
                        else -> R.array.care_sigungu_no_selected
                    }
                    sigunguNames.clear()
                    sigunguNames.add("구군 선택")
                    sigunguNames.addAll(resources.getStringArray(sigunguArray))
                    sigunguPosition = 0
                    binding.spnSmallArea.setSelection(sigunguPosition)
                }
                isBigAreaSpinnerTouched = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                isBigAreaSpinnerTouched = false
            }
        }

        binding.spnSmallArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) return
                binding.tvRequireSelection.visibility = View.GONE
                prevSigunguPosition = sigunguPosition
                sigunguPosition = position

                if (prevSigunguPosition != sigunguPosition) {
                    getFcltListData(
                        sidoNames.getOrElse(sidoPosition) { "" },
                        sigunguNames.getOrElse(sigunguPosition) { "" }
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        with(binding.rvList) {
            val infoListAdapter = InfoListAdapter()
            binding.rvList.adapter = infoListAdapter.apply {
                when (type) {
                    TYPE_CHARGER -> {
                        setThumbnailIcon(R.drawable.ic_home_charge)
                        setOnItemClickListener(object : OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val item = infoList.getOrNull(position)
                                item?.id?.let {
                                    val bundle = Bundle()

                                    bundle.putString(CONTENT_ID, it.toString())
                                    bundle.putString(PAGE_TITLE, resources.getString(R.string.title_charge))
                                    navController.navigate(
                                        resId = R.id.wishListMapFragment,
                                        args = bundle
                                    )
                                }
                            }
                        })
//                    setOnShowMapClickListener(object : OnShowMapClickListener {
//                        override fun onShowMapClick(position: Int) {
//                            // 맵 Fragment 활성화
//                        }
//                    })
//                    setShowMapVisibility(true)
                    }
                    TYPE_RENTAL -> {
                        setThumbnailIcon(R.drawable.ic_carelist_thumbnail)
                    }
                    TYPE_CARE_TOUR -> {
                        setThumbnailIcon(R.drawable.ic_carelist_thumbnail)
                    }
                }
                setOnLikeClickListener(object: OnLikeClickListener {
                    override fun onLikeClick(position: Int) {
                        val item = infoList.getOrNull(position) ?: return

                        type?.let {
                            viewModel.postLikes(contentType = it, contentId = item.id.toString(), likes = if (item.like) 0 else 1)
                        }
                    }
                })
            }
            binding.rvList.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        viewModel.fcltList.observe(viewLifecycleOwner) {
            infoList.clear()
            infoList.addAll(it)
            (binding.rvList.adapter as InfoListAdapter).setInfoList(infoList)
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

    override fun onPause() {
        super.onPause()
        isBigAreaSpinnerTouched = false
        scrollState = binding.rvList.layoutManager?.onSaveInstanceState()
    }

    private fun getFcltListData(sidoNm: String?, sigunguNm: String) {
        when (type) {
            TYPE_CARE_TOUR -> viewModel.getCareTourList(sidoNm.toString(), sigunguNm)
            TYPE_CHARGER -> viewModel.getChargerList(sidoNm.toString(), sigunguNm)
            else -> viewModel.getRentalServiceList(sidoNm.toString(), sigunguNm)
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
        private const val TAG = "WishlistFragment"
        private const val TYPE_CARE_TOUR = "1"
        private const val TYPE_CHARGER = "2"
        private const val TYPE_RENTAL = "3"
    }
}