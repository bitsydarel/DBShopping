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

import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Build
import android.support.annotation.StringRes
import android.util.TypedValue
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import com.dbeginc.users.viewmodels.UserProfileModel
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 23.08.17.
 *
 * Android Extensions functions
 * Add Feature to framework classes
 */

fun Map<String, Any>.getUser(userId: String)  : UserProfileModel? = get(userId) as? UserProfileModel

fun UserProfileModel.toShoppingUser(isShopping: Boolean) : ShoppingUserModel = ShoppingUserModel(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun ViewDataBinding.getString(@StringRes restId: Int) : String = root.context.getString(restId)

fun Context.getAccentColor() : Int {
    val colorAccent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.colorAccent
    else resources.getIdentifier("colorAccent", "attr", packageName)

    val value = TypedValue()

    theme.resolveAttribute(colorAccent, value, true)

    return value.data
}

fun Context.getTextColorForAccent() : Int {
    val colorTextSecondary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.textColorSecondary
    else resources.getIdentifier("textColorSecondary", "attr", packageName)

    val value = TypedValue()

    theme.resolveAttribute(colorTextSecondary, value, true)

    return value.data
}


fun ListModel.getFormattedTime(context: Context) : String {
    val zoneId = ZoneId.of(TimeZone.getDefault().id)

    val currentTime = Instant.now().atZone(zoneId)

    val zonedLastChange = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(lastChange),
            zoneId
    )

    val difference = Duration.between(zonedLastChange, currentTime)

    return when {
        difference.toDays() > 0 -> context.getString(R.string.last_change_was_days_ago, difference.toDays())
        difference.toHours() > 0 -> context.getString(R.string.last_change_was_hours_ago, difference.toHours())
        difference.toMinutes() > 0 -> context.getString(R.string.last_change_was_minutes_ago, difference.toMinutes())
        else -> context.getString(R.string.last_change_was_seconds_ago, difference.seconds)
    }
}

fun Context.getNetworkClient() : OkHttpClient = OkHttpClient
        .Builder()
        .connectTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(35, TimeUnit.SECONDS)
        .readTimeout(55, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .cache(Cache(File(cacheDir, ConstantHolder.DBSHOPPING_NETWORK_CACHE), ConstantHolder.DBSHOPPING_NETWORK_CACHE_SIZE))
        .followSslRedirects(true)
        .build()