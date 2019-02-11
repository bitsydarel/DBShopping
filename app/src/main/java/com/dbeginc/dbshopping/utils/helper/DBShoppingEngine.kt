package com.dbeginc.dbshopping.utils.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 25.08.17.
 */
class DBShoppingEngine : ImageEngine {

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView, uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.overrideOf(resize, resize))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
    }

    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView, uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.overrideOf(resize, resize))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean = true
}