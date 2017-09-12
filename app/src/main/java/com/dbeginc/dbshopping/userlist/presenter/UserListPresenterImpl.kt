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

import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.mapper.data.toListModel
import com.dbeginc.dbshopping.userlist.UserListContract
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.list.GetAllLists
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 22.08.17.
 *
 * User Lists Presenter
 */
class UserListPresenterImpl(dataRepo: IDataRepo) : UserListContract.UserListPresenter {

    private lateinit var view: UserListContract.UserListView
    private val getLists = GetAllLists(dataRepo)
    private val subscriptions = CompositeDisposable()

    override fun bind(view: UserListContract.UserListView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getLists.clean()
        subscriptions.clear()
    }

    override fun loadList() {
        getLists.execute(Unit)
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ lists ->
                    val formattedList = lists.map { list -> list.toListModel() }

                    if (formattedList.isEmpty()) {
                        view.hideUserLists()
                        view.displayNoListAvailableMessage()

                    } else {
                        view.showUserLists()
                        view.hideNoListAvailableMessage()
                    }
                    view.displayUserList(formattedList)

                }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }
}