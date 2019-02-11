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

import com.dbeginc.data.proxies.local.list.LocalItemComment
import com.dbeginc.data.proxies.local.list.LocalShoppingItem
import com.dbeginc.data.proxies.local.list.LocalShoppingList
import com.dbeginc.data.proxies.local.list.LocalShoppingUser
import com.dbeginc.data.proxies.local.user.LocalFriend
import com.dbeginc.data.proxies.local.user.LocalUserProfile
import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.UserProfile


fun ShoppingList.toLocalProxy() : LocalShoppingList = LocalShoppingList(
        uniqueId=uniqueId,
        name= name,
        ownerId=ownerId,
        ownerName = ownerName,
        lastChange=lastChange
)

fun LocalShoppingList.toDomain() : ShoppingList = ShoppingList(
        uniqueId = uniqueId,
        name = name,
        ownerId = ownerId,
        ownerName = ownerName,
        lastChange = lastChange
)

fun ShoppingItem.toLocalProxy() : LocalShoppingItem = LocalShoppingItem(
        uniqueId=uniqueId,
        name=name,
        itemOf=itemOf,
        itemOwner=itemOwner,
        count=count,
        price=price,
        bought=bought,
        boughtBy=boughtBy,
        imageUrl =imageUrl
)

fun LocalShoppingItem.toDomain() : ShoppingItem = ShoppingItem(
        uniqueId = uniqueId,
        name = name,
        itemOf = itemOf,
        itemOwner = itemOwner,
        count = count,
        price = price,
        bought = bought,
        boughtBy = boughtBy,
        imageUrl = imageUrl
)

fun LocalShoppingUser.toDomain() : ShoppingUser = ShoppingUser(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun ShoppingUser.toLocalProxy(listId: String) = LocalShoppingUser(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        memberOf = listId,
        isShopping = isShopping
)

fun UserProfile.toLocalProxy() : LocalUserProfile = LocalUserProfile(
        uniqueId=uniqueId,
        nickname = nickname,
        email = email,
        avatar = avatar,
        creationDate = creationDate,
        birthday = birthday
)

fun LocalUserProfile.toDomain() : UserProfile = UserProfile(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        creationDate = creationDate,
        birthday = birthday
)

fun FriendProfile.toLocalProxy() : LocalFriend = LocalFriend(
        uniqueId = uniqueId,
        isFriendWithCurrentUser = isFriendWithCurrentUser,
        nickname = nickname,
        email = email,
        avatar = avatar,
        birthday = birthday
)

fun LocalFriend.toDomain() : FriendProfile = FriendProfile(
        uniqueId = uniqueId,
        isFriendWithCurrentUser = isFriendWithCurrentUser,
        nickname = nickname,
        email = email,
        avatar = avatar,
        birthday = birthday
)

fun ItemComment.toLocalProxy() : LocalItemComment = LocalItemComment(
        uniqueId = uniqueId,
        publishTime = publishTime,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)

fun LocalItemComment.toDomain() : ItemComment = ItemComment(
        uniqueId = uniqueId,
        publishTime = publishTime,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)