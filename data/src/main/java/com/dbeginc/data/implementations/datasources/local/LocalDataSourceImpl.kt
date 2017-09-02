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

package com.dbeginc.data.implementations.datasources.local

import android.util.Log
import com.dbeginc.data.ConstantHolder
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.data.proxies.local.LocalItemImage
import com.dbeginc.data.proxies.local.LocalShoppingItem
import com.dbeginc.data.proxies.local.LocalShoppingList
import com.dbeginc.data.proxies.local.RealmString
import com.dbeginc.domain.entities.data.ItemImage
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.Realm
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
class LocalDataSourceImpl : DataSource {

    override fun addUserShopping(requestModel: ListRequestModel<String>): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use {
                realm -> realm.executeTransaction {
                db -> val list = db.where(LocalShoppingList::class.java)
                    .equalTo(ConstantHolder.UUID, requestModel.listId)
                    .findFirst()

                list.usersShopping.add(RealmString(requestModel.arg))

                db.insertOrUpdate(list)
            } }
        }
    }

    override fun removeUserShopping(requestModel: ListRequestModel<String>): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use {
                realm -> realm.executeTransaction {
                db -> val list = db.where(LocalShoppingList::class.java)
                    .equalTo(ConstantHolder.UUID, requestModel.listId)
                    .findFirst()

                list.usersShopping.remove(RealmString(requestModel.arg))

                db.insertOrUpdate(list)
            } }
        }
    }

    override fun getList(requestModel: ListRequestModel<Unit>): Flowable<ShoppingList> {

        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> val list = db.where(LocalShoppingList::class.java)
                .equalTo(ConstantHolder.UUID, requestModel.listId)
                .findFirst()
                .toDomainList()

            emitter.onNext(list)

        } } }, BackpressureStrategy.LATEST)
    }

    override fun getAllList(): Flowable<List<ShoppingList>> {
        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {

            db -> val lists = mutableListOf<ShoppingList>()
            db.where(LocalShoppingList::class.java)
                    .findAll()
                    .forEach { list -> lists.add(list.toDomainList()) }

            emitter.onNext(lists)

        } } }, BackpressureStrategy.LATEST)
    }

    override fun getItems(requestModel: ItemRequestModel<Unit>): Flowable<List<ShoppingItem>> {
        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {

            db -> val items = mutableListOf<ShoppingItem>()
            db.where(LocalShoppingItem::class.java)
                    .equalTo(ConstantHolder.ITEM_OF, requestModel.listId)
                    .findAll()
                    .forEach { item -> items.add(item.toDomainItem()) }

            emitter.onNext(items)

        } } }, BackpressureStrategy.LATEST)
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Flowable<ShoppingItem> {
        return Flowable.create({ emitter -> Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> val item = db.where(LocalShoppingItem::class.java)
                .equalTo(ConstantHolder.ITEM_OF, requestModel.listId)
                .equalTo(ConstantHolder.UUID, requestModel.arg)
                .findFirst()
                .toDomainItem()

            emitter.onNext(item)

        } } }, BackpressureStrategy.LATEST)
    }

    override fun addList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return requestModel.arg.isInDatabase()
                .flatMapCompletable { wasFound ->
                    if (wasFound) throw Exception("You already have this list! (Change Me Darel!!!)")
                    else Completable.fromAction { Realm.getDefaultInstance().use {
                        realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxyList()) }
                    } }
                }
    }

    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.fromAction{ Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxyItem()) }
        } }
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use {
                realm -> realm.executeTransaction {
                db -> val list = db.where(LocalShoppingList::class.java)
                    .equalTo(ConstantHolder.UUID, requestModel.listId)
                    .findFirst()
                list.name = requestModel.arg
                db.insertOrUpdate(list)
            } }
        }
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxyList(true)) }
        } }
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxyItem(true)) }
        } }
    }

    override fun uploadItemImage(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.insertOrUpdate(requestModel.arg.toProxyItem(true)) }
        } }
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        val removeList = Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> db.where(LocalShoppingList::class.java)
                .equalTo(ConstantHolder.UUID, requestModel.listId)
                .findAll()
                .deleteAllFromRealm()
        }} }

        val removeItems = Completable.fromAction{ Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> db.where(LocalShoppingItem::class.java)
                .equalTo(ConstantHolder.ITEM_OF, requestModel.listId)
                .findAll()
                .deleteAllFromRealm()

        } } }

        return removeList.andThen(removeItems)
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction {
            db -> db.where(LocalShoppingItem::class.java)
                .equalTo(ConstantHolder.ITEM_OF, requestModel.listId)
                .equalTo(ConstantHolder.UUID, requestModel.arg)
                .findAll()
                .deleteAllFromRealm()
        } } }
    }

    override fun deleteAll(requestModel: UserRequestModel<Unit>): Completable {
        return Completable.fromAction { Realm.getDefaultInstance().use {
            realm -> realm.executeTransaction { db -> db.deleteAll() }
        } }
    }

    override fun clean() { /* Not necessary here  */ }

    private fun ShoppingList.isInDatabase() : Single<Boolean> {

        return Single.create { emitter -> var isInDB = false

            Realm.getDefaultInstance().use { realm -> realm.executeTransaction {
                db -> isInDB = db.where(LocalShoppingList::class.java)
                    .equalTo(ConstantHolder.NAME, name)
                    .findFirst() != null
            }
                emitter.onSuccess(isInDB) }
        }
    }

    private fun ShoppingList.toProxyList(wasSaveRemotely: Boolean = false) : LocalShoppingList {
        var timestamp = 0L

        if (lastChange.isNotEmpty()) {
            try {
                timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        .parse(lastChange)
                        .time

            } catch (pe: ParseException) {
                Log.e(ConstantHolder.TAG, "Error in ${LocalShoppingList::class.java.simpleName}: ${pe.localizedMessage}")
            }
        }

        return LocalShoppingList(uuid= uuid, name = name, ownerName = ownerName,
                lastChange = timestamp, savedInServer = wasSaveRemotely
        )
    }

    private fun ShoppingItem.toProxyItem(wasSaveRemotely: Boolean = false) : LocalShoppingItem {

        return LocalShoppingItem(uuid = uuid, name = name, itemOf = itemOf, count = count,
                price = price, bought = bought, boughtBy = boughtBy, savedInServer = wasSaveRemotely, image = image.toProxyImage())
    }

    private fun ItemImage.toProxyImage() : LocalItemImage = LocalItemImage(uri = uri)
}