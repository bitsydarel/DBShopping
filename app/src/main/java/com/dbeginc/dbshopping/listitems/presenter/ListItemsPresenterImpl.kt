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
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.data.item.AddItem
import com.dbeginc.domain.usecases.data.item.DeleteItem
import com.dbeginc.domain.usecases.data.item.GetItems
import com.dbeginc.domain.usecases.data.item.UpdateItem
import com.dbeginc.domain.usecases.data.list.AddUserShoppingList
import com.dbeginc.domain.usecases.data.list.GetList
import com.dbeginc.domain.usecases.data.list.RemoveUserShopping
import com.dbeginc.domain.usecases.user.GetUser
import com.dbeginc.domain.usecases.user.GetUsers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subscribers.DisposableSubscriber

class ListItemsPresenterImpl(userRepo: IUserRepo, dataRepo: IDataRepo) : ListItemsContract.ListItemPresenter {
    private lateinit var view: ListItemsContract.ListItemsView

    /************************ Use Cases ************************/

    /************************ User Cases ************************/
    private val getUser = GetUser(userRepo)
    private val getUsers = GetUsers(userRepo)

    /************************ Data Cases ************************/
    private val getShoppingUsers = GetList(dataRepo)
    private val getAllItems = GetItems(dataRepo)
    private val addItem = AddItem(dataRepo)
    private val updateItem = UpdateItem(dataRepo)
    private val deleteItem = DeleteItem(dataRepo)
    private val addUserShopping = AddUserShoppingList(dataRepo)
    private val removeUserShopping = RemoveUserShopping(dataRepo)

    /************************ Event publishers ************************/
    val itemUpdate : PublishSubject<ItemModel> = PublishSubject.create()

    /************************ Subscriptions ************************/
    private val subscriptions = CompositeDisposable()

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
        addUserShopping.dispose()
        removeUserShopping.dispose()
        subscriptions.clear()
    }

    /******************************************************************** Data Actions ********************************************************************/

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

    /******************************************************************** User Actions ********************************************************************/
    override fun defineUsersShopping() {
        getShoppingUsers.execute(UsersShoppingObserver(), ListRequestModel(view.getListId(), Unit))
    }

    override fun onShoppingStatusChange(isOn: Boolean) {
        view.displayUpdatingStatus()
        val requestParams = ListRequestModel(view.getListId(), view.getAppUser().id)

        if (isOn) addUserShopping.execute(AddUserShoppingObserver(), requestParams)
        else removeUserShopping.execute(RemoveUserShoppingObserver(), requestParams)
    }

    private fun getWhoIsShopping(userId: String) {
        view.showGettingUsersShoppingStatus()
        getUser.execute(WhoIsShoppingObserver(), UserRequestModel(userId, Unit))
    }

    private fun getWhoIsAlsoShopping(userId: String) {
        view.showGettingUsersShoppingStatus()
        getUser.execute(WhoIsAlsoShoppingObserver(), UserRequestModel(userId, Unit))
    }

    private fun getTheTwoUsersAreShopping(users: List<String>) {
        view.showGettingUsersShoppingStatus()
        getUsers.execute(TwoUsersAreShoppingObserver(), UserRequestModel(view.getAppUser().id, users))
    }

    private inner class WhoIsShoppingObserver : DisposableSubscriber<User>() {
        override fun onNext(user: User) {
            view.hideGettingUsersShoppingStatus()
            view.displayUserShopping(user.name)
            dispose()
        }
        override fun onError(error: Throwable) {
            view.hideGettingUsersShoppingStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
        override fun onComplete() = dispose()
    }

    private inner class WhoIsAlsoShoppingObserver : DisposableSubscriber<User>() {
        override fun onNext(user: User) {
            view.hideGettingUsersShoppingStatus()
            view.displayCurrentUserShoppingWith(user.name)
            dispose()
        }
        override fun onError(error: Throwable) {
            view.hideGettingUsersShoppingStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
        override fun onComplete() = dispose()
    }

    private inner class TwoUsersAreShoppingObserver : DisposableSubscriber<List<User>>() {
        override fun onNext(users: List<User>) {
            view.hideGettingUsersShoppingStatus()
            view.displayTheTwoUsersShopping(users[0].name, users[1].name)
            dispose()
        }
        override fun onError(error: Throwable) {
            view.hideGettingUsersShoppingStatus()
            view.displayErrorMessage(error.localizedMessage)
        }
        override fun onComplete() = dispose()
    }

    private inner class UsersShoppingObserver : DisposableSubscriber<ShoppingList>() {
        override fun onNext(list: ShoppingList) {
            val numberOfUsers = list.usersShopping.size

            when {
                list.usersShopping.contains(view.getAppUser().id) -> {
                    val currentUserPosition = list.usersShopping.indexOf(view.getAppUser().id)

                    when(numberOfUsers) {
                        1 -> view.displayCurrentUserShoppingAlone()
                        2 -> if (currentUserPosition == 0) getWhoIsAlsoShopping(userId = list.usersShopping[1]) else getWhoIsAlsoShopping(userId = list.usersShopping[0])
                        else -> view.displayCurrentUserShoppingWith(numberOfUsers - 1)
                    }

                }
                numberOfUsers > 0 -> when(numberOfUsers) {
                    1 -> getWhoIsShopping(list.usersShopping[0])
                    2 -> getTheTwoUsersAreShopping(list.usersShopping)
                    else -> view.displayUsersShopping(numberOfUsers)
                }
                else -> view.displayNoUserShopping()
            }
        }

        override fun onError(error: Throwable) {
            view.displayCouldNotFindUsersShopping()
            view.displayErrorMessage(error.localizedMessage)
        }
        override fun onComplete() = dispose()
    }

    private inner class AddUserShoppingObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideUpdatingStatus()
            view.enableShoppingMode()
            defineUsersShopping()
            dispose()
        }
        override fun onError(e: Throwable) {
            view.hideUpdatingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class RemoveUserShoppingObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideUpdatingStatus()
            view.disableShoppingMode()
            defineUsersShopping()
            dispose()
        }
        override fun onError(e: Throwable) {
            view.hideUpdatingStatus()
            view.displayErrorMessage(e.localizedMessage)
        }
    }
}