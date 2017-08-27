package com.dbeginc.dbshopping.di.application.component

import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.di.application.module.AppModule
import com.dbeginc.dbshopping.di.application.module.DataSources
import com.dbeginc.dbshopping.di.application.module.PresentationModule
import com.dbeginc.dbshopping.di.application.module.ThreadModule
import com.dbeginc.dbshopping.di.authentication.component.AuthenticationComponent
import com.dbeginc.dbshopping.di.authentication.module.AuthenticationModule
import com.dbeginc.dbshopping.di.scopes.ApplicationScope
import com.dbeginc.dbshopping.di.user.component.UserComponent
import com.dbeginc.dbshopping.di.user.module.UserModule
import com.dbeginc.dbshopping.splash.view.SplashActivity
import dagger.Component

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
@ApplicationScope
@Component(modules = arrayOf(AppModule::class, PresentationModule::class, DataSources::class, ThreadModule::class))
interface ApplicationComponent {

    fun inject(splashActivity: SplashActivity)
    fun inject(loadingDialog: LoadingDialog)
    fun inject(baseFragment: BaseFragment)
    fun inject(baseActivity: BaseActivity)

    /**
     * Authentication Component
     */
    fun plus(authModule: AuthenticationModule) : AuthenticationComponent

    /**
     * User Component
     */
    fun plus(userModule: UserModule) : UserComponent
}