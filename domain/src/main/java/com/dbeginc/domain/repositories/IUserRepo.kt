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

package com.dbeginc.domain.repositories

import com.dbeginc.domain.entities.requestmodel.AccountRequestModel
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.GoogleRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.Account
import com.dbeginc.domain.entities.user.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface IUserRepo : Cleanable {

    /**
     * Create New User
     * @param requestModel [AuthRequestModel] to be added
     * @return [Completable] that notify about the task completion
     */
    fun createNewUser(requestModel: AuthRequestModel<String>) : Completable

    fun createNewUserWithGoogle(requestModel: GoogleRequestModel<String>) : Completable

    fun loginUser(requestModel: AuthRequestModel<Unit>) : Completable

    fun loginWithGoogle(requestModel: UserRequestModel<String>) : Completable

    fun doesUserExist(requestModel: UserRequestModel<Unit>) : Single<Boolean>

    fun canUserLoginWithAccountProvider(requestModel: UserRequestModel<String>) : Single<Boolean>

    fun getUser(requestModel: UserRequestModel<Unit>) : Flowable<User>

    fun getUsers(requestModel: UserRequestModel<List<String>>) : Flowable<List<User>>

    fun getAccount(requestModel: AccountRequestModel<Unit>) : Flowable<Account>

    fun updateAccount(requestModel: AccountRequestModel<Account>) : Completable

    fun updateUser(requestModel: UserRequestModel<User>) : Completable

    fun deleteUser(requestModel: UserRequestModel<Unit>) : Completable

    fun logoutUser(requestModel: UserRequestModel<Unit>): Completable
}