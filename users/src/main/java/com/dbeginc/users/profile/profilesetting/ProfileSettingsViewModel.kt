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

package com.dbeginc.users.profile.profilesetting

import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.FacebookRequestModel
import com.dbeginc.domain.entities.request.GoogleRequestModel
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.users.viewmodels.UserProfileModel
import com.dbeginc.users.viewmodels.toDomain
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 17.03.18.
 *
 * Profile Settings ViewModel
 */
class ProfileSettingsViewModel @Inject constructor(private val userRepo: IUserRepo, private val dataRepo: IDataRepo,
                                                   private val threads: ThreadProvider, private val logger: Logger)  : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()

    fun linkAccountWithGoogle(email: String, token: String) {
        userRepo.linkAccountWithGoogle(GoogleRequestModel(email, token))
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

    fun linkAccountWithFacebook(email: String, token: String) {
        userRepo.linkAccountWithFacebook(FacebookRequestModel(email, token))
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

    fun updateUser(currentUser: UserProfileModel) {
        userRepo.updateUserInfo(UserRequestModel(currentUser.uniqueId, currentUser.toDomain()))
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

    fun deleteAccount(userId: String){
        val requestModel = UserRequestModel(userId, Unit)

        dataRepo.deleteUserData(requestModel)
                .andThen(userRepo.logoutUser(requestModel))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        {
                            getModifiableLastRequest().onNext(RequestType.DELETE)
                            requestState.postValue(RequestState.COMPLETED)
                        },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
                .addTo(subscriptions)
    }

}