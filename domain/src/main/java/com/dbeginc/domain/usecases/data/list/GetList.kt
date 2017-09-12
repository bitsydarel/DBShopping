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

import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 20.08.17.
 *
 * Get List
 */
class GetList(private val dataRepo: IDataRepo) : UseCase<ShoppingList, ListRequestModel<Unit>>() {

    override fun buildUseCaseObservable(params: ListRequestModel<Unit>): Flowable<ShoppingList> =
            dataRepo.getList(params)

    override fun clean() = dataRepo.clean()
}