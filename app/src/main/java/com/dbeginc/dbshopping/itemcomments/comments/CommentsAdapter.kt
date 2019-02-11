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

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.dbshopping.itemcomments.CommentActionBridge
import com.dbeginc.domain.entities.data.CommentType
import com.dbeginc.lists.viewmodels.CommentModel
import java.util.*

/**
 * Created by darel on 09.03.18.
 *
 * Comments Adapter
 */
class CommentsAdapter(override var data: LinkedList<CommentModel> = LinkedList(), private val actionBridge: CommentActionBridge) : BaseAdapter<CommentModel, RecyclerView.ViewHolder>() {
    companion object {
        private const val TEXT_COMMENT_VIEW = 1
        private const val IMAGE_COMMENT_VIEW = 2
        private const val VOICE_COMMENT_VIEW = 3
        private const val LOCATION_COMMENT_VIEW = 4
    }

    private val _holders = HashMap<String, CommentBridge?>()
    private var container: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            TEXT_COMMENT_VIEW -> TextCommentViewHolder(
                    DataBindingUtil.inflate(
                            inflater,
                            R.layout.text_comment_layout,
                            parent,
                            false
                    ),
                    actionBridge
            )
            IMAGE_COMMENT_VIEW -> ImageCommentViewHolder(
                    DataBindingUtil.inflate(
                            inflater,
                            R.layout.image_comment_layout,
                            parent,
                            false
                    ),
                    actionBridge
            )
            VOICE_COMMENT_VIEW -> VoiceCommentViewHolder(
                    DataBindingUtil.inflate(
                            inflater,
                            R.layout.voice_comment_layout,
                            parent,
                            false
                    ),
                    actionBridge
            )
            LOCATION_COMMENT_VIEW -> LocationCommentViewHolder(
                    DataBindingUtil.inflate(
                            inflater,
                            R.layout.location_comment_layout,
                            parent,
                            false
                    ),
                    actionBridge
            )
            else -> TextCommentViewHolder(
                    DataBindingUtil.inflate(
                            inflater,
                            R.layout.text_comment_layout,
                            parent,
                            false
                    ),
                    actionBridge
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentOrDate = data[position]

        when(holder) {
            is TextCommentViewHolder -> holder.bindTextComment(commentOrDate)
            is ImageCommentViewHolder -> holder.bindImageComment(commentOrDate)
            is VoiceCommentViewHolder -> holder.bindVoiceComment(commentOrDate)
            is LocationCommentViewHolder -> holder.bindLocationComment(commentOrDate)
        }

        if (_holders.containsKey(commentOrDate.uniqueId) && holder is CommentBridge) {
            _holders[commentOrDate.uniqueId] = holder
        }

    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        val comment = data[position]

        return when(comment.commentType) {
            CommentType.TEXT -> TEXT_COMMENT_VIEW
            CommentType.IMAGE -> IMAGE_COMMENT_VIEW
            CommentType.VOICE -> VOICE_COMMENT_VIEW
            CommentType.LOCATION -> LOCATION_COMMENT_VIEW
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        container = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        recyclerView.clearOnScrollListeners()

        container = null
    }

    @Synchronized
    override fun updateData(newData: List<CommentModel>) {
        val diffResult = DiffUtil.calculateDiff(
                UserCommentDiff(
                        data.toList(),
                        newData
                )
        )

        data = LinkedList(newData)

        diffResult.dispatchUpdatesTo(this)

        container?.smoothScrollToPosition(data.size)
    }

    fun setCommentFailedToBeSent(comment: CommentModel?) {
        comment?.let {
            _holders.remove(comment.uniqueId)?.onCommentNotSent()
        }
    }

    fun addComment(comment: CommentModel) {
        _holders[comment.uniqueId] = null

        data.add(comment)

        notifyItemInserted(data.size)

        container?.smoothScrollToPosition(data.size)
    }

}