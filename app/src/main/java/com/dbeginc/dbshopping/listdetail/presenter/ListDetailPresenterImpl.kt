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

package com.dbeginc.dbshopping.listdetail.presenter

import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.listdetail.ListDetailContract
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.list.ChangeListName
import com.dbeginc.domain.usecases.data.list.DeleteList
import com.dbeginc.domain.usecases.data.list.GetList
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 23.08.17.
 *
 * List Detail Presenter
 */
class ListDetailPresenterImpl(dataRepo: IDataRepo) : ListDetailContract.ListDetailPresenter {

    private lateinit var view: ListDetailContract.ListDetailView
    private val getList = GetList(dataRepo)
    private val updateName = ChangeListName(dataRepo)
    private val deleteList = DeleteList(dataRepo)
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ListDetailContract.ListDetailView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getList.clean()
        updateName.clean()
        deleteList.clean()
        subscriptions.clear()
    }

    override fun loadItems() = view.displayItems()

    override fun changeName() = view.requestNewName()

    override fun updateList() {
        getList.execute(ListRequestModel(view.getListId(), Unit))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ list -> view.displayNewName(list.name) }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun updateName(value: String) {
        when {
            value.isEmpty() -> return
            value == view.getListName() -> return
            else -> updateName.execute(ListRequestModel(view.getListId(), value))
                    .doOnSubscribe { view.displayLoadingStatus() }
                    .subscribe (
                            {
                                getList.execute(ListRequestModel(view.getListId(), Unit))
                                        .doOnTerminate { view.hideLoadingStatus() }
                                        .subscribe({ list -> view.displayNewName(list.name) }, { e -> view.displayErrorMessage(e.localizedMessage)})
                                        .addTo(subscriptions)
                            }
                            ,{ error -> view.displayErrorMessage(error.localizedMessage) }
                    ).addTo(subscriptions)
        }
    }

    override fun isUserOwner(userEmail: String): Boolean = userEmail == view.getOwner()

    override fun deleteList() {
        deleteList.execute(ListRequestModel(view.getListId(), Unit))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ view.goToHomePage() }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }
}