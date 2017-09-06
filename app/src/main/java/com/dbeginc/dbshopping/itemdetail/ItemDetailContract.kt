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

package com.dbeginc.dbshopping.itemdetail

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.dbshopping.viewmodels.UserModel

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
interface ItemDetailContract {

    interface ItemDetailView : IView {
        fun getItemId() : String
        fun getItem() : ItemModel
        fun requestImage()
        fun displayItem(itemModel: ItemModel)
        fun displayImage(imageUrl: String)
        fun addQuantity(value: Int)
        fun removeQuantity(value: Int)
        fun displayLoadingStatus()
        fun hideLoadingStatus()
        fun displayImageUploadDoneMessage()
        fun goToList()
        fun displayErrorMessage(error: String)
        fun itemNotBought()
        fun itemBought()
        fun getCurrentUser(): UserModel
        fun displayUpdateStatus()
        fun hideUpdateStatus()
        fun restrictUserToEditItemName()
        fun allowUserToEditItemName()
    }

    interface ItemDetailPresenter : IPresenter<ItemDetailView> {
        fun changeItemImage()
        fun onImageSelected(imageUrl: String)
        fun onItemBought(boolean: Boolean)
        fun setupRestrictions()
        fun addQuantity()
        fun removeQuantity()
        fun saveItemImage()
        fun updateItem()
        fun deleteItem()
    }
}