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

package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 20.08.17.
 *
 * Add item to a shopping list
 */
class AddItem(private val dataRepo: IDataRepo) : UseCaseCompletable<ItemRequestModel<ShoppingItem>>() {

    override fun buildUseCaseCompletableObservable(params: ItemRequestModel<ShoppingItem>): Completable =
            dataRepo.addItem(params)

    override fun clean() = dataRepo.clean()
}