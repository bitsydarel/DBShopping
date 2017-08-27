package com.dbeginc.domain.entities.requestmodel

/**
 * Created by darel on 20.08.17.
 *
 * Request Model for list
 */
data class ListRequestModel<out V>(val listId: String, val arg: V)