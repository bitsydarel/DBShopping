package com.dbeginc.dbshopping.di.user.module

import android.content.res.Resources
import com.dbeginc.data.datasource.DataSource
import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.implementations.datasources.local.LocalDataSourceImpl
import com.dbeginc.data.implementations.datasources.remote.RemoteDataSourceImpl
import com.dbeginc.data.implementations.repositories.DataRepoImpl
import com.dbeginc.data.implementations.repositories.UserRepoImpl
import com.dbeginc.dbshopping.di.qualifiers.*
import com.dbeginc.dbshopping.di.scopes.UserScope
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.exception.implementation.StorageErrorHandlerImpl
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler

/**
 * Created by darel on 22.08.17.
 */
@Module class UserModule(val user: User) {

    @Provides
    @UserScope
    internal fun provideUser() : User = user

    @Provides
    @UserScope
    @StorageErrorManager
    internal fun provideStorageErrorManager(resource: Resources) : IErrorManager = StorageErrorHandlerImpl(resource)

    @Provides
    @RemoteListTable
    @UserScope
    internal fun provideShoppingListsTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference {
        val listTable = firebaseDatabase.reference.child(user.uuid).child(ConstantHolder.SHOPPING_LISTS)
        listTable.keepSynced(true)
        return listTable
    }

    @Provides
    @RemoteItemsTable
    @UserScope
    internal fun provideShoppingItemsTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference {
        val itemTable = firebaseDatabase.reference.child(user.uuid).child(ConstantHolder.SHOPPING_ITEMS)
        itemTable.keepSynced(true)
        return itemTable
    }

    @Provides
    @UserScope
    internal fun provideStorageReference(firebaseStorage: FirebaseStorage) : StorageReference =
            firebaseStorage.reference.child(user.uuid).child(ConstantHolder.SHOPPING_LISTS)

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