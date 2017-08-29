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

package com.dbeginc.dbshopping.di.user.module

import android.content.res.Resources
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.data.implementations.datasources.local.LocalDataSourceImpl
import com.dbeginc.data.implementations.datasources.remote.RemoteDataSourceImpl
import com.dbeginc.data.implementations.repositories.DataRepoImpl
import com.dbeginc.dbshopping.di.qualifiers.*
import com.dbeginc.dbshopping.di.scopes.UserScope
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.exception.implementation.StorageErrorHandlerImpl
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.viewmodels.UserModel
import com.dbeginc.domain.repositories.IDataRepo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler

/**
 * Created by darel on 22.08.17.
 *
 * Application User Module
 */
@Module class UserModule(val user: UserModel) {

    @Provides
    @UserScope
    internal fun provideUser() : UserModel = user

    @Provides
    @UserScope
    @StorageErrorManager
    internal fun provideStorageErrorManager(resource: Resources) : IErrorManager = StorageErrorHandlerImpl(resource)

    @Provides
    @RemoteListTable
    @UserScope
    internal fun provideShoppingListsTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference {
        val listTable = firebaseDatabase.reference.child(user.id).child(ConstantHolder.SHOPPING_LISTS)
        listTable.keepSynced(true)
        return listTable
    }

    @Provides
    @RemoteItemsTable
    @UserScope
    internal fun provideShoppingItemsTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference {
        val itemTable = firebaseDatabase.reference.child(user.id).child(ConstantHolder.SHOPPING_ITEMS)
        itemTable.keepSynced(true)
        return itemTable
    }

    @Provides
    @UserScope
    internal fun provideStorageReference(firebaseStorage: FirebaseStorage) : StorageReference =
            firebaseStorage.reference.child(user.id).child(ConstantHolder.SHOPPING_LISTS)

    @Provides
    @UserScope
    @LocalDataSource
    internal fun provideLocalDataSource() : DataSource = LocalDataSourceImpl()

    @Provides
    @UserScope
    @RemoteDataSource
    internal fun provideRemoteDataSource(@RemoteListTable remoteList: DatabaseReference, @RemoteItemsTable remoteItems: DatabaseReference, storageReference: StorageReference) : DataSource =
            RemoteDataSourceImpl(remoteListTable = remoteList, remoteItemsTable = remoteItems, remoteStorage = storageReference)

    @Provides
    @UserScope
    internal fun provideDataSource(@LocalDataSource localSource : DataSource, @RemoteDataSource remoteSource: DataSource,
                                   @UIThread uiThread: Scheduler, @IOThread ioThread: Scheduler,
                                   @ComputationThread workerThread: Scheduler) : IDataRepo {

        return DataRepoImpl(local = localSource, remote = remoteSource, uiThread = uiThread,
                workerThread = workerThread, ioThread = ioThread
        )
    }
}