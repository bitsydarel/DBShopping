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

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by darel on 03.09.17.
 *
 * Account Model from the user interface
 */
class AccountModel(val id: String, var name: String, var profileImage: String = "", var accountProviders: List<String> = listOf()) : Parcelable {

    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(profileImage)
        parcel.writeStringList(accountProviders)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AccountModel> {
        override fun createFromParcel(parcel: Parcel): AccountModel = AccountModel(parcel)
        override fun newArray(size: Int): Array<AccountModel?> = arrayOfNulls(size)
    }
}