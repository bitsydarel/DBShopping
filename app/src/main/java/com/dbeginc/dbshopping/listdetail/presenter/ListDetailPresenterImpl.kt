package com.dbeginc.dbshopping.listdetail.presenter

import com.dbeginc.dbshopping.listdetail.ListDetailContract
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.list.ChangeListName
import com.dbeginc.domain.usecases.data.list.DeleteList
import com.dbeginc.domain.usecases.data.list.GetList
import io.reactivex.observers.DisposableCompletableObserver
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
 * Created by darel on 23.08.17.
 */
class ListDetailPresenterImpl(dataRepo: IDataRepo) : ListDetailContract.ListDetailPresenter {
    private lateinit var view: ListDetailContract.ListDetailView
    private val getList = GetList(dataRepo)
    private val updateName = ChangeListName(dataRepo)
    private val deleteList = DeleteList(dataRepo)

    override fun bind(view: ListDetailContract.ListDetailView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getList.dispose()
        deleteList.dispose()
    }

    override fun loadItems() = view.displayItems()

    override fun changeName() = view.requestNewName()

    override fun updateList() {
        view.displayLoadingStatus()
        getList.execute(ListObserver(), ListRequestModel(view.getListId(), Unit))
    }

    override fun updateName(value: String) {
        when {
            value.isEmpty() -> return
            value == view.getListName() -> return
            else -> {
                view.displayLoadingStatus()
                updateName.execute(ListUpdateObserver(), ListRequestModel(view.getListId(), value))
            }
        }
    }

    override fun isUserOwner(userEmail: String): Boolean = userEmail == view.getOwner()

    override fun deleteList() {
        view.displayLoadingStatus()
        deleteList.execute(DeleteObserver(), ListRequestModel(view.getListId(), Unit))
    }

    private inner class ListUpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            getList.execute(ListObserver(), ListRequestModel(view.getListId(), Unit))
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideLoadingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class ListObserver : DisposableSubscriber<ShoppingList>() {
        override fun onNext(list: ShoppingList) {
            view.displayNewName(list.name)
            view.hideLoadingStatus()
        }

        override fun onComplete() = dispose()

        override fun onError(error: Throwable) {
            view.hideLoadingStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
    }

    private inner class DeleteObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            dispose()
            view.hideLoadingStatus()
            view.goToHomePage()
        }

        override fun onError(e: Throwable) {
            view.hideLoadingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }
}