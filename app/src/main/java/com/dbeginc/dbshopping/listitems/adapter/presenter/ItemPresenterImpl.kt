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

package com.dbeginc.dbshopping.listitems.adapter.presenter

import com.dbeginc.dbshopping.listitems.adapter.ItemContract
import com.dbeginc.dbshopping.viewmodels.ItemModel
import io.reactivex.subjects.PublishSubject

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
class ItemPresenterImpl(val item: ItemModel, private val currentUserName: String, private val updateEvent: PublishSubject<ItemModel>) : ItemContract.ItemPresenter{
    private lateinit var view: ItemContract.ItemView

    override fun bind(view: ItemContract.ItemView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() { /* Not needed here for viewHolder presenter, at least for now */ }

    override fun getData(): ItemModel = item

    override fun loadItem() = view.displayItem(item)

    override fun onItemClick() = view.displayItemDetail(item)

    override fun onItemChecked(checked: Boolean) {
        if (view.isShoppingEnabled()) {
            item.bought = checked

            /******* Set the name of who bought the item ****/
            if (checked) {
                item.boughtBy = currentUserName
                view.displayItemBroughtBy(currentUserName)

            } else {
                item.boughtBy = ""
                view.hideItemBroughtBy()
            }

            updateEvent.onNext(item)

        } else view.displayNotInShoppingModeMessage()
    }
}