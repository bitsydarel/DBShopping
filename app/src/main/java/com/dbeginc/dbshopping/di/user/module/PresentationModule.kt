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

package com.dbeginc.dbshopping.di.user.module

import com.dbeginc.dbshopping.addlist.AddListContract
import com.dbeginc.dbshopping.addlist.presenter.AddListPresenterImpl
import com.dbeginc.dbshopping.di.qualifiers.AppUserRepo
import com.dbeginc.dbshopping.di.qualifiers.StorageErrorManager
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.home.HomeContract
import com.dbeginc.dbshopping.home.presenter.HomePresenterImpl
import com.dbeginc.dbshopping.itemdetail.ItemDetailContract
import com.dbeginc.dbshopping.itemdetail.presenter.ItemDetailPresenterImpl
import com.dbeginc.dbshopping.listdetail.ListDetailContract
import com.dbeginc.dbshopping.listdetail.presenter.ListDetailPresenterImpl
import com.dbeginc.dbshopping.listitems.ListItemsContract
import com.dbeginc.dbshopping.listitems.presenter.ListItemsPresenterImpl
import com.dbeginc.dbshopping.settings.SettingsContract
import com.dbeginc.dbshopping.settings.presenter.SettingsPresenterImpl
import com.dbeginc.dbshopping.userlist.UserListContract
import com.dbeginc.dbshopping.userlist.presenter.UserListPresenterImpl
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 22.08.17.
 *
 * User Presentation module
 */
@Module class PresentationModule {

    @Provides
    internal fun provideSettingsPresenter() : SettingsContract.SettingsPresenter =
            SettingsPresenterImpl()

    @Provides
    internal fun provideItemDetailPresenter(dataRepo: IDataRepo, @StorageErrorManager errorManager: IErrorManager) : ItemDetailContract.ItemDetailPresenter =
            ItemDetailPresenterImpl(dataRepo, errorManager)

    @Provides
    internal fun provideListItemsPresenter(@AppUserRepo userRepo: IUserRepo, dataRepo: IDataRepo) : ListItemsContract.ListItemPresenter =
            ListItemsPresenterImpl(userRepo, dataRepo)

    @Provides
    internal fun provideListDetailPresenter(dataRepo: IDataRepo) : ListDetailContract.ListDetailPresenter =
            ListDetailPresenterImpl(dataRepo)

    @Provides
    internal fun provideUserListPresenter(dataRepo: IDataRepo) : UserListContract.UserListPresenter =
            UserListPresenterImpl(dataRepo = dataRepo)

    @Provides
    internal fun provideAddListPresenter(dataRepo: IDataRepo) : AddListContract.AddListPresenter =
            AddListPresenterImpl(dataRepo)

    @Provides
    internal fun provideHomePresenter(@AppUserRepo userRepo: IUserRepo) : HomeContract.HomePresenter = HomePresenterImpl(userRepo)
}