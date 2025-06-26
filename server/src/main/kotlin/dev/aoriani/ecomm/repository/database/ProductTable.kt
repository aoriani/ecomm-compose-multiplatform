package dev.aoriani.ecomm.repository.database

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.json.json

const val MAX_VARCHAR_LENGTH = 256

object ProductTable : IdTable<String>("products") {
    override val id: Column<EntityID<String>> = varchar(name = "id", length = MAX_VARCHAR_LENGTH).entityId()
    val name = varchar(name = "name", length = MAX_VARCHAR_LENGTH)
    val price = decimal(name = "price", precision = 20, scale = 2)
    val description = text(name = "description")
    val material = varchar("material", MAX_VARCHAR_LENGTH)
    val images = json<List<String>>("images", Json)
    val inStock = bool("in_stock")
    val countryOfOrigin = varchar("country", MAX_VARCHAR_LENGTH)

    override val primaryKey = PrimaryKey(id)
}