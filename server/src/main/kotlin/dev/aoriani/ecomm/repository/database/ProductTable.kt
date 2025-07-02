package dev.aoriani.ecomm.repository.database

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.ImmutableEntityClass
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.upsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.json.json
import java.math.BigDecimal

const val MAX_VARCHAR_LENGTH = 256

object ProductTable : IdTable<String>("products") {
    override val id: Column<EntityID<String>> = varchar(name = "id", length = MAX_VARCHAR_LENGTH).entityId()
    val name = varchar(name = "name", length = MAX_VARCHAR_LENGTH)
    val price = decimal(name = "price", precision = 20, scale = 2)
    val description = text(name = "description")
    val material = varchar("material", MAX_VARCHAR_LENGTH)
    val images = json<List<String>>("images", Json)
    val inStock = bool("in_stock")
    val countryOfOrigin = varchar("country_of_origin", MAX_VARCHAR_LENGTH)

    override val primaryKey = PrimaryKey(id)
}

class ProductEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : ImmutableEntityClass<String, ProductEntity>(ProductTable)

    val name by ProductTable.name
    val price by ProductTable.price
    val description by ProductTable.description
    val material by ProductTable.material
    val images by ProductTable.images
    val inStock by ProductTable.inStock
    val countryOfOrigin by ProductTable.countryOfOrigin
}

private const val IMAGE_URL_BASE = "https://aoriani.dev/static/images"

/**
 * Initializes the database schema for products.
 * Creates the [ProductTable] if it doesn't already exist.
 */
fun ProductTable.initializeSchema() {
    transaction {
        SchemaUtils.create(this@initializeSchema)
    }
}

/**
 * Seeds the [ProductTable] with initial sample data.
 * This function should be called within a transaction.
 * It populates the table with a predefined list of tech personality plushies.
 */
private fun ProductTable.doSeedData() {
    // Data seeding logic starts here
    ProductTable.upsert {
        it[id] = "elon_musk_plush"
            it[name] = "Elon Musk"
            it[price] = BigDecimal("34.99")
            it[description] = """
            This chibi-style plush of Elon Musk captures his visionary spirit with a mini SpaceX jacket and embroidered Tesla T-shirt details. Crafted from an ultra-soft microfiber blend reminiscent of aerospace materials, it features an oversized head, gentle pastel hues, and a tiny rocket accessory at his side. Perfect for fans of innovation, each stitch celebrates Musk’s journey from South Africa to the stars. A fun collector’s item and conversation starter for any tech enthusiast.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/elon_musk_plush.png")
            it[material] = "Aerospace-grade microfiber blend"
            it[inStock] = true
            it[countryOfOrigin] = "South Africa"
        }

        ProductTable.upsert {
            it[id] = "steve_jobs_plush"
            it[name] = "Steve Jobs"
            it[price] = BigDecimal("39.99")
            it[description] = """
            This premium Steve Jobs plush features his iconic black turtleneck and round glasses rendered in super-soft organic cotton fabric. Every detail—from the embroidered Apple logo to the miniature gadget in hand—evokes Jobs's legacy of sleek design and simplicity. With a slightly weighted base for display and a smooth, matte finish on the stitches, it's both a comforting keepsake and a tribute to Apple's co-founder. Handmade in the USA by eco-conscious artisans.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/steve_jobs_plush.png")
            it[material] = "100% organic cotton"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "bill_gates_plush"
            it[name] = "Bill Gates"
            it[price] = BigDecimal("29.99")
            it[description] = """
            Celebrate Microsoft's co-founder with this chibi-style Bill Gates plush, complete with his signature sweater and embroidered book accessory. Made from recycled polyester fibers, it sports large, friendly eyes and a gentle smile that reflect Gates's approachable, philanthropic persona. The cozy knit texture and carefully stitched logo capture his enduring impact on technology and global health. Perfect for desks, shelves, or gifting to anyone who admires his work.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/bill_gates_plush.png")
            it[material] = "Recycled polyester fiber"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "jeff_bezos_plush"
            it[name] = "Jeff Bezos"
            it[price] = BigDecimal("32.99")
            it[description] = """
            This Jeff Bezos plush brings the Amazon founder to life in chibi form, wearing a hoodie with the Amazon logo and clutching a mini Blue Origin rocket. Crafted from a premium bamboo-derived fabric, it's silky to the touch and environmentally friendly. The detailed stitching on the rocket and thoughtful shading on the face nod to Bezos's dual ventures in e-commerce and space exploration. A standout piece for any collection dedicated to modern pioneers.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/jeff_bezos_plush.png")
            it[material] = "Bamboo-derived plush fabric"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "mark_zuckerberg_plush"
            it[name] = "Mark Zuckerberg"
            it[price] = BigDecimal("27.49")
            it[description] = """
            Capture the social media pioneer with this Mark Zuckerberg plush, complete with a soft gray hoodie bearing the Facebook logo and a tiny fabric smartphone. Constructed from an eco-friendly cotton blend, it emphasizes comfort and durability while reflecting Zuckerberg's ethos of connection. The large embroidered eyes and subtle smile give it a friendly, approachable character. A perfect desk companion for anyone in tech or social networking.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/mark_zuckerberg_plush.png")
            it[material] = "Eco-friendly cotton blend"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "sundar_pichai_plush"
            it[name] = "Sundar Pichai"
            it[price] = BigDecimal("24.99")
            it[description] = """
            This Sundar Pichai plush celebrates Google's CEO with a chibi-style figure dressed in a branded sweater and holding a mini smartphone. Made from sustainably sourced Indian cotton, it's soft yet sturdy, honoring Pichai's roots in Chennai. The detailed embroidery and pastel color palette convey a modern, minimalist aesthetic. Ideal for gifting to fans of Google's innovations or anyone who admires his leadership journey.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/sundar_pichai_plush.png")
            it[material] = "Sustainably sourced Indian cotton"
            it[inStock] = true
            it[countryOfOrigin] = "India"
        }

        ProductTable.upsert {
            it[id] = "sam_altman_plush"
            it[name] = "Sam Altman"
            it[price] = BigDecimal("26.99")
            it[description] = """
            This chibi-style Sam Altman plush features a crisp OpenAI T-shirt and a tiny embroidered AI chip, symbolizing his role in advancing artificial intelligence. Crafted from a recycled polyester blend, it's lightweight and eco-conscious, much like Altman's tech-forward vision. The expressive embroidered face and soft pastel tones make it both a comforting companion and a nod to innovation. Perfect for display on any AI enthusiast's shelf.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/sam_altman_plush.png")
            it[material] = "Recycled polyester blend"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "john_carmack_plush"
            it[name] = "John Carmack"
            it[price] = BigDecimal("31.49")
            it[description] = """
            Honor the legendary programmer with this John Carmack plush, clad in an Oculus hoodie and holding a mini VR headset accessory. Made from a high-performance microfiber weave, it's durable enough for everyday handling yet luxuriously soft. The detailed bronze-framed glasses and embroidered logo pay tribute to Carmack's pioneering work in gaming and virtual reality. A must-have for fans of VR and gaming history.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/john_carmack_plush.png")
            it[material] = "High-performance microfiber weave"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "linus_torvalds_plush"
            it[name] = "Linus Torvalds"
            it[price] = BigDecimal("28.99")
            it[description] = """
            This Linus Torvalds chibi plush sports a classic Tux penguin T-shirt and holds a tiny penguin companion, celebrating his creation of Linux. Woven from a cozy Finnish wool-blend, it evokes Torvalds's Nordic heritage and the warmth of open-source community. The soft embroidery and friendly expression make it a delightful desk accent. Ideal for developers and open-source advocates alike.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/linus_torvalds_plush.png")
            it[material] = "Finnish wool-blend"
            it[inStock] = true
            it[countryOfOrigin] = "Finland"
        }

        ProductTable.upsert {
            it[id] = "tim_cook_plush"
            it[name] = "Tim Cook"
            it[price] = BigDecimal("33.49")
            it[description] = """
            This Tim Cook plush embodies Apple's modern elegance with a black crewneck shirt and a miniature iPhone accessory. Crafted from premium Pima cotton, it's exceptionally soft and long-lasting—just like the products he oversees. Detailed round glasses and subtle stitching reflect Cook's understated leadership style. A refined addition to any Apple enthusiast's collection.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/tim_cook_plush.png")
            it[material] = "Premium Pima cotton"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "richard_hendricks_plush"
            it[name] = "Richard Hendricks"
            it[price] = BigDecimal("22.99")
            it[description] = """
            Capture the heart of Silicon Valley comedy with this Richard Hendricks plush, wearing a Pied Piper hoodie and clutching a tiny compression icon. Constructed from soft fleece, it's both cozy and expressive, just like the character itself. The detailed embroidery and curly hair nod to his earnest personality and coding genius. A playful gift for fans of the show and tech culture alike.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/richard_hendricks_plush.png")
            it[material] = "Soft polyester fleece"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "larry_page_plush"
            it[name] = "Larry Page"
            it[price] = BigDecimal("30.99")
            it[description] = """
            Celebrate Google's co-founder with this Larry Page plush, donning a crisp Google T-shirt and holding a tiny Android figurine. Made from organic Egyptian cotton, it's breathable and luxuriously smooth—reflecting Page's commitment to innovation and quality. The bright embroidered logo and gentle smile capture his visionary spirit. A standout collectible for any tech aficionado.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/larry_page_plush.png")
            it[material] = "Organic Egyptian cotton"
            it[inStock] = true
            it[countryOfOrigin] = "United States"
        }

        ProductTable.upsert {
            it[id] = "lisa_su_plush"
            it[name] = "Lisa Su"
            it[price] = BigDecimal("36.99")
            it[description] = """
            This Lisa Su plush pays homage to AMD's CEO in a stylish polo shirt and with a miniature CPU chip accessory. Woven from a silk-cotton blend, it combines softness with a subtle sheen—much like the high-performance hardware she champions. The embroidered "AMD" logo and neat hairstyle details reflect her precision and leadership. A perfect piece for PC builders and tech fans.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/lisa_su_plush.png")
            it[material] = "Silk-cotton blend"
            it[inStock] = true
            it[countryOfOrigin] = "Taiwan"
        }

        ProductTable.upsert {
            it[id] = "alan_turing_plush"
            it[name] = "Alan Turing"
            it[price] = BigDecimal("44.99")
            it[description] = """
            Honor computing's founding father with this Alan Turing plush, featuring a tweed jacket and an embroidered punch card accessory. Crafted from a heritage tweed-wool blend in the UK, it evokes the era of early computation and Turing's legacy. The detailed glasses and thoughtful expression celebrate his brilliance and humanity. An elegant collectible for history buffs and tech lovers alike.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/alan_turing_plush.png")
            it[material] = "Heritage tweed-wool blend"
            it[inStock] = true
            it[countryOfOrigin] = "United Kingdom"
        }

        ProductTable.upsert {
            it[id] = "linus_sebastian_plush"
            it[name] = "Linus Sebastian"
            it[price] = BigDecimal("23.99")
            it[description] = """
            This Linus Sebastian plush captures the energy of the LTT founder in a bright orange LTT tee and with a tiny graphics card fabric replica. Made from a durable canvas-cotton blend, it's built for unboxing marathons and everyday display. The detailed hairstyle and embroidered logo showcase his charismatic on-camera presence. A must-have for tech reviewers and PC enthusiasts.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/linus_sebastian_plush.png")
            it[material] = "Canvas-cotton blend"
            it[inStock] = true
            it[countryOfOrigin] = "Canada"
        }

        ProductTable.upsert {
            it[id] = "satya_nadella_plush"
            it[name] = "Satya Nadella"
            it[price] = BigDecimal("27.99")
            it[description] = """
            Celebrate Microsoft's modern era with this Satya Nadella plush, dressed in a navy polo and holding a tiny Surface tablet. Constructed from a tech-grade nylon weave, it's sleek, durable, and lightweight—mirroring Nadella's transformative leadership. The rounded glasses and thoughtful smile evoke his focus on empathy and innovation. Ideal for showcasing on any office desk.
        """.trimIndent()
            it[images] = listOf("$IMAGE_URL_BASE/satya_nadella_plush.png")
            it[material] = "Tech-grade nylon weave"
            it[inStock] = true
            it[countryOfOrigin] = "India"
        }
    }
}

/**
 * Initializes the database by creating the schema and seeding it with sample data if the product table is currently empty.
 * This is the primary function called during application startup to set up the database.
 */
fun initializeDatabaseAndSeedIfEmpty() {
    ProductTable.initializeSchema() // Creates schema if it doesn't exist

    transaction {
        val isProductTableEmpty = ProductEntity.all().empty()
        if (isProductTableEmpty) {
            ProductTable.doSeedData()
        }
    }
}
