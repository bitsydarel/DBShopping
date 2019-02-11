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

package com.dbeginc.data.implementations.datasources.local.list

import android.support.annotation.RestrictTo
import com.dbeginc.data.implementations.datasources.local.LocalDataSource
import com.dbeginc.data.implementations.datasources.local.list.room.LocalDataDatabase
import com.dbeginc.data.proxies.local.list.LocalPendingDataRequest
import com.dbeginc.data.proxies.local.list.LocalPendingListMemberRequest
import com.dbeginc.data.proxies.local.mapper.toDomain
import com.dbeginc.data.proxies.local.mapper.toLocalProxy
import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.request.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by darel on 21.08.17.
 *
 * Local Implementation of a data source
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class LocalDataSourceImpl(private val db : LocalDataDatabase) : LocalDataSource {

    /***************************************** LIST RELATED STUFF *****************************************/
    override fun getList(requestModel: ListRequestModel<Unit>): Flowable<ShoppingList> {
        return db.listDao().getShoppingList(requestModel.listId)
                .map { localList -> localList.toDomain() }
    }

    override fun getAllListsFromUser(requestModel: UserRequestModel<Unit>): Flowable<List<ShoppingList>> {
        return db.listDao().getShoppingListsOfUser()
                .map { localLists -> localLists.map { list -> list.toDomain() } }
    }

    override fun addList(requestModel: AddListRequest): Completable {
        val addMemberToList = Completable.fromCallable {
            db.listDao().addListMember(requestModel.member.toLocalProxy(requestModel.list.uniqueId))
        }

        return Completable.fromCallable {
            db.listDao().addShoppingList(requestModel.list.toLocalProxy())
        }.andThen(addMemberToList)
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        return Completable.fromCallable {
            db.listDao().changeShoppingListName(requestModel.listId, requestModel.arg)
        }
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        return Completable.fromCallable {
            db.listDao().updateShoppingList(requestModel.arg.toLocalProxy())
        }
    }

    override fun addAllList(lists: List<ShoppingList>): Completable {
        return Completable.fromCallable {
            db.listDao().addShoppingLists(lists.map { list -> list.toLocalProxy() })
        }
    }

    override fun getListMember(requestModel: ListRequestModel<String>): Flowable<ShoppingUser> {
        return db.listDao().getListMember(requestModel.listId, requestModel.arg)
                .map { it.toDomain() }
    }

    override fun getListMembers(requestModel: ListRequestModel<Unit>): Flowable<List<ShoppingUser>> {
        return db.listDao().getListMembers(requestModel.listId)
                .map { users -> users.map { it.toDomain() } }
    }

    override fun updateListMember(requestModel: ListRequestModel<ShoppingUser>): Completable {
        return Completable.fromCallable {
            db.listDao().addListMember(requestModel.arg.toLocalProxy(requestModel.listId))
        }
    }

    override fun addListMembers(requestModel: ListRequestModel<List<ShoppingUser>>): Completable {
        return Completable.fromCallable {
            db.listDao().addListMembers(requestModel.arg.map { it.toLocalProxy(requestModel.listId) })
        }
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        return Completable.fromCallable {
            db.listDao().deleteListWithChild(requestModel.listId)
        }
    }

    /***************************************** ITEM RELATED STUFF *****************************************/

    override fun getItems(requestModel: ListRequestModel<Unit>): Flowable<List<ShoppingItem>> {
        return db.listDao().getShoppingItemsFromList(requestModel.listId)
                .map { items -> items.map { item -> item.toDomain() } }
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Flowable<ShoppingItem> {
        return db.listDao().getShoppingItem(requestModel.listId, requestModel.arg)
                .map { localItem -> localItem.toDomain() }
    }

    override fun updateOrAddItem(item: ShoppingItem): Completable {
        return Completable.fromCallable {
            db.listDao().updateOrAddShoppingItem(item.toLocalProxy())
        }
    }

    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.fromCallable {
            db.listDao().addShoppingItem(requestModel.arg.toLocalProxy())
        }
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        return Completable.fromCallable {
            db.listDao().updateShoppingItem(requestModel.arg.toLocalProxy())
        }
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        return Completable.fromCallable {
            db.listDao().deleteItem(listId = requestModel.listId, itemId = requestModel.arg)
        }
    }

    override fun getAllItemComments(requestModel: ItemRequestModel<String>): Flowable<List<ItemComment>> =
            db.listDao().getAllItemComments(requestModel.arg)
                    .map { it.map { it.toDomain() } }

    override fun addItemComment(comment: ItemComment): Completable {
        return Completable.fromCallable {
            db.listDao().addItemComment(comment.toLocalProxy())
        }
    }

    override fun deleteAll(): Completable {
        return Completable.fromCallable {
            db.listDao().deleteAll()
        }
    }

    /************************ PENDING REQUEST STUFF ************************************************/
    override fun addPendingListMemberRequest(request: LocalPendingListMemberRequest): Completable {
        return Completable.fromCallable {
            db.listDao().addPendingListMemberRequest(request)
        }
    }

    override fun getAllListMemberRequest(): Maybe<List<LocalPendingListMemberRequest>> =
            db.listDao().getAllPendingListMemberRequest()

    override fun deletePendingListMemberRequest(request: LocalPendingListMemberRequest): Completable {
        return Completable.fromCallable {
            db.listDao().deletePendingListMemberRequest(request)
        }
    }

    override fun addPendingDataRequest(request: LocalPendingDataRequest): Completable =
            Completable.fromCallable { db.listDao().addPendingDataRequest(request) }

    override fun getAllPendingDataRequest(): Maybe<List<LocalPendingDataRequest>> =
            db.listDao().getAllPendingDataRequest()

    override fun deletePendingDataRequest(request: LocalPendingDataRequest): Completable =
            Completable.fromCallable { db.listDao().deletePendingDataRequest(request) }
}