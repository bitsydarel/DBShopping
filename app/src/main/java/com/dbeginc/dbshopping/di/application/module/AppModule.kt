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

package com.dbeginc.dbshopping.di.application.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.dbeginc.dbshopping.BuildConfig
import com.dbeginc.dbshopping.di.qualifiers.AccountTable
import com.dbeginc.dbshopping.di.qualifiers.UserTable
import com.dbeginc.dbshopping.di.scopes.ApplicationScope
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

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
@Module class AppModule(val application: Application) {

    @Provides
    @ApplicationScope
    internal fun provideContext() : Context = application.applicationContext

    @Provides
    @ApplicationScope
    internal fun provideFirebaseApp(context: Context) : FirebaseApp = FirebaseApp.initializeApp(context) as FirebaseApp

    @Provides
    @ApplicationScope
    internal fun provideResources() : Resources = application.resources

    @Provides
    @ApplicationScope
    internal fun provideSharedPreferences() : SharedPreferences =
            application.getSharedPreferences(ConstantHolder.DB_NAME, Context.MODE_PRIVATE)

    @Provides
    @ApplicationScope
    internal fun provideFirebaseDatabase(firebaseApp: FirebaseApp): FirebaseDatabase {
        val firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp)

        firebaseDatabase.setPersistenceEnabled(true)
        if (BuildConfig.DEBUG) firebaseDatabase.setLogLevel(Logger.Level.DEBUG)

        return firebaseDatabase
    }

    @Provides
    @ApplicationScope
    internal fun provideFirebaseStorage(firebaseApp: FirebaseApp) : FirebaseStorage {
        val firebaseStorage = FirebaseStorage.getInstance(firebaseApp)

        if (BuildConfig.DEBUG) firebaseStorage.maxUploadRetryTimeMillis = 2000
        else firebaseStorage.maxUploadRetryTimeMillis = 300000

        return firebaseStorage
    }

    @Provides
    @ApplicationScope
    internal fun provideFirebaseAuth(firebaseApp: FirebaseApp) : FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    @Provides
    @UserTable
    @ApplicationScope
    internal fun provideShoppingItemsTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference =
            firebaseDatabase.reference.child(ConstantHolder.USERS)

    @Provides
    @AccountTable
    @ApplicationScope
    internal fun provideUserAccountTable(firebaseDatabase: FirebaseDatabase) : DatabaseReference =
            firebaseDatabase.reference.child(ConstantHolder.ACCOUNTS)
}