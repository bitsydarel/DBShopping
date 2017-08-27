package com.dbeginc.dbshopping.listitems

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.ListModel

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
 * Created by darel on 24.08.17.
 */
interface ListItemsContract {

    interface ListItemsView : IView {
        fun getListId() : String
        fun displayItems(items: List<ItemModel>)
        fun hideItems()
        fun displayNoItemsMessage()
        fun hideNoItemsMessage()
        fun addItem(item: ItemModel)
        fun removeItem(position: Int)
        fun getItemAtPosition(position: Int): ItemModel
        fun getDefaultItemName(): String
        fun getItemsSize(): Int
        fun displayLoadingStatus()
        fun hideLoadingStatus()
        fun displayUpdatingStatus()
        fun hideUpdatingStatus()
        fun displayErrorMessage(error: String)
    }

    interface ListItemPresenter : IPresenter<ListItemsView> {
        fun loadItems()

        fun updateItem(item: ItemModel)

        fun removeItem(position: Int)

        fun addItem()
    }
}