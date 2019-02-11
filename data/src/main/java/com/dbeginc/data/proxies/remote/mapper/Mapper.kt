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

import com.dbeginc.data.proxies.remote.list.RemoteItemComment
import com.dbeginc.data.proxies.remote.list.RemoteShoppingItem
import com.dbeginc.data.proxies.remote.list.RemoteShoppingList
import com.dbeginc.data.proxies.remote.list.RemoteShoppingUser
import com.dbeginc.data.proxies.remote.user.RemoteUserProfile
import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.user.UserProfile
import com.google.firebase.database.DataSnapshot

fun ShoppingList.toRemoteProxy(): RemoteShoppingList = RemoteShoppingList(
        uniqueId = uniqueId,
        name = name,
        ownerId = ownerId,
        ownerName = ownerName
)

fun ShoppingUser.toRemoteProxy() : RemoteShoppingUser = RemoteShoppingUser(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun ShoppingItem.toRemoteProxy() : RemoteShoppingItem = RemoteShoppingItem(
        uniqueId = uniqueId,
        name = name,
        itemOf = itemOf,
        itemOwner = itemOwner,
        count = count,
        price = price,
        brought = bought,
        boughtBy = boughtBy,
        imageUrl = imageUrl
)

fun ItemComment.toRemoteProxy() : RemoteItemComment = RemoteItemComment(
        uniqueId = uniqueId,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)

fun UserProfile.toRemoteProxy() : RemoteUserProfile {

    return RemoteUserProfile(
            uniqueId = uniqueId,
            nickname = nickname,
            email = email,
            avatar = avatar,
            creationDate = creationDate,
            birthday = birthday
    )
}

fun RemoteShoppingList.toDomain(): ShoppingList = ShoppingList(
        uniqueId = uniqueId,
        name = name,
        ownerId = ownerId,
        ownerName = ownerName,
        lastChange = lastChange as Long
)

fun RemoteShoppingUser.toDomain(): ShoppingUser = ShoppingUser(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun RemoteShoppingItem.toDomain(): ShoppingItem = ShoppingItem(
        uniqueId = uniqueId,
        name = name,
        itemOf = itemOf,
        itemOwner = itemOwner,
        count = count,
        price = price,
        bought = brought,
        boughtBy = boughtBy,
        imageUrl = imageUrl
)

fun RemoteItemComment.toDomain() : ItemComment = ItemComment(
        uniqueId = uniqueId,
        publishTime = publishTime as Long,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)

fun RemoteUserProfile.toDomain() : UserProfile = UserProfile(
        uniqueId = uniqueId,
        nickname = nickname,
        email = email,
        avatar = avatar,
        creationDate = creationDate as Long,
        birthday = birthday
)

fun DataSnapshot.toItemComment() : ItemComment {
    val proxy : RemoteItemComment = getValue(RemoteItemComment::class.java) as RemoteItemComment
    return proxy.toDomain()
}

fun DataSnapshot.toUser() : UserProfile {
    val proxy : RemoteUserProfile = getValue(RemoteUserProfile::class.java) as RemoteUserProfile
    return proxy.toDomain()
}

fun DataSnapshot.toShoppingUser() : ShoppingUser {
    val proxy : RemoteShoppingUser = getValue(RemoteShoppingUser::class.java) as RemoteShoppingUser
    return proxy.toDomain()
}