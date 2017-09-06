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

package com.dbeginc.data.proxies.remote.mapper

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.proxies.local.LocalShoppingList
import com.dbeginc.data.proxies.remote.*
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import com.google.firebase.database.ServerValue
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun ShoppingList.toProxy(): RemoteShoppingList {
    val shoppingUserIds = mutableMapOf<String, Boolean>()
    usersShopping.forEach { userId -> shoppingUserIds.put(userId, true)  }

    return RemoteShoppingList(uuid = uuid, name = name, ownerName = ownerName)
}

fun ShoppingItem.toProxy() : RemoteShoppingItem {
    return RemoteShoppingItem(uuid = uuid, name = name, itemOf = itemOf, itemOwner = itemOwner,
            count = count, price = price, brought = bought, boughtBy = boughtBy, image = image.toProxy()
    )
}

fun ItemImage.toProxy() : RemoteItemImage = RemoteItemImage(uri = uri)

fun User.toProxy() : RemoteUser {

    var creationTime : Any = ServerValue.TIMESTAMP

    if (joinedAt.isNotEmpty()) {
        try {
            creationTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    .parse(joinedAt)
                    .time

        } catch (pe: ParseException) {
            Log.e(ConstantHolder.TAG, "Error in ${LocalShoppingList::class.java.simpleName}: ${pe.localizedMessage}")
        }
    }

    return RemoteUser(uuid = uuid, name = name, email = email, joinedAt = creationTime)
}

fun Account.toProxy() : RemoteAccount {
    val providers = mutableMapOf<String, Boolean>()
    accountProviders.forEach { provider -> providers.put(provider, true) }
    return RemoteAccount(userId = userId, name = name, profileImage = profileImage, accountProviders = providers)
}
