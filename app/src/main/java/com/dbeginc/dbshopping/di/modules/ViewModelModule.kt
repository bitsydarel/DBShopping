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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dbeginc.common.utils.ApplicationScope
import com.dbeginc.common.utils.DBShoppingViewModelFactory
import com.dbeginc.dbshopping.di.ViewModelKey
import com.dbeginc.lists.additem.AddItemViewModel
import com.dbeginc.lists.addlist.AddListViewModel
import com.dbeginc.lists.itemcomment.ItemCommentsViewModel
import com.dbeginc.lists.itemdetail.ItemDetailViewModel
import com.dbeginc.lists.listdetail.ListDetailViewModel
import com.dbeginc.lists.userlists.UserListsViewModel
import com.dbeginc.users.authentication.forgotpassword.ForgotPasswordViewModel
import com.dbeginc.users.authentication.login.LoginViewModel
import com.dbeginc.users.authentication.register.RegisterViewModel
import com.dbeginc.users.friends.friendrequests.FriendRequestViewModel
import com.dbeginc.users.profile.UserViewModel
import com.dbeginc.users.friends.sharelist.ShareListViewModel
import com.dbeginc.users.profile.profilesetting.ProfileSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by darel on 22.02.18.
 *
 * ViewModel Module
 */
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindForgotPasswordViewModel(forgotPasswordViewModel: ForgotPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddListViewModel::class)
    abstract fun bindAddListViewModel(addListViewModel: AddListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddItemViewModel::class)
    abstract fun bindAddItemViewModel(addItemViewModel: AddItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemDetailViewModel::class)
    abstract fun bindItemDetailViewModel(itemDetailViewModel: ItemDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListDetailViewModel::class)
    abstract fun bindListDetailViewModel(listDetailViewModel: ListDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserListsViewModel::class)
    abstract fun bindUserListsViewModel(userListsViewModel: UserListsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShareListViewModel::class)
    abstract fun bindShareListViewModel(shareListViewModel: ShareListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendRequestViewModel::class)
    abstract fun bindFriendRequestViewModel(friendRequestViewModel: FriendRequestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemCommentsViewModel::class)
    abstract fun bindItemCommentsViewModel(itemCommentsViewModel: ItemCommentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSettingsViewModel::class)
    abstract fun bindProfileSettingsViewModel(profileSettingsViewModel: ProfileSettingsViewModel): ViewModel

    @ApplicationScope
    @Binds
    abstract fun bindViewModelFactory(factory: DBShoppingViewModelFactory): ViewModelProvider.Factory
}