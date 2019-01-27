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

package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 21.08.17.
 *
 * Get Items
 */
class GetItems(private val dataRepo: IDataRepo) : UseCase<List<ShoppingItem>, ItemRequestModel<Unit>>() {

    override fun buildUseCaseObservable(params: ItemRequestModel<Unit>): Flowable<List<ShoppingItem>> =
            dataRepo.getItems(params)

    override fun clean() = dataRepo.clean()
}
