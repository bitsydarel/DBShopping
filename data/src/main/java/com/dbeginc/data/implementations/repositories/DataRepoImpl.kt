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

package com.dbeginc.data.implementations.repositories

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.ThreadProvider
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

class DataRepoImpl(private val local: DataSource, private val remote: DataSource, private val schedulerProvider: ThreadProvider) : IDataRepo {

    private val subscriptions = CompositeDisposable()

    override fun addUserShopping(requestModel: ListRequestModel<String>): Completable {
        return local.addUserShopping(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.addUserShopping(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun removeUserShopping(requestModel: ListRequestModel<String>): Completable {
        return remote.removeUserShopping(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.removeUserShopping(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun getList(requestModel: ListRequestModel<Unit>): Flowable<ShoppingList> {
        return remote.getList(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { list -> subscriptions.addList(list) }
                .publish {
                    remoteData -> Flowable.merge(remoteData, local.getList(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }
                .observeOn(schedulerProvider.ui)
    }

    override fun getAllList(): Flowable<List<ShoppingList>> {
        return remote.getAllList()
                .subscribeOn(schedulerProvider.network)
                .doOnNext { lists -> lists.forEach { list -> subscriptions.addList(list) }}
                .publish {
                    remoteData -> Flowable.merge(remoteData, local.getAllList().takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }
                .observeOn(schedulerProvider.ui)
    }

    override fun getItems(requestModel: ItemRequestModel<Unit>): Flowable<List<ShoppingItem>> {
        return remote.getItems(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { items -> items.forEach { subscriptions.addItem(requestModel.listId, it) } }
                .publish {
                    remoteData -> Flowable.merge(remoteData, local.getItems(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }
                .observeOn(schedulerProvider.ui)
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Flowable<ShoppingItem> {
        return remote.getItem(requestModel)
                .subscribeOn(schedulerProvider.network)
                .doOnNext { item -> subscriptions.addItem(requestModel.listId, item) }
                .publish {
                    remoteData -> Flowable.merge(remoteData, local.getItem(requestModel).takeUntil(remoteData).subscribeOn(schedulerProvider.computation))
                }
                .observeOn(schedulerProvider.ui)
    }

    override fun addList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return local.addList(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.addList(requestModel).doOnComplete {
                    subscriptions.addList(requestModel.arg)

                }.subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return local.addItem(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.addItem(requestModel).doOnComplete {
                    subscriptions.addItem(requestModel.listId, requestModel.arg)

                }.subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        return local.changeListName(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.changeListName(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return local.updateList(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.updateList(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return local.updateItem(requestModel)
                .subscribeOn(schedulerProvider.computation)
                .andThen(remote.updateItem(requestModel).subscribeOn(schedulerProvider.network))
                .observeOn(schedulerProvider.ui)
    }

    override fun uploadItemImage(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return remote.uploadItemImage(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.uploadItemImage(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        return remote.deleteList(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.deleteList(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        return remote.deleteItem(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.deleteItem(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun deleteAll(requestModel: UserRequestModel<Unit>): Completable {
        return remote.deleteAll(requestModel)
                .subscribeOn(schedulerProvider.network)
                .andThen(local.deleteAll(requestModel).subscribeOn(schedulerProvider.computation))
                .observeOn(schedulerProvider.ui)
    }

    override fun clean() = subscriptions.clear()

    private fun CompositeDisposable.addList(list: ShoppingList) {
        val requestModel = ListRequestModel(listId = list.uuid, arg = list)
        add(local.updateList(requestModel).subscribeOn(schedulerProvider.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addItem(listId: String, item: ShoppingItem) {
        val requestModel = ItemRequestModel(listId = listId, arg = item)
        add(local.updateItem(requestModel).subscribeOn(schedulerProvider.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            Log.i(ConstantHolder.TAG, "Update of data done in ${DataRepoImpl::class.java.simpleName}")
        }
        override fun onError(e: Throwable) {
            Log.e(ConstantHolder.TAG, "Error in ${DataRepoImpl::class.java.simpleName}: ", e)
        }
    }
}