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

package com.dbeginc.dbshopping.listitems.presenter

import com.dbeginc.dbshopping.listitems.ListItemsContract
import com.dbeginc.dbshopping.mapper.data.toItem
import com.dbeginc.dbshopping.mapper.data.toItemModel
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.data.item.AddItem
import com.dbeginc.domain.usecases.data.item.DeleteItem
import com.dbeginc.domain.usecases.data.item.GetItems
import com.dbeginc.domain.usecases.data.item.UpdateItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subjects.PublishSubject
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
 * Created by darel on 24.08.17.
 */
class ListItemsPresenterImpl(dataRepo: IDataRepo) : ListItemsContract.ListItemPresenter {

    private lateinit var view: ListItemsContract.ListItemsView
    private val getAllItems = GetItems(dataRepo)
    private val addItem = AddItem(dataRepo)
    private val updateItem = UpdateItem(dataRepo)
    private val deleteItem = DeleteItem(dataRepo)
    private val subscriptions = CompositeDisposable()
    val itemUpdate : PublishSubject<ItemModel> = PublishSubject.create<ItemModel>()

    override fun bind(view: ListItemsContract.ListItemsView) {
        this.view = view
        this.view.setupView()
        subscriptions.add(
                itemUpdate.subscribe(
                        { item -> updateItem(item) },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                )
        )
    }

    override fun unBind() {
        getAllItems.dispose()
        addItem.dispose()
        updateItem.dispose()
        deleteItem.dispose()
        subscriptions.clear()
    }

    override fun loadItems() {
        view.displayLoadingStatus()
        getAllItems.execute(ItemsObserver(), ItemRequestModel(view.getListId(), Unit))
    }

    override fun updateItem(item: ItemModel) {
        view.displayUpdatingStatus()
        updateItem.execute(UpdateObserver(), ItemRequestModel(view.getListId(), item.toItem()))
    }

    override fun removeItem(position: Int) {
        val id = view.getItemAtPosition(position).id

        view.displayUpdatingStatus()

        view.removeItem(position)

        if (position == 0 && view.getItemsSize() == 0) view.displayNoItemsMessage()

        deleteItem.execute(RemoveItemObserver(), ItemRequestModel(view.getListId(), id))
    }

    override fun addItem() {
        view.displayUpdatingStatus()
        val item = ShoppingItem(name = view.getDefaultItemName(), itemOf = view.getListId())

        view.hideNoItemsMessage()

        view.addItem(item.toItemModel())

        addItem.execute(AddItemObserver(), ItemRequestModel(view.getListId(), item))
    }

    override fun onShoppingStatusChange(isOn: Boolean) {
        if (isOn) view.enableShoppingMode()
        else view.disableShoppingMode()
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideUpdatingStatus()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideUpdatingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class RemoveItemObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideUpdatingStatus()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideUpdatingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class AddItemObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideUpdatingStatus()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideUpdatingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class ItemsObserver : DisposableSubscriber<List<ShoppingItem>>() {
        override fun onComplete() = dispose()

        override fun onNext(items: List<ShoppingItem>) {
            val models = mutableListOf<ItemModel>()

            if (items.isEmpty()) view.displayNoItemsMessage()
            else {
                items.mapTo(models) { item -> item.toItemModel() }
                view.hideNoItemsMessage()
            }

            view.hideLoadingStatus()
            view.displayItems(models)
        }

        override fun onError(error: Throwable) {
            view.hideLoadingStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
    }
}