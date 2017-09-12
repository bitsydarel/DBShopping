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
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

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
class LoginWithGoogle(private val userRepo: IUserRepo) : UseCaseCompletable<UserRequestModel<String>>() {

    override fun buildUseCaseCompletableObservable(params: UserRequestModel<String>): Completable =
            userRepo.loginWithGoogle(params)

    override fun clean() = userRepo.clean()
}