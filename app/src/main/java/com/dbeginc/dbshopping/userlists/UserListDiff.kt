package com.dbeginc.dbshopping.userlists

import com.dbeginc.dbshopping.base.BaseDataDiff
import com.dbeginc.lists.viewmodels.ListModel


class UserListDiff(oldList: List<ListModel>, newList: List<ListModel>) : BaseDataDiff<ListModel>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldData[oldItemPosition] == newData[newItemPosition]

}