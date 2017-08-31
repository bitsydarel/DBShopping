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

package com.dbeginc.dbshopping.listitems.adapter

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
import com.dbeginc.dbshopping.viewmodels.ItemModel

/*
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
 */
interface ItemContract {

    interface ItemView : IView {
        fun displayItem(item: ItemModel)

        fun setUpOnClick(presenter: ItemContract.ItemPresenter)

        fun displayItemDetail(item: ItemModel)

        fun displayItemBroughtBy(currentUserName: String)

        fun hideItemBroughtBy()

        fun enableShoppingMode()

        fun disableShoppingMode()

        fun showError(e: Throwable)
    }

    interface ItemPresenter : IPresenter<ItemView> {
        fun getData() : ItemModel

        fun loadItem()

        fun onItemClick()

        fun onItemChecked(checked: Boolean)
    }

}