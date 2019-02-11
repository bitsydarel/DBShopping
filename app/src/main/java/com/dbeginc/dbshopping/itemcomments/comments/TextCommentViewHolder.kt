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

package com.dbeginc.dbshopping.itemcomments.comments

import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.TextCommentLayoutBinding
import com.dbeginc.dbshopping.itemcomments.CommentActionBridge
import com.dbeginc.dbshopping.utils.extensions.remove
import com.dbeginc.dbshopping.utils.extensions.show
import com.dbeginc.lists.viewmodels.CommentModel

/**
 * Created by darel on 12.03.18.
 *
 * Text comment viewHolder
 */
class TextCommentViewHolder (private val binding: TextCommentLayoutBinding, private val actionBridge: CommentActionBridge) : RecyclerView.ViewHolder(binding.root), CommentBridge {
    init {
        binding.retrySendingComment.setOnClickListener {
            actionBridge.resentComment(binding.comment!!)

            binding.retrySendingComment.remove()

            binding.deleteComment.remove()
        }

        binding.deleteComment.setOnClickListener {
            actionBridge.deleteComment(binding.comment!!, adapterPosition)

            binding.retrySendingComment.remove()

            binding.deleteComment.remove()
        }
    }

    override fun onCommentNotSent() {
        Toast.makeText(
                binding.root.context,
                R.string.delete_comment,
                Toast.LENGTH_LONG
        ).show()

        binding.retrySendingComment.show()

        binding.deleteComment.show()
    }

    fun bindTextComment(comment: CommentModel) {
        binding.comment = comment

        binding.executePendingBindings()
    }
}