/*
 *
 *  * Copyright (C) 2019 Darel Bitsy
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

import com.dbeginc.domain.entities.requestmodel.GoogleRequestModel
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 22.08.17.
 *
 * Create new user with google
 */
class CreateNewUserWithGoogle(private val userRepo: IUserRepo) : UseCaseCompletable<GoogleRequestModel<String>>() {

    override fun buildUseCaseCompletableObservable(params: GoogleRequestModel<String>): Completable =
            userRepo.createNewUserWithGoogle(params)

    override fun clean() = userRepo.clean()
}
