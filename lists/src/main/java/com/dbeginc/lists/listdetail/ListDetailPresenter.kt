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

package com.dbeginc.lists.listdetail

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.lists.viewmodels.ItemModel
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel

/**
 * Created by darel on 26.02.18.
 *
 * List detail presenter
 */
class ListDetailPresenter : MVPVPresenter<ListDetailView> {
    @Volatile var listId: String = ""

    override fun bind(view: ListDetailView) = view.setupView()

    fun checkIfUserHasAdminRightOnList(currentUserId: String, list: ListModel, view: ListDetailView) {
        if (list.ownerId == currentUserId) view.showAdminActions()
        else view.hideAdminActions()
    }

    fun isCurrentUserShopping(currentUserHisName: String, members: List<ShoppingUserModel>): Boolean =
            members.find { it.nickname == currentUserHisName }?.isShopping ?: false

    fun canUserEditItem(nickname: String, email: String, item: ItemModel) : Boolean {
        return if (item.bought) item.boughtBy == nickname || item.itemOwner == email
        else true
    }
}
