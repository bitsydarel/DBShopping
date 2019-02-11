/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping.utils.customviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.utils.extensions.getAccentColor
import com.dbeginc.dbshopping.utils.extensions.getTextColorForAccent

/**
 * Created by darel on 05.03.18.
 *
 * Icon badge drawable
 */
class BadgeDrawable(context: Context) : Drawable() {
    private val _badgePaint : Paint = Paint()
    private val _textPaint : Paint = Paint()
    private val _textRect : Rect = Rect()
    private var _badgeCount = ""
    private var _shouldNotDraw = false

    init {
        _badgePaint.color = context.getAccentColor()
        _badgePaint.isAntiAlias = true
        _badgePaint.style = Paint.Style.FILL

        _textPaint.color = context.getTextColorForAccent()
        _textPaint.typeface = Typeface.DEFAULT_BOLD
        _textPaint.textSize = context.resources.getDimension(R.dimen.badge_text_size)
        _textPaint.isAntiAlias = true
        _textPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (_shouldNotDraw) return

        val localRect : Rect = bounds

        val width = localRect.right - localRect.left
        val height = localRect.bottom - localRect.top

        val circleRadius: Float = if (_badgeCount.toInt() < 10) Math.min(width, height) / 4.0f + 2.5F else Math.min(width, height) / 4.0f + 4.5F

        val circleX = width - circleRadius + 6.2f

        val circleY = circleRadius - 9.5f

        canvas.drawCircle(circleX, circleY, circleRadius, _badgePaint)

        _textPaint.getTextBounds(_badgeCount, 0, _badgeCount.length, _textRect)

        var textX = circleX

        var textY = circleY + ((_textRect.bottom - _textRect.top) / 2f)

        if (_badgeCount.length > 2) {
            textX -= 0.5f
            textY -= 0.5f
        }

        canvas.drawText(if (_badgeCount.length > 2) "99+" else _badgeCount, textX, textY , _textPaint)
    }

    fun setCount(count: Int) {
        _badgeCount = count.toString()

        _shouldNotDraw = count == 0

        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.UNKNOWN

    override fun setColorFilter(colorFilter: ColorFilter?) {}

}