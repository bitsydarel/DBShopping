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

package com.dbeginc.dbshopping.mapper.data

import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.ListModel
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList

/**
 * Created by darel on 23.08.17.
 * Application data mapping file
 * This file has function which help to convert
 */

fun ShoppingList.toListModel() : ListModel = ListModel(id = uuid, name = name, owner = ownerName,
        usersShopping = usersShopping, lastChange = lastChange
)

fun ListModel.toList() : ShoppingList = ShoppingList(id, name, owner, usersShopping, lastChange)

fun ShoppingItem.toItemModel() : ItemModel = ItemModel(id = uuid, name = name, itemOf = itemOf, itemOwner = itemOwner,
        count = count, price = price, bought = bought, boughtBy = boughtBy, image = image.uri
)

fun ItemModel.toItem() : ShoppingItem = ShoppingItem(uuid = id, name = name, itemOf = itemOf, itemOwner = itemOwner,
        count = count, price = price, bought = bought, boughtBy = boughtBy, image = ItemImage(image)
)