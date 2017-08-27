package com.dbeginc.dbshopping.mapper.data

import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.ListModel
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList

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
 * Created by darel on 23.08.17.
 */

fun ShoppingList.toListModel() : ListModel = ListModel(uuid, name, ownerName, lastChange)

fun ListModel.toList() : ShoppingList = ShoppingList(id, name, owner, lastChange)

fun ShoppingItem.toItemModel() : ItemModel = ItemModel(uuid, name, itemOf, count, price, brought, image.uri)

fun ItemModel.toItem() : ShoppingItem = ShoppingItem(id, name, itemOf, count, price, brought, ItemImage(image))