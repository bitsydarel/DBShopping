package com.dbeginc.dbshopping.helper

import com.dbeginc.dbshopping.addlist.view.AddListActivity
import com.dbeginc.dbshopping.authentication.login.view.LoginActivity
import com.dbeginc.dbshopping.authentication.signup.view.SignUpActivity
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.di.application.component.ApplicationComponent
import com.dbeginc.dbshopping.di.authentication.component.AuthenticationComponent
import com.dbeginc.dbshopping.di.authentication.module.AuthenticationModule
import com.dbeginc.dbshopping.di.user.component.UserComponent
import com.dbeginc.dbshopping.home.view.HomeActivity
import com.dbeginc.dbshopping.itemdetail.view.ItemDetailActivity
import com.dbeginc.dbshopping.listdetail.view.EditListNameDialog
import com.dbeginc.dbshopping.listdetail.view.ListDetailActivity
import com.dbeginc.dbshopping.listitems.view.ListItemsFragment
import com.dbeginc.dbshopping.splash.view.SplashActivity
import com.dbeginc.dbshopping.userlist.view.UserListFragment

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
object Injector {
    lateinit var appComponent: ApplicationComponent
    lateinit var userComponent: UserComponent
    private val authComponent: AuthenticationComponent by lazy { appComponent.plus(AuthenticationModule()) }

    fun injectInAuthComponent(loginActivity: LoginActivity) = authComponent.inject(loginActivity)

    fun injectInAuthComponent(signUpActivity: SignUpActivity) = authComponent.inject(signUpActivity)

    fun injectInAppComponent(baseActivity: BaseActivity) = appComponent.inject(baseActivity)
    fun injectInAppComponent(baseFragment: BaseFragment) = appComponent.inject(baseFragment)
    fun injectInAppComponent(splashActivity: SplashActivity) = appComponent.inject(splashActivity)
    fun injectInAppComponent(loadingDialog: LoadingDialog) = appComponent.inject(loadingDialog)

    fun injectUserComponent(userListFragment: UserListFragment) = userComponent.inject(userListFragment)
    fun injectUserComponent(addListActivity: AddListActivity) = userComponent.inject(addListActivity)
    fun injectUserComponent(listDetailActivity: ListDetailActivity) = userComponent.inject(listDetailActivity)
    fun injectUserComponent(listItemsFragment: ListItemsFragment) = userComponent.inject(listItemsFragment)
    fun injectUserComponent(itemDetailActivity: ItemDetailActivity) = userComponent.inject(itemDetailActivity)
    fun injectUserComponent(editListNameDialog: EditListNameDialog) = userComponent.inject(editListNameDialog)
    fun injectUserComponent(homeActivity: HomeActivity) = userComponent.inject(homeActivity)
}