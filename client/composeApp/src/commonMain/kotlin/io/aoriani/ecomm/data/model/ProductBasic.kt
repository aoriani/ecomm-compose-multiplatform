package io.aoriani.ecomm.data.model

interface ProductBasic {
    typealias Id = String
    val id: Id
    val name: String
    val price: DollarAmount
    val thumbnailUrl: String?
}
