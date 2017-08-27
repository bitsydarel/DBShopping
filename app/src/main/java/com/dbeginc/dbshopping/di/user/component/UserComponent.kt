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
import com.dbeginc.dbshopping.userlist.view.UserListFragment
import dagger.Subcomponent

/**
 * Created by darel on 22.08.17.
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
}