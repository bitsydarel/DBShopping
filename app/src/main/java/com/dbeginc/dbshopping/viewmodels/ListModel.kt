package com.dbeginc.dbshopping.viewmodels

import android.os.Parcel
import android.os.Parcelable

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 22.08.17.
 */
data class ListModel(val id: String, var name: String, var owner: String, var lastChange: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(owner)
        parcel.writeString(lastChange)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ListModel> {
        override fun createFromParcel(parcel: Parcel): ListModel = ListModel(parcel)

        override fun newArray(size: Int): Array<ListModel?> = arrayOfNulls(size)
    }

}