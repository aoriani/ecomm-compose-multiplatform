package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ImageColumnListAdapter : ColumnAdapter<ImmutableList<String>, String> {
    override fun decode(databaseValue: String): ImmutableList<String> {
        return databaseValue.split(",").toImmutableList()
    }

    override fun encode(value: ImmutableList<String>): String {
        return value.joinToString(separator = ",")
    }
}