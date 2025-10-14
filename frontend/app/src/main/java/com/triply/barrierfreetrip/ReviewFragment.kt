package com.triply.barrierfreetrip

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_TITLE
import com.triply.barrierfreetrip.adapter.ReviewAdapter
import com.triply.barrierfreetrip.adapter.decoration.ReviewViewHolderDecoration
import com.triply.barrierfreetrip.databinding.FragmentReviewBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    private var contentId = ""
    private var title = ""
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(CONTENT_ID)?.let {
            contentId = it
        }
        arguments?.getString(CONTENT_TITLE)?.let {
            title = it
        }
    }

    override fun initInViewCreated() {
        val navController = findNavController()
        viewModel.getReviews(contentId)

        with(binding) {
            // 제목 설정
            tvContentTitle.text = title

            ivReviewBack.setOnClickListener {
                navController.navigateUp()
            }

            // 리뷰 리사이클러뷰 설정
            rvReview.adapter = ReviewAdapter()
            rvReview.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            if (rvReview.itemDecorationCount == 0) {
                rvReview.addItemDecoration(ReviewViewHolderDecoration())
            }

            // 리뷰 작성 버튼
            btnWritingReview.setOnClickListener {
                val bundle = Bundle()

                bundle.putString(CONTENT_ID, contentId)
                navController.navigate(
                    resId = R.id.reviewWritingFragment,
                    args = bundle
                )
            }
        }

        viewModel.reviews.observe(viewLifecycleOwner) {
            val totalReviewCnt = it.totalCnt
            val reviews = it.reviews
            (binding.rvReview.adapter as ReviewAdapter).setDataList(reviews)
            (binding.tvReviewCount).text = totalReviewCnt.toString()
        }
    }
}