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

package com.dbeginc.dbshopping.listdetail.items

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by darel on 27.02.18.
 *
 * Item Touch Swipe to delete list item
 */
class SwipeToDeleteItem (private val actionBridge: ItemsAdapterBridge) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = (viewHolder as ItemsAdapter.ItemViewHolder).itemBinding.item!!

        actionBridge.onItemDeleted(item, viewHolder.adapterPosition)
    }

    override fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
        val item = (viewHolder as ItemsAdapter.ItemViewHolder).itemBinding.item!!

        return if (actionBridge.isCurrentUserTheCreatorOfTheItem(item)) super.getSwipeDirs(recyclerView, viewHolder) else 0
    }
}