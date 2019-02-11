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

import com.dbeginc.dbshopping.base.BaseDataDiff
import com.dbeginc.lists.viewmodels.ItemModel

/**
 * Created by darel on 24.08.17.
 *
 * Item Difference calculator
 */
class ItemDiffCallback(oldItems: List<ItemModel>, newItems: List<ItemModel>) : BaseDataDiff<ItemModel>(oldItems, newItems) {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldData[oldItemPosition].uniqueId == newData[newItemPosition].uniqueId

}
