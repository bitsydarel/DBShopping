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

package com.dbeginc.dbshopping.itemcomments.imagedetail


import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.FragmentExpandedImageBinding
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.ITEM_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LIST_DATA_KEY
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.WithParentRequiredArguments


/**
 * A simple [Fragment] subclass.
 */
class ExpandedImageFragment : Fragment(), WithParentRequiredArguments {
    private lateinit var listId: String
    private lateinit var itemId: String
    private lateinit var imageUrl: String
    private lateinit var senderNickname: String
    private lateinit var senderAvatar: String
    private lateinit var binding: FragmentExpandedImageBinding

    companion object {
        private const val IMAGE_DATA_KEY = "image_data_key"
        private const val SENDER_NICKNAME_KEY = "sender_nickname_key"
        private const val SENDER_AVATAR_KEY = "sender_avatar_key"

        @JvmStatic
        fun newInstance(listId: String, itemId: String, imageUrl: String, senderNickname: String, senderAvatar: String) : ExpandedImageFragment {
            val args = Bundle().apply {
                putString(LIST_DATA_KEY, listId)
                putString(ITEM_DATA_KEY, itemId)
                putString(IMAGE_DATA_KEY, imageUrl)
                putString(SENDER_NICKNAME_KEY, senderNickname)
                putString(SENDER_AVATAR_KEY, senderAvatar)
            }

            val fragment = ExpandedImageFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun retrieveParentArguments(): Bundle {
        return Bundle().apply {
            putString(LIST_DATA_KEY, listId)
            putString(ITEM_DATA_KEY, itemId)
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (savedState == null) {
            listId = arguments!!.getString(LIST_DATA_KEY)
            itemId = arguments!!.getString(ITEM_DATA_KEY)
            imageUrl = arguments!!.getString(IMAGE_DATA_KEY)
            senderNickname = arguments!!.getString(SENDER_NICKNAME_KEY)
            senderAvatar = arguments!!.getString(SENDER_AVATAR_KEY)

        } else {
            listId = savedState.getString(LIST_DATA_KEY)
            itemId = savedState.getString(ITEM_DATA_KEY)
            imageUrl = savedState.getString(IMAGE_DATA_KEY)
            senderNickname = savedState.getString(SENDER_NICKNAME_KEY)
            senderAvatar = savedState.getString(SENDER_AVATAR_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LIST_DATA_KEY, listId)
        outState.putString(ITEM_DATA_KEY, itemId)
        outState.putString(IMAGE_DATA_KEY, imageUrl)
        outState.putString(SENDER_NICKNAME_KEY, senderNickname)
        outState.putString(SENDER_AVATAR_KEY, senderAvatar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.action_share_image) {
            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SEND

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl))

            shareIntent.type = "image/jpeg"

            startActivity(Intent.createChooser(
                    shareIntent,
                    resources.getText(R.string.share_with)
            ))

            true
        }
        else super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_ExpandedImage)),
                R.layout.fragment_expanded_image,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.expandedImageToolbar)

        binding.expandedImageToolbar.setNavigationOnClickListener {
            MainNavigator.goToItemComment(
                    activity as MainActivity,
                    retrieveParentArguments()
            )
        }

        binding.avatar = senderAvatar

        binding.nickname = senderNickname

        binding.image = imageUrl
    }

}