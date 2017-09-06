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

package com.dbeginc.data.proxies.local.mapper

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.implementations.datasources.local.LocalUserSourceImpl
import com.dbeginc.data.proxies.local.*
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import io.realm.RealmList
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun ShoppingList.toProxyList(wasSaveRemotely: Boolean = false) : LocalShoppingList {
    var timestamp = 0L

    if (lastChange.isNotEmpty()) {
        try {
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    .parse(lastChange)
                    .time

        } catch (pe: ParseException) {
            Log.e(ConstantHolder.TAG, "Error in ${LocalShoppingList::class.java.simpleName}: ${pe.localizedMessage}")
        }
    }

    return LocalShoppingList(uuid= uuid, name = name, ownerName = ownerName, lastChange = timestamp,
            savedInServer = wasSaveRemotely, usersShopping = usersShopping.mapTo(RealmList()) { userId -> RealmString(userId) }
    )
}

fun ShoppingItem.toProxyItem(wasSaveRemotely: Boolean = false) : LocalShoppingItem {

    return LocalShoppingItem(uuid = uuid, name = name, itemOf = itemOf, itemOwner = itemOwner,
            count = count, price = price, bought = bought, boughtBy = boughtBy, savedInServer = wasSaveRemotely, image = image.toProxyImage())
}

fun ItemImage.toProxyImage() : LocalItemImage = LocalItemImage(uri = uri)

fun User.toProxy() : LocalUser {
    var creationDate = 0L

    if (joinedAt.isNotEmpty()) {
        try {
            creationDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    .parse(joinedAt)
                    .time

        } catch (pe: ParseException) {
            Log.e(ConstantHolder.TAG, "Error in ${LocalUserSourceImpl::class.java.simpleName}: ${pe.localizedMessage}")
        }
    }

    return LocalUser(uuid = uuid, name = name, email = email, joinedAt = creationDate)
}

fun Account.toProxy() : LocalAccount {
    return LocalAccount(uuid = userId, name = name, profileImage = profileImage,
            accountProviders = accountProviders.mapTo(RealmList(), { provider -> RealmString(provider) }))
}
