package io.aoriani.ecomm.data.repositories.db

import app.cash.sqldelight.ColumnAdapter
import io.aoriani.ecomm.data.model.DollarAmount

class DollarAmountAdapter : ColumnAdapter<DollarAmount, String> {
    override fun decode(databaseValue: String): DollarAmount = DollarAmount(databaseValue)
    override fun encode(value: DollarAmount): String = value.toString()
}