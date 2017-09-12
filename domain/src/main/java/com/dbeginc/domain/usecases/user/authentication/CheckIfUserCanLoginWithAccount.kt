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

package com.dbeginc.domain.usecases.user.authentication

import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.UseCaseSingle
import io.reactivex.Single

/**
 * Created by darel on 03.09.17.
 *
 * Check If User Can Login with Account Provider
 * he selected during authentication process
 */
class CheckIfUserCanLoginWithAccount(private val userRepo: IUserRepo) : UseCaseSingle<Boolean, UserRequestModel<String>>() {
    override fun buildUseCaseObservable(params: UserRequestModel<String>): Single<Boolean> =
            userRepo.canUserLoginWithAccountProvider(params)

    override fun clean() = userRepo.clean()
}