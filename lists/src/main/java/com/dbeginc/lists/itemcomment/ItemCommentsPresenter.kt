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

package com.dbeginc.lists.itemcomment

import android.net.Uri
import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.lists.viewmodels.CommentModel
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * Created by darel on 10.03.18.
 *
 * Item comments presenter
 */
class ItemCommentsPresenter : MVPVPresenter<ItemCommentsView> {
    @Volatile var itemId: String = ""

    private val commentPrison : BlockingQueue<CommentModel> = ArrayBlockingQueue(1, true)

    override fun bind(view: ItemCommentsView) = view.setupView()

    fun validateTextComment(commentBody: String, view: ItemCommentsView) {
        if (commentBody.isNotBlank()) view.sendTextComment(commentBody)
    }

    fun validateImageComment(imageLocalPath: String, view: ItemCommentsView) {
        if (imageLocalPath.isNotBlank()) view.sendImageComment(imageLocalPath)
    }

    fun validateVoiceComment(voiceLocalFile: File, duration: Long, view: ItemCommentsView) {
        if (voiceLocalFile.exists() && duration > 0) view.sendVoiceComment(
                Uri.fromFile(voiceLocalFile).toString(),
                duration
        )
    }

    fun validateLocationComment(address: String, latitude: Double, longitude: Double, view: ItemCommentsView) {
        if (latitude != 0.0 && longitude != 0.0) view.sendLocationComment(address, latitude, longitude)
    }

    fun pushPendingComment(comment: CommentModel) = commentPrison.put(comment)

    fun releasePendingComment() : CommentModel? = commentPrison.poll()
}