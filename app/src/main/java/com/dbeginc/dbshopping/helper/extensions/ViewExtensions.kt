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

package com.dbeginc.dbshopping.helper.extensions

import android.databinding.BindingAdapter
import android.net.Uri
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dbeginc.dbshopping.R
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable


fun View.invisible() {
    animate().alpha(0F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
    visibility = View.INVISIBLE
}

fun View.hide() {
    animate().alpha(0F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
    animate().alpha(1F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
}

fun ViewGroup.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}


fun EditText.getInputEvent() : Flowable<String> {
    return Flowable.create({ emitter ->
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!emitter.isCancelled) { emitter.onNext(s.toString()) }
            }

        })}, BackpressureStrategy.LATEST)
}

fun EditText.getValue() : String = text.toString()

fun Button.fadeIn() {
    animate().alpha(1F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
    isEnabled = true
}

fun Button.fadeOut() {
    animate().alpha(0F)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
    isEnabled = false
}

@BindingAdapter("setImageUrl")
fun setImageUrl(imageView: ImageView, image: String) {
    Glide.with(imageView.context)
            .load(Uri.parse(image))
            .apply(RequestOptions.centerCropTransform())
            .apply(RequestOptions.errorOf(R.drawable.ic_no_image))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .into(imageView)
}

@BindingAdapter("setCircleImage")
fun setRoundedImage(imageView: ImageView, image: String) {
    Glide.with(imageView.context)
            .load(Uri.parse(image))
            .apply(RequestOptions.circleCropTransform())
            .apply(RequestOptions.errorOf(R.drawable.ic_no_image))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .apply(RequestOptions.skipMemoryCacheOf(false))
            .into(imageView)
}

@BindingAdapter("setPrice")
fun setPrice(textView: TextView, value: Double) {
    textView.text = value.toString().plus('$')
}

@BindingAdapter("boughtBy")
fun setFormattedText(textView: TextView, formatArg: String) {
    textView.text = textView.context.getString(R.string.itemBoughtBy, formatArg)
}