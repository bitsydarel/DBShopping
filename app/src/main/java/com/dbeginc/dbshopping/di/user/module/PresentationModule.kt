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
import com.dbeginc.dbshopping.userlist.UserListContract
import com.dbeginc.dbshopping.userlist.presenter.UserListPresenterImpl
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 22.08.17.
 */
@Module class PresentationModule {

    @Provides
    internal fun provideItemDetailPresenter(dataRepo: IDataRepo, @StorageErrorManager errorManager: IErrorManager) : ItemDetailContract.ItemDetailPresenter =
            ItemDetailPresenterImpl(dataRepo, errorManager)

    @Provides
    internal fun provideListItemsPresenter(dataRepo: IDataRepo) : ListItemsContract.ListItemPresenter =
            ListItemsPresenterImpl(dataRepo)

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