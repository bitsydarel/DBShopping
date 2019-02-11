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

package com.dbeginc.dbshopping.utils.helper

import android.os.Bundle
import android.support.transition.Fade
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.addItem.AddItemFragment
import com.dbeginc.dbshopping.addlist.AddListFragment
import com.dbeginc.dbshopping.friendrequests.FriendRequestsFragment
import com.dbeginc.dbshopping.itemcomments.ItemCommentsFragment
import com.dbeginc.dbshopping.itemcomments.imagedetail.ExpandedImageFragment
import com.dbeginc.dbshopping.itemdetail.ItemDetailFragment
import com.dbeginc.dbshopping.listdetail.ListDetailFragment
import com.dbeginc.dbshopping.listdetail.listmembers.ListMemberFragment
import com.dbeginc.dbshopping.sharelist.ShareListFragment
import com.dbeginc.dbshopping.userlists.UserListsFragment

/**
 * Created by darel on 14.03.18.
 *
 *
 */
object MainNavigator {

    fun goToListsScreen(container: MainActivity) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val userListsFragment = UserListsFragment().apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                userListsFragment,
                UserListsFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToAddListScreen(container: MainActivity) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val addListFragment = AddListFragment().apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                addListFragment,
                AddListFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToFriendRequestsScreen(container: MainActivity) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val friendRequestsFragment = FriendRequestsFragment().apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                friendRequestsFragment,
                FriendRequestsFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToListDetail(container: MainActivity, args: Bundle) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val listDetailFragment = ListDetailFragment.newInstance(args).apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                listDetailFragment,
                ListDetailFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToListMembersScreen(container: MainActivity, listId: String) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val args = Bundle(1)
        args.putString(ConstantHolder.LIST_DATA_KEY, listId)

        val listMemberFragment = ListMemberFragment.newInstance(args).apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                listMemberFragment,
                ListMemberFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToShareListScreen(container: MainActivity, listId: String, listName:String) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val shareListFragment = ShareListFragment.newInstance(listId, listName).apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                shareListFragment,
                ShareListFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToAddItemScreen(container: MainActivity, listId: String) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(
                R.id.main_content,
                AddItemFragment.newInstance(listId),
                AddItemFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToItemDetailScreen(container: MainActivity, args: Bundle) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(
                R.id.main_content,
                ItemDetailFragment.newInstance(args),
                ItemDetailFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToItemComment(container: MainActivity, args: Bundle) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val itemCommentsFragment = ItemCommentsFragment.newInstance(args).apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                itemCommentsFragment,
                ItemCommentsFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun goToExpandedImageScreen(container: MainActivity, listId: String, itemId: String, imageUrl: String, senderNickname: String, senderAvatar: String) {
        val fragmentTransaction = container.supportFragmentManager.beginTransaction()

        val expandedImageFragment = ExpandedImageFragment.newInstance(listId, itemId, imageUrl, senderNickname, senderAvatar).apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                expandedImageFragment,
                ExpandedImageFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    fun onBackPressed(container: MainActivity): Boolean {
        val visibleScreen = container.supportFragmentManager.findFragmentById(R.id.main_content)

        return when(visibleScreen) {
            is AddListFragment -> {
                goToListsScreen(container)
                return true
            }
            is FriendRequestsFragment -> {
                goToListsScreen(container)
                return true
            }
            is ListDetailFragment -> {
                goToListsScreen(container)
                return true
            }
            is AddItemFragment -> {
                goToListDetail(container, visibleScreen.retrieveParentArguments())
                return true
            }
            is ShareListFragment -> {
                goToListDetail(container, visibleScreen.retrieveParentArguments())
                return true
            }
            is ItemDetailFragment -> {
                goToListDetail(container, visibleScreen.retrieveParentArguments())
                return true
            }
            is ItemCommentsFragment -> {
                goToItemDetailScreen(container, visibleScreen.retrieveParentArguments())
                return true
            }
            is ExpandedImageFragment -> {
                goToItemComment(container, visibleScreen.retrieveParentArguments())
                return true
            }
            else -> false
        }
    }
}