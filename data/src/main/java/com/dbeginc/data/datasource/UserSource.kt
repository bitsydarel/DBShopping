package com.dbeginc.data.datasource

import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 21.08.17.
 */
interface UserSource {

    fun checkIfUserExist(requestModel: UserRequestModel<Unit>) : Single<Boolean>

    fun createUser(requestModel: UserRequestModel<User>) : Completable

    fun getUser(requestModel: UserRequestModel<Unit>) : Flowable<User>

    fun updateUser(requestModel: UserRequestModel<User>) : Completable

    fun deleteUser(requestModel: UserRequestModel<Unit>) : Completable
}