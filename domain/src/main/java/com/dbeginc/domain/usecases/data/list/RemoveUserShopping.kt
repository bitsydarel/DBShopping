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

package com.dbeginc.domain.usecases.data.list

import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 29.08.17.
 *
 * Remove an user that is not shopping a list anymore
 */
class RemoveUserShopping(private val dataRepo: IDataRepo) : UseCaseCompletable<ListRequestModel<String>>() {
    override fun buildUseCaseCompletableObservable(params: ListRequestModel<String>): Completable =
            dataRepo.removeUserShopping(params)

    override fun clean() = dataRepo.clean()
}