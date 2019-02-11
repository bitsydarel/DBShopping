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

import com.dbeginc.dbshopping.addItem.AddItemFragment
import com.dbeginc.dbshopping.addlist.AddListFragment
import com.dbeginc.dbshopping.authentication.forgotpassword.ForgotPasswordFragment
import com.dbeginc.dbshopping.authentication.login.LoginFragment
import com.dbeginc.dbshopping.authentication.register.RegisterFragment
import com.dbeginc.dbshopping.friendrequests.FriendRequestsFragment
import com.dbeginc.dbshopping.itemcomments.ItemCommentsFragment
import com.dbeginc.dbshopping.itemdetail.ItemDetailFragment
import com.dbeginc.dbshopping.listdetail.ListDetailFragment
import com.dbeginc.dbshopping.listdetail.listmembers.ListMemberFragment
import com.dbeginc.dbshopping.settings.SettingsFragment
import com.dbeginc.dbshopping.sharelist.ShareListFragment
import com.dbeginc.dbshopping.splash.SplashFragment
import com.dbeginc.dbshopping.userlists.UserListsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by darel on 02.03.18.
 *
 * Dagger Fragments module
 */
@Module
abstract class FragmentsModule {
    @ContributesAndroidInjector()
    abstract fun contributeSplashFragment() : SplashFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment() : LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment() : RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment() : ForgotPasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeUserListsFragment() : UserListsFragment

    @ContributesAndroidInjector()
    abstract fun contributeListDetailFragment() : ListDetailFragment

    @ContributesAndroidInjector()
    abstract fun contributeListMemberFragment() : ListMemberFragment

    @ContributesAndroidInjector()
    abstract fun contributeItemDetailFragment() : ItemDetailFragment

    @ContributesAndroidInjector()
    abstract fun contributeAddListFragment() : AddListFragment

    @ContributesAndroidInjector()
    abstract fun contributeAddItemFragment() : AddItemFragment

    @ContributesAndroidInjector()
    abstract fun contributeShareListFragment() : ShareListFragment

    @ContributesAndroidInjector()
    abstract fun contributeFriendRequestsFragment() : FriendRequestsFragment

    @ContributesAndroidInjector()
    abstract fun contributeItemCommentsFragment() : ItemCommentsFragment

    @ContributesAndroidInjector()
    abstract fun contributeSettingsFragment() : SettingsFragment
}