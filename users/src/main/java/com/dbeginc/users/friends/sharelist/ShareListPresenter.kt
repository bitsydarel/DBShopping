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

package com.dbeginc.users.friends.sharelist

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.users.viewmodels.FriendModel

/**
 * Created by darel on 05.03.18.
 *
 * Friends Presenter
 */
class ShareListPresenter : MVPVPresenter<ShareListView>{
    override fun bind(view: ShareListView) = view.setupView()

    fun isQueryValid(username: String, view: ShareListView) {
        if (username.isNotBlank()) view.findUserWithName(username)
    }

    fun findUserWhoAreNotMemberOfThisList(users: List<FriendModel>, membersId: List<String>) : List<FriendModel> =
            users.filterNot { user -> membersId.contains(user.uniqueId) }
}