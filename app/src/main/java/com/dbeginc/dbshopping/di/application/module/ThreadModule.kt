package com.dbeginc.dbshopping.di.application.module

import com.dbeginc.dbshopping.di.qualifiers.ComputationThread
import com.dbeginc.dbshopping.di.qualifiers.IOThread
import com.dbeginc.dbshopping.di.qualifiers.UIThread
import com.dbeginc.dbshopping.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
@Module class ThreadModule {

    @Provides
    @IOThread
    @ApplicationScope
    internal fun provideIoScheduler() = Schedulers.io()

    @Provides
    @UIThread
    @ApplicationScope
    internal fun provideUiScheduler() = AndroidSchedulers.mainThread()

    @Provides
    @ComputationThread
    @ApplicationScope
    internal fun provideComputationScheduler() = Schedulers.computation()

}