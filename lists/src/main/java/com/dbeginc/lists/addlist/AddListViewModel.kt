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

package com.dbeginc.lists.addlist

import android.arch.lifecycle.MutableLiveData
import android.database.sqlite.SQLiteConstraintException
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.AddListRequest
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.lists.viewmodels.ListModel
import com.dbeginc.lists.viewmodels.ShoppingUserModel
import com.dbeginc.lists.viewmodels.toDomain
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 27.02.18.
 *
 * Add list viewModel
 */
class AddListViewModel @Inject constructor(private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    val presenter = AddListPresenter()

    fun addList(list: ListModel, currentUser: ShoppingUserModel) {
        dataRepo.addList(AddListRequest(list = list.toDomain(), member = currentUser.toDomain()))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doOnError {
                    if (it is SQLiteConstraintException) getModifiableLastRequest().onNext(RequestType.ADD)
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

}