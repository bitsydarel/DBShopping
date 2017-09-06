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

package com.dbeginc.dbshopping.userlist.presenter

import com.dbeginc.dbshopping.mapper.data.toListModel
import com.dbeginc.dbshopping.userlist.UserListContract
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.list.GetAllList
import io.reactivex.subscribers.DisposableSubscriber

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
 * Created by darel on 22.08.17.
 */
class UserListPresenterImpl(dataRepo: IDataRepo) : UserListContract.UserListPresenter {

    private lateinit var view: UserListContract.UserListView
    private val getLists = GetAllList(dataRepo)

    override fun bind(view: UserListContract.UserListView) {
        this.view = view
        this.view.setupView()
    }

    override fun loadList() {
        view.displayLoadingStatus()
        getLists.execute(ListsObserver(), Unit)
    }

    override fun unBind() = getLists.dispose()

    private inner class ListsObserver : DisposableSubscriber<List<ShoppingList>>() {
        override fun onNext(lists: List<ShoppingList>)  {
            val formattedList = lists.map { list -> list.toListModel() }

            if (formattedList.isEmpty()) {
                view.hideUserLists()
                view.displayNoListAvailableMessage()

            } else {
                view.showUserLists()
                view.hideNoListAvailableMessage()
            }

            view.hideLoadingStatus()
            view.displayUserList(formattedList)
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }

}