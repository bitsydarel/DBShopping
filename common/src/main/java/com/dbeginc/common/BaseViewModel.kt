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

package com.dbeginc.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by darel on 25.02.18.
 *
 * Base View Model from my MVPV (Model ViewModel Presenter View) Architecture
 */
abstract class BaseViewModel : ViewModel() {
    private val _lastRequest = BehaviorSubject.createDefault(RequestType.GET)
    protected abstract val subscriptions: CompositeDisposable
    protected abstract val requestState: MutableLiveData<RequestState>

    /**
     * Request state event
     * This method help me
     * to subscribe to the status
     * of any request made by viewModel
     * And also avoid memory leak :))
     */
    fun getRequestStateEvent() : LiveData<RequestState> = requestState

    fun getLastRequest() : RequestType = _lastRequest.value

    protected fun getModifiableLastRequest() : BehaviorSubject<RequestType> = _lastRequest

    fun resetState() = subscriptions.clear()

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}