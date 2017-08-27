package com.dbeginc.dbshopping.di.application.module

import com.dbeginc.data.datasource.UserSource
import com.dbeginc.data.implementations.datasources.local.LocalDataSourceImpl
import com.dbeginc.data.implementations.datasources.local.LocalUserSourceImpl
import com.dbeginc.data.implementations.datasources.remote.RemoteUserSourceImpl
import com.dbeginc.data.implementations.repositories.UserRepoImpl
import com.dbeginc.dbshopping.di.qualifiers.*
import com.dbeginc.dbshopping.di.scopes.ApplicationScope
import com.dbeginc.dbshopping.di.scopes.UserScope
import com.dbeginc.domain.repositories.IUserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler

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
@Module class DataSources {

    @Provides
    @LocalUserSource
    @ApplicationScope
    internal fun provideLocalUserSource() : UserSource = LocalUserSourceImpl()

    @Provides
    @RemoteUserSource
    @ApplicationScope
    internal fun provideRemoteUserSource(@UserTable userTable: DatabaseReference) : UserSource = RemoteUserSourceImpl(userTable)

    @Provides
    @AppUserRepo
    @ApplicationScope
    internal fun provideUserRepository(firebaseAuth: FirebaseAuth, @LocalUserSource local: UserSource, @RemoteUserSource remote: UserSource,
                                       @ComputationThread workerThread: Scheduler, @UIThread uiThread: Scheduler, @IOThread networkThread: Scheduler) : IUserRepo {

        return UserRepoImpl(firebaseAuth = firebaseAuth, local = local, remote = remote,
                workerThread = workerThread, uiThread = uiThread, ioThread = networkThread, localData = LocalDataSourceImpl())
    }
}