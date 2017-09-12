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

import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.listitems.ListItemsContract
import com.dbeginc.dbshopping.mapper.data.toItem
import com.dbeginc.dbshopping.mapper.data.toItemModel
import com.dbeginc.dbshopping.viewmodels.ItemModel
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
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
import io.reactivex.subjects.PublishSubject

class ListItemsPresenterImpl(userRepo: IUserRepo, dataRepo: IDataRepo) : ListItemsContract.ListItemPresenter {
    private lateinit var view: ListItemsContract.ListItemsView

    private val getUser = GetUser(userRepo)
    private val getUsers = GetUsers(userRepo)
    private val getShoppingUsers = GetList(dataRepo)
    private val getAllItems = GetItems(dataRepo)
    private val addItem = AddItem(dataRepo)
    private val updateItem = UpdateItem(dataRepo)
    private val deleteItem = DeleteItem(dataRepo)
    private val addUserShopping = AddUserShoppingList(dataRepo)
    private val removeUserShopping = RemoveUserShopping(dataRepo)
    val itemUpdate : PublishSubject<ItemModel> = PublishSubject.create()

    /************************ Subscriptions ************************/
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ListItemsContract.ListItemsView) {
        this.view = view
        this.view.setupView()
        itemUpdate.subscribe(
                { item -> updateItem(item) },
                { error -> view.displayErrorMessage(error.localizedMessage) }
        ).addTo(subscriptions)
    }

    override fun unBind() {
        getUser.clean()
        getUsers.clean()
        getShoppingUsers.clean()
        getAllItems.clean()
        addItem.clean()
        updateItem.clean()
        deleteItem.clean()
        addUserShopping.clean()
        removeUserShopping.clean()
        subscriptions.clear()
    }

    /******************************************************************** Data Actions ********************************************************************/

    override fun loadItems() {
        getAllItems.execute(ItemRequestModel(view.getListId(), Unit))
                .doOnSubscribe { view.displayLoadingStatus() }
                .doOnTerminate { view.hideLoadingStatus() }
                .subscribe({ items ->
                    val models = mutableListOf<ItemModel>()

                    if (items.isEmpty()) view.displayNoItemsMessage()
                    else {
                        items.mapTo(models) { item -> item.toItemModel() }
                        view.hideNoItemsMessage()
                    }
                    view.displayItems(models)

                }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun updateItem(item: ItemModel) {
        updateItem.execute(ItemRequestModel(view.getListId(), item.toItem()))
                .doOnSubscribe { view.displayUpdatingStatus() }
                .doOnTerminate { view.hideUpdatingStatus() }
                .subscribe({ /*Not Needed*/ }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun removeItem(position: Int) {
        val id = view.getItemAtPosition(position).id
        view.removeItem(position)
        if (position == 0 && view.getItemsSize() == 0) view.displayNoItemsMessage()

        deleteItem.execute(ItemRequestModel(view.getListId(), id))
                .doOnSubscribe { view.displayUpdatingStatus() }
                .doOnTerminate { view.hideUpdatingStatus() }
                .subscribe({ /*Not Needed*/}, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun addItem() {
        val item = ShoppingItem(name = view.getDefaultItemName(), itemOf = view.getListId(), itemOwner = view.getAppUser().email)

        view.hideNoItemsMessage()

        view.addItem(item.toItemModel())

        addItem.execute(ItemRequestModel(view.getListId(), item))
                .doOnSubscribe { view.displayUpdatingStatus() }
                .doOnTerminate { view.hideUpdatingStatus() }
                .subscribe({ /*Not Needed*/ }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    /******************************************************************** User Actions ********************************************************************/
    override fun defineUsersShopping() {
        getShoppingUsers.execute(ListRequestModel(view.getListId(), Unit))
                .doOnSubscribe { view.displayUpdatingStatus() }
                .doOnTerminate { view.hideUpdatingStatus() }
                .doOnError { view.displayCouldNotFindUsersShopping() }
                .subscribe(
                        { list ->
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
                        }, { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscriptions)
    }

    override fun onShoppingStatusChange(isOn: Boolean) {
        val requestParams = ListRequestModel(view.getListId(), view.getAppUser().id)

        if (isOn) {
            addUserShopping.execute(requestParams)
                    .doOnSubscribe { view.displayUpdatingStatus() }
                    .doAfterTerminate { view.hideUpdatingStatus() }
                    .subscribe({
                        view.enableShoppingMode()
                        defineUsersShopping()

                    }, { error -> view.displayErrorMessage(error.localizedMessage) })
                    .addTo(subscriptions)
        } else {
            removeUserShopping.execute(requestParams)
                    .doOnSubscribe { view.displayUpdatingStatus() }
                    .doAfterTerminate { view.hideUpdatingStatus() }
                    .subscribe({
                        view.disableShoppingMode()
                        defineUsersShopping()
                    }, { error -> view.displayErrorMessage(error.localizedMessage) })
                    .addTo(subscriptions)
        }
    }

    private fun getWhoIsShopping(userId: String) {
        getUser.execute(UserRequestModel(userId, Unit))
                .doOnSubscribe { view.showGettingUsersShoppingStatus() }
                .doOnTerminate { view.hideGettingUsersShoppingStatus() }
                .subscribe(
                        { user -> view.displayUserShopping(user.name) },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscriptions)
    }

    private fun getWhoIsAlsoShopping(userId: String) {
        getUser.execute(UserRequestModel(userId, Unit))
                .doOnSubscribe { view.showGettingUsersShoppingStatus() }
                .doOnTerminate { view.hideGettingUsersShoppingStatus() }
                .subscribe(
                        { user -> view.displayCurrentUserShoppingWith(user.name) },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscriptions)
    }

    private fun getTheTwoUsersAreShopping(users: List<String>) {
        getUsers.execute(UserRequestModel(view.getAppUser().id, users))
                .doOnSubscribe { view.showGettingUsersShoppingStatus() }
                .doOnTerminate { view.hideGettingUsersShoppingStatus() }
                .subscribe(
                        { usersFound -> view.displayTheTwoUsersShopping(usersFound[0].name, usersFound[1].name) },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscriptions)
    }
}