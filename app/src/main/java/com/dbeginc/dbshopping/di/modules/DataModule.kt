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

package com.dbeginc.dbshopping.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.dbeginc.common.utils.ApplicationScope
import com.dbeginc.data.CrashlyticsLogger
import com.dbeginc.data.RxThreadProvider
import com.dbeginc.data.implementations.repositories.DataRepoImpl
import com.dbeginc.data.implementations.repositories.UserRepoImpl
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.SHARED_PREFS
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Module
import dagger.Provides
import dagger.android.DaggerApplication

/**
 * Created by darel on 22.02.18.
 *
 * Data Module
 */
@Module
class DataModule {
    @Provides
    @ApplicationScope
    fun provideUserRepository(appContext: Context) : IUserRepo = UserRepoImpl.create(appContext)

    @Provides
    @ApplicationScope
    fun provideDataRepository(appContext: Context) : IDataRepo = DataRepoImpl.create(appContext)

    @Provides
    @ApplicationScope
    fun provideResources(application: DaggerApplication) : Resources = application.resources

    @Provides
    @ApplicationScope
    fun provideSharedPreferences(appContext: Context) : SharedPreferences = appContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    @Provides
    @ApplicationScope
    fun provideThreadProvider() : ThreadProvider = RxThreadProvider

    @Provides
    @ApplicationScope
    fun provideLogger() : Logger = CrashlyticsLogger
}