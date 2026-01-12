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

class WishlistViewHolderDecoration: RecyclerView.ItemDecoration() {
    private val paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        paint.color = parent.context.getColor(R.color.divider_gray)
        val left = parent.paddingLeft + 25F.toDp(parent.context)
        val right = parent.width - parent.paddingRight - 25F.toDp(parent.context)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val top = child.bottom
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