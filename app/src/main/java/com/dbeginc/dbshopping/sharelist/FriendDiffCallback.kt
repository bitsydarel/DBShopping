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

package com.dbeginc.dbshopping.sharelist

import android.support.v7.util.DiffUtil
import com.dbeginc.users.viewmodels.FriendModel

/**
 * Created by darel on 04.03.18.
 *
 * FriendProfile diff callback
 */
class FriendDiffCallback (private val oldItems: List<FriendModel>, private val newItems: List<FriendModel>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].uniqueId == newItems[newItemPosition].uniqueId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size
}