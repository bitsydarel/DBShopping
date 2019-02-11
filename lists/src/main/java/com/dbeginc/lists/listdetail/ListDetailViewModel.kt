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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.ItemRequestModel
import com.dbeginc.domain.entities.request.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.lists.viewmodels.*
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 26.02.18.
 *
 * List detail view model
 */
class ListDetailViewModel @Inject constructor(private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val listResponseListener = BehaviorRelay.create<Pair<ListModel, List<ShoppingUserModel>>>()
    private val itemsResponseListener = BehaviorRelay.create<List<ItemModel>>()
    private val _items = MutableLiveData<List<ItemModel>>()
    private val _listWithMembers = MutableLiveData<Pair<ListModel, List<ShoppingUserModel>>>()
    val presenter = ListDetailPresenter()

    init {
        listResponseListener.subscribe(_listWithMembers::postValue)
        itemsResponseListener.subscribe(_items::postValue)
    }

    fun getListWithMembers() : LiveData<Pair<ListModel, List<ShoppingUserModel>>> = _listWithMembers

    fun getItems() : LiveData<List<ItemModel>> = _items

    fun loadList(listId: String) {
        dataRepo.getList(ListRequestModel(listId, Unit))
                .flatMap { list ->
                    dataRepo.getListMembers(ListRequestModel(list.uniqueId, Unit))
                            .map { members -> list to members }
                }
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .map { listWithMembers -> listWithMembers.first.toUI() to listWithMembers.second.map { it.toUI() } }
                .observeOn(threads.UI)
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(listResponseListener)
                .addTo(subscriptions)
    }

    fun loadItems(listId: String) {
        dataRepo.getItems(ListRequestModel(listId, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { domainItems -> domainItems.map { domainItem -> domainItem.toUI() } }
                .observeOn(threads.UI)
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(itemsResponseListener)
                .addTo(subscriptions)
    }

    fun changeListName(listId: String, newName: String) {
        dataRepo.changeListName(ListRequestModel(listId, newName))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun updateListMember(listId: String, user: ShoppingUserModel) {
        dataRepo.updateListMember(ListRequestModel(listId, user.toDomain()))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun updateList(list: ListModel) {
        dataRepo.updateList(ListRequestModel(list.uniqueId, list.toDomain()))
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.UPDATE)
                }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun updateItem(item: ItemModel) {
        dataRepo.updateItem(ItemRequestModel(item.itemOf, item.toDomain()))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun deleteList(listId: String) {
        dataRepo.deleteList(ListRequestModel(listId, Unit))
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.DELETE)
                }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun deleteItem(item: ItemModel) {
        dataRepo.deleteItem(ItemRequestModel(item.itemOf, item.uniqueId))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }
}