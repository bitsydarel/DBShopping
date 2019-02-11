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

package com.dbeginc.dbshopping.di.components

import com.dbeginc.common.utils.ApplicationScope
import com.dbeginc.dbshopping.di.modules.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

/**
 * Created by darel on 21.08.17.
 *
 * Application component
 */
@ApplicationScope
@Component(modules = [
    ApplicationModule::class, DataModule::class, ViewModelModule::class,
    FragmentsModule::class, ActivitiesModule::class, ServicesModule::class,
    AndroidInjectionModule::class, AndroidSupportInjectionModule::class
])
interface ApplicationComponent : AndroidInjector<DaggerApplication> {
    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<DaggerApplication>()
}