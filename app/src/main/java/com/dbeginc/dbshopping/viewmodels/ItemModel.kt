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

package com.dbeginc.dbshopping.viewmodels

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Pojo representing a item of an shopping list
 * from the user interface
 */
data class ItemModel(var id: String = UUID.randomUUID().toString(),
                     var name: String,
                     var itemOf: String,
                     var itemOwner: String,
                     var count: Long = 1,
                     var price: Double = 0.0,
                     var bought: Boolean = false,
                     var boughtBy: String = "",
                     var image: String = Uri.EMPTY.toString()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readDouble(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(itemOf)
        parcel.writeString(itemOwner)
        parcel.writeLong(count)
        parcel.writeDouble(price)
        parcel.writeByte(if (bought) 1 else 0)
        parcel.writeString(boughtBy)
        parcel.writeString(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ItemModel> {
        override fun createFromParcel(parcel: Parcel): ItemModel = ItemModel(parcel)

        override fun newArray(size: Int): Array<ItemModel?> = arrayOfNulls(size)
    }
}