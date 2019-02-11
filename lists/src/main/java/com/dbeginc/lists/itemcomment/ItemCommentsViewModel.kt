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

package com.dbeginc.lists.itemcomment

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
import com.dbeginc.lists.viewmodels.*
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 09.03.18.
 *
 * Item comment view model
 */
class ItemCommentsViewModel @Inject constructor(private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _item : MutableLiveData<ItemModel> = MutableLiveData()
    private val itemResponseListener = BehaviorRelay.create<ItemModel>()
    private var _comments : MutableLiveData<List<CommentModel>> = MutableLiveData()
    private val listResponseListener = BehaviorRelay.create<List<CommentModel>>()
    val presenter: ItemCommentsPresenter = ItemCommentsPresenter()

    init {
        itemResponseListener.subscribe(_item::postValue)
        listResponseListener.subscribe(_comments::postValue)
    }

    fun getItem() : LiveData<ItemModel> = _item

    fun getComments() : LiveData<List<CommentModel>> = _comments

    fun loadAllItemComments(listId: String, itemId: String) {
        dataRepo.getAllItemComments(ItemRequestModel(listId, itemId))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { comments -> comments.map { comment -> comment.toUI() } }
                .map { comments -> comments.sortByPublishTime() }
                .observeOn(threads.UI)
                .doAfterNext {
                    getModifiableLastRequest().onNext(RequestType.GET)
                    requestState.postValue(RequestState.COMPLETED)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(listResponseListener)
                .addTo(subscriptions)
    }

    fun loadItem(listId: String, itemId: String) {
        dataRepo.getItem(ItemRequestModel(listId, itemId))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { it.toUI() }
                .observeOn(threads.UI)
                .doAfterNext {
                    getModifiableLastRequest().onNext(RequestType.GET)
                    requestState.postValue(RequestState.COMPLETED)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(itemResponseListener)
                .addTo(subscriptions)
    }

    fun postComment(listId: String, comment: CommentModel) {
        dataRepo.postItemComment(ItemRequestModel(listId, comment.toDomain()))
                .doOnSubscribe {
                    requestState.postValue(RequestState.LOADING)
                    getModifiableLastRequest().onNext(RequestType.ADD)
                }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) }
                        ,
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
                .addTo(subscriptions)
    }
}