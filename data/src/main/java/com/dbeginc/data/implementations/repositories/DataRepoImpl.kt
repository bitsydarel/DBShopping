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

package com.dbeginc.data.implementations.repositories

import android.content.Context
import com.dbeginc.data.ConstantHolder.DELETE_REQUEST
import com.dbeginc.data.ConstantHolder.INSERT_REQUEST
import com.dbeginc.data.ConstantHolder.LOCAL_ITEMS_COMMENTS
import com.dbeginc.data.ConstantHolder.REMOTE_IMAGES_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_ITEMS_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_LISTS_TABLE
import com.dbeginc.data.ConstantHolder.REMOTE_USERS_TABLE
import com.dbeginc.data.ConstantHolder.UPDATE_IMAGE_REQUEST
import com.dbeginc.data.ConstantHolder.UPDATE_REQUEST
import com.dbeginc.data.CrashlyticsLogger
import com.dbeginc.data.RxThreadProvider
import com.dbeginc.data.implementations.datasources.local.LocalDataSource
import com.dbeginc.data.implementations.datasources.local.list.LocalDataSourceImpl
import com.dbeginc.data.implementations.datasources.local.list.room.LocalDataDatabase
import com.dbeginc.data.implementations.datasources.remote.RemoteListDataSource
import com.dbeginc.data.implementations.datasources.remote.RemoteListDataSourceImpl
import com.dbeginc.data.proxies.local.list.LocalPendingDataRequest
import com.dbeginc.data.proxies.local.list.LocalPendingListMemberRequest
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.data.ItemComment
import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.request.AddListRequest
import com.dbeginc.domain.entities.request.ItemRequestModel
import com.dbeginc.domain.entities.request.ListRequestModel
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber

class DataRepoImpl private constructor(private val local: LocalDataSource, private val remote: RemoteListDataSource, private val threads: ThreadProvider, private val logger: Logger) : IDataRepo {
    private val subscriptions = CompositeDisposable()

    companion object {
        @JvmStatic
        fun create(appContext: Context) : IDataRepo {
            var firstTime = false

            val app : FirebaseApp = FirebaseApp.getInstance() ?: FirebaseApp.initializeApp(appContext)!!.also {
                firstTime = true
            }

            val firebaseDatabase = FirebaseDatabase.getInstance(app)

            if (firstTime) firebaseDatabase.setPersistenceEnabled(true)

            val firebaseStorage = FirebaseStorage.getInstance(app)

            firebaseStorage.maxUploadRetryTimeMillis = 300000

            val listTable = firebaseDatabase.reference.child(REMOTE_LISTS_TABLE)
            listTable.keepSynced(true)

            val userTable = firebaseDatabase.reference.child(REMOTE_USERS_TABLE)
            userTable.keepSynced(true)

            val itemsTable = firebaseDatabase.reference.child(REMOTE_ITEMS_TABLE)
            itemsTable.keepSynced(true)

            val itemsCommentTable = firebaseDatabase.reference.child(LOCAL_ITEMS_COMMENTS)
            itemsCommentTable.keepSynced(true)

            val storageRef = firebaseStorage.reference.child(REMOTE_IMAGES_TABLE)

            return DataRepoImpl(
                    LocalDataSourceImpl(LocalDataDatabase.create(appContext)),
                    RemoteListDataSourceImpl(
                            listTable = listTable,
                            itemsTable = itemsTable,
                            itemsCommentsTable = itemsCommentTable,
                            userTable = userTable,
                            remoteStorage = storageRef
                    ),
                    RxThreadProvider,
                    CrashlyticsLogger
            )
        }
    }

    /***************************************** LIST RELATED STUFF *****************************************/
    override fun getList(requestModel: ListRequestModel<Unit>): Flowable<ShoppingList> {
        return local.getList(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getList(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribe(UpdateListObserver())
                }
    }

    override fun getAllListsFromUser(requestModel: UserRequestModel<Unit>): Flowable<List<ShoppingList>> {
        return local.getAllListsFromUser(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getAllListsFromUser(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribeWith(UpdateAllListObserver())
                }
    }

    override fun addList(requestModel: AddListRequest): Completable {
        val request = LocalPendingDataRequest(requestModel.list.uniqueId, null)

        return local.addList(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.addList(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun changeListName(requestModel: ListRequestModel<String>): Completable {
        val request = LocalPendingDataRequest(requestModel.listId, null, UPDATE_REQUEST)

        return local.changeListName(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.changeListName(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun updateList(requestModel: ListRequestModel<ShoppingList>): Completable {
        val request = LocalPendingDataRequest(requestModel.listId, null, UPDATE_REQUEST)

        return local.updateList(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.updateList(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun getListMembers(requestModel: ListRequestModel<Unit>): Flowable<List<ShoppingUser>> {
        return local.getListMembers(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getListMembers(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribe(UpdateListMemberObserver(requestModel.listId))
                }
    }

    override fun updateListMember(requestModel: ListRequestModel<ShoppingUser>): Completable {
        val request = LocalPendingListMemberRequest(requestModel.arg.uniqueId, requestModel.listId)

        return local.updateListMember(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingListMemberRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.updateListMember(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingListMemberRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun deleteList(requestModel: ListRequestModel<Unit>): Completable {
        val request = LocalPendingDataRequest(requestModel.listId, null, DELETE_REQUEST)

        return local.deleteList(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.deleteList(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    /***************************************** ITEM RELATED STUFF *****************************************/
    override fun getItems(requestModel: ListRequestModel<Unit>): Flowable<List<ShoppingItem>> {
        return local.getItems(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getItems(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribe(UpdateAllItemsObserver())
                }
    }

    override fun getItem(requestModel: ItemRequestModel<String>): Flowable<ShoppingItem> {
        return local.getItem(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getItem(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribe(UpdateItemObserver())
                }
    }

    override fun addItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val request = LocalPendingDataRequest(requestModel.arg.uniqueId, requestModel.listId)

        return local.addItem(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.addItem(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun updateItem(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val request = LocalPendingDataRequest(requestModel.arg.uniqueId, requestModel.listId, UPDATE_REQUEST)

        return local.updateItem(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.updateItem(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun updateItemImage(requestModel: ItemRequestModel<ShoppingItem>): Completable {
        val request = LocalPendingDataRequest(requestModel.arg.uniqueId, requestModel.listId, UPDATE_REQUEST)

        return local.updateItem(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.updateItemImage(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun deleteItem(requestModel: ItemRequestModel<String>): Completable {
        val request = LocalPendingDataRequest(requestModel.arg, requestModel.listId, DELETE_REQUEST)

        return local.deleteItem(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    local.addPendingDataRequest(request)
                            .subscribeOn(threads.CP)
                            .subscribe(UpdateObserver())
                }
                .doOnComplete {
                    remote.deleteItem(requestModel)
                            .subscribeOn(threads.IO)
                            .andThen(local.deletePendingDataRequest(request).subscribeOn(threads.CP))
                            .subscribe(UpdateObserver())
                }
    }

    override fun getAllItemComments(requestModel: ItemRequestModel<String>): Flowable<List<ItemComment>> {
        return local.getAllItemComments(requestModel)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remote.getAllItemComments(requestModel)
                            .subscribeOn(threads.IO)
                            .subscribe(UpdateItemComments())
                }
    }

    override fun postItemComment(requestModel: ItemRequestModel<ItemComment>): Completable =
            remote.postComment(requestModel).subscribeOn(threads.IO)

    override fun deleteUserData(requestModel: UserRequestModel<Unit>): Completable {
        return remote.deleteAllUserData(requestModel).subscribeOn(threads.IO)
                .andThen(deleteAll())
    }

    override fun deleteAll(): Completable = local.deleteAll().subscribeOn(threads.CP)

    /***************************************** Pending Request Handling  **************************************************/
    override fun clean() = subscriptions.clear()

    override fun publishPendingChanges(user: ShoppingUser): Completable {
        val publishListMemberRequests = local
                .getAllListMemberRequest()
                .flatMapPublisher { pendingRequests -> Flowable.fromIterable(pendingRequests) }
                .flatMapCompletable { pendingRequest -> publishPendingListMemberChanges(pendingRequest) }

        val publishDataRequests = local.getAllPendingDataRequest()
                .flatMapPublisher { pendingRequests -> Flowable.fromIterable(pendingRequests) }
                .flatMapCompletable { pendingRequest ->
                    val publishStream = if (pendingRequest.parentId == null) publishPendingListChanges(pendingRequest, user) else publishPendingItemChanges(pendingRequest)
                    publishStream.andThen(local.deletePendingDataRequest(pendingRequest))
                }

        return publishListMemberRequests.andThen(publishDataRequests)
    }

    private fun publishPendingListChanges(request: LocalPendingDataRequest, user: ShoppingUser) : Completable {
        return local.getList(ListRequestModel(request.itemUniqueId, Unit))
                .subscribeOn(threads.CP)
                .flatMapCompletable { list ->
                    val addListRequest = AddListRequest(list, user)
                    val listRequest = ListRequestModel(list.uniqueId, list)

                    when(request.requestType) {
                        INSERT_REQUEST -> remote.addList(addListRequest).subscribeOn(threads.IO)
                        UPDATE_REQUEST -> remote.updateList(listRequest).subscribeOn(threads.IO)
                        DELETE_REQUEST -> remote.deleteList(ListRequestModel(list.uniqueId, Unit)).subscribeOn(threads.IO)
                        else -> remote.updateList(listRequest).subscribeOn(threads.IO)
                    }
                }
    }

    private fun publishPendingItemChanges(request: LocalPendingDataRequest) : Completable {
        return local.getItem(ItemRequestModel(request.parentId!!, request.itemUniqueId))
                .subscribeOn(threads.CP)
                .flatMapCompletable { item ->
                    val itemRequest = ItemRequestModel(item.itemOf, item)

                    when(request.requestType) {
                        INSERT_REQUEST -> remote.addItem(itemRequest).subscribeOn(threads.IO)
                        UPDATE_REQUEST -> remote.updateItem(itemRequest).subscribeOn(threads.IO)
                        UPDATE_IMAGE_REQUEST -> remote.updateItemImage(itemRequest).subscribeOn(threads.IO)
                        DELETE_REQUEST -> remote.deleteItem(ItemRequestModel(item.itemOf, item.uniqueId)).subscribeOn(threads.IO)
                        else -> remote.updateItem(itemRequest).subscribeOn(threads.IO)
                    }
                }
    }

    private fun publishPendingListMemberChanges(request: LocalPendingListMemberRequest) : Completable {
        return local.getListMember(ListRequestModel(request.listId, request.userId))
                .subscribeOn(threads.CP)
                .flatMapCompletable { member -> remote.updateListMember(ListRequestModel(request.listId, member)).subscribeOn(threads.IO) }
    }

    /************************* HELPER STUFF ****************************/
    private fun CompositeDisposable.addList(list: ShoppingList) {
        val requestModel = ListRequestModel(listId = list.uniqueId, arg = list)
        add(local.updateList(requestModel)
                .subscribeOn(threads.CP)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addListMembers(listId: String, members: List<ShoppingUser>) {
        add(local.addListMembers(ListRequestModel(listId, members))
                .subscribeOn(threads.CP)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addAllList(lists: List<ShoppingList>) {
        add(local.addAllList(lists).subscribeOn(threads.CP).subscribeWith(UpdateObserver()))
    }

    private fun CompositeDisposable.updateOrAddItem(item: ShoppingItem) {
        add(local.updateOrAddItem(item).subscribeOn(threads.CP).subscribeWith(UpdateObserver()))
    }

    private fun CompositeDisposable.addItem(listId: String, item: ShoppingItem) {
        val requestModel = ItemRequestModel(listId = listId, arg = item)
        add(local.updateItem(requestModel).subscribeOn(threads.CP).subscribeWith(UpdateObserver()))
    }

    private fun CompositeDisposable.deleteItem(listId: String, item: ShoppingItem) {
        val requestModel: ItemRequestModel<String> = ItemRequestModel(listId, item.uniqueId)
        add(local.deleteItem(requestModel).subscribeOn(threads.CP).subscribeWith(UpdateObserver()))
    }

    private fun CompositeDisposable.addItemComment(comment: ItemComment) {
        add(local.addItemComment(comment).subscribeOn(threads.CP).subscribeWith(UpdateObserver()))
    }

    private inner class UpdateAllListObserver : DisposableSingleObserver<List<ShoppingList>>() {
        override fun onSuccess(lists: List<ShoppingList>) = subscriptions.addAllList(lists)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateListObserver : DisposableSingleObserver<ShoppingList>() {
        override fun onSuccess(list: ShoppingList) = subscriptions.addList(list)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateListMemberObserver(private val listId: String) : DisposableSingleObserver<List<ShoppingUser>>() {
        override fun onSuccess(members: List<ShoppingUser>) = subscriptions.addListMembers(listId, members)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateItemComments : DisposableSubscriber<ItemComment>() {
        override fun onComplete() =
                logger.logEvent("Update all data done in ${DataRepoImpl::class.java.simpleName}")

        override fun onNext(comment: ItemComment) = subscriptions.addItemComment(comment)

        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateAllItemsObserver : DisposableSubscriber<Pair<ShoppingItem,Boolean>>() {
        override fun onComplete() = logger.logEvent("Update all data done in ${DataRepoImpl::class.java.simpleName}")
        override fun onNext(item: Pair<ShoppingItem,Boolean>) =
                if (item.second) subscriptions.deleteItem(item.first.itemOf, item.first) else subscriptions.updateOrAddItem(item.first)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateItemObserver : DisposableSingleObserver<ShoppingItem>() {
        override fun onSuccess(item: ShoppingItem) = subscriptions.addItem(item.itemOf, item)
        override fun onError(error: Throwable) = logger.logError(error)
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() = logger.logEvent("Update of data done in ${DataRepoImpl::class.java.simpleName}")
        override fun onError(error: Throwable) = logger.logError(error)
    }
}