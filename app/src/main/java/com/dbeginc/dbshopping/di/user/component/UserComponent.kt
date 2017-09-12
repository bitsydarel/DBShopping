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

package com.dbeginc.dbshopping.di.user.component

import com.dbeginc.dbshopping.addlist.view.AddListActivity
import com.dbeginc.dbshopping.di.scopes.UserScope
import com.dbeginc.dbshopping.di.user.module.PresentationModule
import com.dbeginc.dbshopping.di.user.module.UserModule
import com.dbeginc.dbshopping.home.view.HomeActivity
import com.dbeginc.dbshopping.itemdetail.view.ItemDetailActivity
import com.dbeginc.dbshopping.listdetail.view.EditListNameDialog
import com.dbeginc.dbshopping.listdetail.view.ListDetailActivity
import com.dbeginc.dbshopping.listitems.view.ListItemsFragment
import com.dbeginc.dbshopping.settings.view.SettingsActivity
import com.dbeginc.dbshopping.userlist.view.UserListFragment
import dagger.Subcomponent

/**
 * Created by darel on 22.08.17.
 *
 * User his subComponent
 */
@UserScope
@Subcomponent(modules = arrayOf(UserModule::class, PresentationModule::class))
interface UserComponent {
    fun inject(userListFragment: UserListFragment)
    fun inject(addListActivity: AddListActivity)
    fun inject(listDetailActivity: ListDetailActivity)
    fun inject(listItemsFragment: ListItemsFragment)
    fun inject(itemDetailActivity: ItemDetailActivity)
    fun inject(editListNameDialog: EditListNameDialog)
    fun inject(homeActivity: HomeActivity)
    fun inject(settingsActivity: SettingsActivity)
}