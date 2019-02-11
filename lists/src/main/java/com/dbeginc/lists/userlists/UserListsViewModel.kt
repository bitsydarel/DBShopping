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

package com.dbeginc.lists.userlists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.ListRequestModel
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.lists.userlists.userlist.UserListPresenter
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import com.dbeginc.lists.viewmodels.toUI
import com.jakewharton.rxrelay2.*
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 25.02.18.
 *
 * UserProfile List View Model
 */
class UserListsViewModel @Inject constructor(private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val listsResponseListener = BehaviorRelay.create<List<ListModel>>()
    private val membersResponseListener = PublishRelay.create<Pair<String, List<ShoppingUserModel>>>().toSerialized()
    private val _lists = MutableLiveData<List<ListModel>>()
    private val _members = MutableLiveData<Pair<String, List<ShoppingUserModel>>>()
    val presenter: UserListPresenter = UserListPresenter()

    companion object {
        const val ORDER_BY_PUBLISH_TIME = 1
        const val ORDER_BY_LIST_NAME = 2
        const val ORDER_BY_OWNER_NAME = 3
    }

    init {
        listsResponseListener.subscribe(_lists::postValue)
        membersResponseListener.subscribe(_members::setValue)
    }

    fun getMembers() : LiveData<Pair<String, List<ShoppingUserModel>>> = _members

    fun getLists() : LiveData<List<ListModel>> = _lists

    fun loadLists(userId: String, sortingOrder: Int) {
        dataRepo.getAllListsFromUser(UserRequestModel(userId, Unit)) // get all lists
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { lists -> lists.sortedWith(Comparator { first, second ->
                    when(sortingOrder) {
                        ORDER_BY_PUBLISH_TIME -> first.lastChange.compareTo(second.lastChange)
                        ORDER_BY_LIST_NAME -> first.name.compareTo(second.name, ignoreCase = true)
                        ORDER_BY_OWNER_NAME -> first.ownerName.compareTo(second.ownerName, ignoreCase = true)
                        else -> first.lastChange.compareTo(second.lastChange)
                    } })

                }
                .map { lists -> if (sortingOrder == ORDER_BY_PUBLISH_TIME) lists.asReversed() else lists }
                .map { lists -> lists.map { list -> list.toUI() } }
                .observeOn(threads.UI)
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(listsResponseListener)
                .addTo(subscriptions)
    }

    fun findList(possibleListName: String, userId: String) {
        dataRepo.getAllListsFromUser(UserRequestModel(userId, Unit)) // get all lists
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { lists -> lists.filter { it.name.contains(possibleListName) } }
                .map { lists -> lists.map { it.toUI() } }
                .observeOn(threads.UI)
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(listsResponseListener)
                .addTo(subscriptions)
    }

    fun deleteList(listId: String) {
        dataRepo.deleteList(ListRequestModel(listId, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
                .addTo(subscriptions)
    }

    fun findMembers(lists: List<ListModel>) {
        lists.forEach {
            list -> list.findMembers()
        }
    }

    private fun ListModel.findMembers() {
        dataRepo.getListMembers(ListRequestModel(uniqueId, Unit))
                .map { members -> uniqueId to members.map { member -> member.toUI() } }
                .observeOn(threads.UI)
                .subscribe(membersResponseListener)
                .addTo(subscriptions)
    }
}