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

package com.dbeginc.dbshopping.utils.extensions

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dbeginc.dbshopping.R
import com.dbeginc.lists.viewmodels.ItemModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.*

fun View.isVisible() = visibility == View.VISIBLE

fun View.remove() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hideWithAnimation() {
    animate().alpha(0F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
}

fun View.showWithAnimation() {
    animate().alpha(1F)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .start()
}

fun View.toast(message: String = "", resId: Int = 0, duration: Int = Toast.LENGTH_LONG) {
    if (resId != 0) Toast.makeText(context, resId, duration).show()
    else Toast.makeText(context, message, duration).show()
}

fun ViewGroup.snack(message: String = "", resId: Int = 0, duration: Int = Snackbar.LENGTH_LONG) {
    if (resId != 0) Snackbar.make(this, resId, duration).show()
    else Snackbar.make(this, message, duration).show()
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

@BindingAdapter("setAvatarLogo")
fun setAvatarLogo(toolbar: Toolbar, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        val toolbarTarget = object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                toolbar.logo = resource
            }
        }

        Glide.with(toolbar.context)
                .load(Uri.parse(imageUrl))
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.errorOf(R.drawable.ic_avatar))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(toolbarTarget)
    }
}

@BindingAdapter("setItemImage")
fun setItemImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(imageView.context)
                .load(Uri.parse(imageUrl))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.errorOf(R.drawable.ic_add_picture))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(imageView)
    }
}

@BindingAdapter("setCircleItemImage")
fun setCircleItemImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrBlank()) {
        Glide.with(imageView.context)
                .load(Uri.parse(imageUrl))
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.errorOf(R.drawable.ic_add_picture))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.skipMemoryCacheOf(false))
                .into(imageView)
    }
}

@BindingAdapter("setUserImage")
fun setUserImage(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrBlank()) {
        Glide.with(imageView.context)
                .load(Uri.parse(imageUrl))
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.errorOf(R.drawable.ic_avatar))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.skipMemoryCacheOf(false))
                .into(imageView)
    }
}

@BindingAdapter("setPrice")
fun setPrice(textView: TextView, item: ItemModel) {

    val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())

    formatter.maximumFractionDigits = 2

    textView.text = formatter.format(item.price).plus(" x ${item.count}")
}

@BindingAdapter("boughtBy")
fun setBoughtBy(textView: TextView, formatArg: String?) {
    if (formatArg.isNullOrBlank()) textView.text = textView.context.getString(R.string.itemBoughtBy, textView.context.getString(R.string.noOneYet))
    else textView.text = textView.context.getString(R.string.itemBoughtBy, formatArg)
}

@BindingAdapter("boughtBy")
fun setBoughtBy(toolbar: android.support.v7.widget.Toolbar, formatArg: String?) {
    toolbar.subtitle = toolbar.context.getString(
            R.string.itemBoughtBy,
            if (formatArg.isNullOrBlank()) toolbar.context.getString(R.string.noOneYet)
            else toolbar.context.getString(R.string.itemBoughtBy, formatArg)
    )
}

@BindingAdapter(value = ["price", "count"], requireAll = true)
fun calculateTotalPrice(textView: TextView, price: Double, count: Long) {
    val totalAmount: Double = count * price

    val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())

    formatter.maximumFractionDigits = 2

    textView.text = textView.context.getString(R.string.itemTotalAmount, formatter.format(totalAmount))
}

@BindingAdapter(value = ["android:userComment"])
fun setText(textView: TextView, value: Long) {
    textView.text = value.toString()
}

@BindingAdapter(value = ["valueAttrChanged"])
fun setListener(editText: TextInputEditText, listener: InverseBindingListener?) {
    if (listener != null) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.endsWith('E', ignoreCase = true)) listener.onChange()
            }
        })
    }
}

@BindingAdapter(value = ["value"])
fun setEditTextValue(editText: TextInputEditText, value: String?) = editText.setText(value)

@InverseBindingAdapter(attribute = "value")
fun getEditTextValue(editText: TextInputEditText) : String = editText.getValue()

@BindingAdapter(value = ["value"])
fun setEditTextValue(editText: TextInputEditText, value: Double) = editText.setText(value.toString())

@InverseBindingAdapter(attribute = "value")
fun getEditTextDoubleValue(editText: TextInputEditText) : Double {
    val value = editText.getValue()

    return if (value.isBlank()) 0.0 else value.toDouble()
}

@BindingAdapter(value = ["value"])
fun setEditTextValue(editText: TextInputEditText, value: Long) = editText.setText(value.toString())

@InverseBindingAdapter(attribute = "value")
fun getEditTextLongValue(editText: TextInputEditText) : Long {
    val value = editText.getValue()
    return if (value.isBlank()) 0 else value.toLong()
}

@BindingAdapter("android:text")
fun setLongValue(textView: TextView, value: Long) {
    textView.text = value.toString()
}
