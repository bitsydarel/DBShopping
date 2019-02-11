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

package com.dbeginc.lists.itemdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.lists.viewmodels.ItemModel
import com.dbeginc.lists.viewmodels.toDomain
import com.dbeginc.lists.viewmodels.toUI
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 28.02.18.
 *
 * Item detail viewModel
 */
class ItemDetailViewModel @Inject constructor(private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _item = MutableLiveData<ItemModel>()
    private val itemResponseListener = BehaviorRelay.create<ItemModel>()
    val presenter = ItemDetailPresenter()

    init {
        itemResponseListener.subscribe(_item::postValue)
    }

    fun getItem() : LiveData<ItemModel> = _item

    fun loadItem(listId: String, itemId: String) {
        dataRepo.getItem(ItemRequestModel(listId, itemId))
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .map { domainItem -> domainItem.toUI() }
                .observeOn(threads.UI)
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(itemResponseListener)
                .addTo(subscriptions)
    }

    fun changeItemImage(listId: String, item: ItemModel) {
        dataRepo.updateItemImage(ItemRequestModel(listId, item.toDomain()))
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.IMAGE)
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

    fun uploadItem(listId: String, item: ItemModel) {
        dataRepo.updateItem(ItemRequestModel(listId, item.toDomain()))
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

    fun deleteItem(listId: String, itemId: String) {
        dataRepo.deleteItem(ItemRequestModel(listId, itemId))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doOnComplete { getModifiableLastRequest().onNext(RequestType.DELETE) }
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