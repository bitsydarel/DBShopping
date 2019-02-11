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

package com.dbeginc.dbshopping.base

import android.support.v7.widget.RecyclerView
import java.util.*

/**
 * Created by darel on 06.03.18.
 *
 * Base RecyclerView adapter
 */
abstract class BaseAdapter<T , V: RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {
    protected abstract var data: LinkedList<T>

    override fun getItemCount(): Int = data.size

    abstract fun updateData(newData: List<T>)

    fun getDataSet() : List<T> = data

    fun cancelItemDeletion(position: Int) {
        notifyItemChanged(position)
    }

    fun remoteItemAt(position: Int) {
        data.removeAt(position)

        notifyItemRemoved(position)
    }
}