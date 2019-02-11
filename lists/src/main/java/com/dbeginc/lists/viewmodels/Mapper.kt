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

package com.dbeginc.lists.viewmodels

import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by darel on 25.02.18.
 *
 * File containing extension function
 * to transform domain object to UI representation
 */

fun ShoppingList.toUI() : ListModel = ListModel(
        uniqueId = uniqueId,
        name = name,
        ownerId = ownerId,
        ownerName = ownerName,
        lastChange = lastChange
)

fun ListModel.toDomain() : ShoppingList = ShoppingList(
        uniqueId = uniqueId,
        name = name,
        ownerId = ownerId,
        ownerName = ownerName,
        lastChange = lastChange
)

fun ShoppingUser.toUI() : ShoppingUserModel = ShoppingUserModel(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun ShoppingUserModel.toDomain() : ShoppingUser = ShoppingUser(
        uniqueId = uniqueId,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isShopping = isShopping
)

fun ShoppingItem.toUI() : ItemModel = ItemModel(
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

fun ItemModel.toDomain() : ShoppingItem = ShoppingItem(
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

fun ItemComment.toUI() : CommentModel = CommentModel(
        uniqueId = uniqueId,
        publishTime = publishTime.toDateWithTime(),
        timestamp = publishTime,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)

fun CommentModel.toDomain() : ItemComment = ItemComment(
        uniqueId = uniqueId,
        publishTime = timestamp,
        comment = comment,
        commentType = commentType,
        commentArg = commentArg,
        itemId = itemId,
        userId = userId,
        userName = userName,
        userAvatar = userAvatar
)

fun Long.toDateWithTime() : String {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.of(TimeZone.getDefault().id))
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy hh:mm a"))
}

fun List<CommentModel>.sortByPublishTime() : List<CommentModel> {
    return sortedWith(Comparator { firstComment, secondComment ->
        Instant.ofEpochMilli(firstComment.timestamp)
                .compareTo(Instant.ofEpochMilli(secondComment.timestamp))
    })
}