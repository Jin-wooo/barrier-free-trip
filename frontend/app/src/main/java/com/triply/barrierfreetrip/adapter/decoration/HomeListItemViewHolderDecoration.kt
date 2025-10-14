package com.triply.barrierfreetrip.adapter.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.adapter.HomeInfoAdapter
import com.triply.barrierfreetrip.util.toDp
import kotlin.math.roundToInt

class HomeListItemViewHolderDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        when ((parent.adapter as HomeInfoAdapter).getItemViewType(position)) {
            HomeInfoAdapter.VIEWTYPE_TITLE -> {
                outRect.top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9F, parent.context.resources.displayMetrics).roundToInt()
            }
            HomeInfoAdapter.VIEWTYPE_NEARBY_STAY -> {
                outRect.left = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (position%2 == 0) 24F else 8F,
                    parent.context.resources.displayMetrics
                ).roundToInt()

                outRect.right = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (position%2 == 0) 8F else 24F,
                    parent.context.resources.displayMetrics
                ).roundToInt()

                outRect.top = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (position <= 3) 12F else 8F,
                    parent.context.resources.displayMetrics
                ).roundToInt()

                outRect.bottom = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (position < (parent.adapter as HomeInfoAdapter).infoList.filterIsInstance<HomeInfoAdapter.HomeInfoDTO.InfoSquare>().size) 8F else 32F,
                    parent.context.resources.displayMetrics
                ).roundToInt()
            }
        }
    }

    private val paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        paint.color = parent.context.getColor(R.color.divider_gray)
        val left = parent.paddingLeft + 25F.toDp(parent.context)
        val right = parent.width - parent.paddingRight - 25F.toDp(parent.context)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(child)
            if (pos == RecyclerView.NO_POSITION) continue

            val type = parent.adapter?.getItemViewType(pos)
            if (type != HomeInfoAdapter.VIEWTYPE_NEARBY_CHARGER) continue

            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + 2

            c.drawRect(
                left,
                top.toFloat(),
                right,
                bottom.toFloat(),
                paint
            )
        }
    }
}