package dev.aoriani.ecomm.data.repositories

import dev.aoriani.ecomm.data.repositories.HardcodedProductRepositoryImpl.getAll
import dev.aoriani.ecomm.data.repositories.HardcodedProductRepositoryImpl.getById
import dev.aoriani.ecomm.domain.models.Product
import dev.aoriani.ecomm.domain.models.ProductNotFoundException
import dev.aoriani.ecomm.domain.repositories.ProductRepository
import java.math.BigDecimal

/**
 * Base URL for accessing product images. This constant is used to construct
 * the full URL for each product image.
 */
private const val IMAGE_URL_BASE = "https://api.aoriani.dev/static/images"

/**
 * A concrete implementation of [dev.aoriani.ecomm.domain.repositories.ProductRepository] that provides a hardcoded list of [Product] objects.
 * This repository is primarily used for demonstration and testing purposes, offering a static dataset
 * of products without requiring a database or external service.
 */
object HardcodedProductRepositoryImpl : ProductRepository {

    /**
     * In-memory hardcoded catalog of products exposed by this repository.
     * Image URLs are built using [IMAGE_URL_BASE] and stored per item in the [Product.images] field.
     * This immutable list backs [getAll] and [getById] and is not intended for mutation.
     */
    private val products = listOf(
        Product(
            id = "elon_musk_plush",
            name = "Elon Musk",
            price = BigDecimal("34.99"),
            description = """
            This chibi-style plush of Elon Musk captures his visionary spirit with a mini SpaceX jacket and embroidered Tesla T-shirt details. Crafted from an ultra-soft microfiber blend reminiscent of aerospace materials, it features an oversized head, gentle pastel hues, and a tiny rocket accessory at his side. Perfect for fans of innovation, each stitch celebrates Musk’s journey from South Africa to the stars. A fun collector’s item and conversation starter for any tech enthusiast.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/elon_musk_plush.png"),
            material = "Aerospace-grade microfiber blend",
            inStock = true,
            countryOfOrigin = "South Africa"
        ),
        Product(
            id = "steve_jobs_plush",
            name = "Steve Jobs",
            price = BigDecimal("39.99"),
            description = """
            This premium Steve Jobs plush features his iconic black turtleneck and round glasses rendered in super-soft organic cotton fabric. Every detail—from the embroidered Apple logo to the miniature gadget in hand—evokes Jobs’s legacy of sleek design and simplicity. With a slightly weighted base for display and a smooth, matte finish on the stitches, it’s both a comforting keepsake and a tribute to Apple’s co-founder. Handmade in the USA by eco-conscious artisans.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/steve_jobs_plush.png"),
            material = "100% organic cotton",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "bill_gates_plush",
            name = "Bill Gates",
            price = BigDecimal("29.99"),
            description = """
            Celebrate Microsoft’s co-founder with this chibi-style Bill Gates plush, complete with his signature sweater and embroidered book accessory. Made from recycled polyester fibers, it sports large, friendly eyes and a gentle smile that reflect Gates’s approachable, philanthropic persona. The cozy knit texture and carefully stitched logo capture his enduring impact on technology and global health. Perfect for desks, shelves, or gifting to anyone who admires his work.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/bill_gates_plush.png"),
            material = "Recycled polyester fiber",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "jeff_bezos_plush",
            name = "Jeff Bezos",
            price = BigDecimal("32.99"),
            description = """
            This Jeff Bezos plush brings the Amazon founder to life in chibi form, wearing a hoodie with the Amazon logo and clutching a mini Blue Origin rocket. Crafted from a premium bamboo-derived fabric, it’s silky to the touch and environmentally friendly. The detailed stitching on the rocket and thoughtful shading on the face nod to Bezos’s dual ventures in e-commerce and space exploration. A standout piece for any collection dedicated to modern pioneers.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/jeff_bezos_plush.png"),
            material = "Bamboo-derived plush fabric",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "mark_zuckerberg_plush",
            name = "Mark Zuckerberg",
            price = BigDecimal("27.49"),
            description = """
            Capture the social media pioneer with this Mark Zuckerberg plush, complete with a soft gray hoodie bearing the Facebook logo and a tiny fabric smartphone. Constructed from an eco-friendly cotton blend, it emphasizes comfort and durability while reflecting Zuckerberg’s ethos of connection. The large embroidered eyes and subtle smile give it a friendly, approachable character. A perfect desk companion for anyone in tech or social networking.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/mark_zuckerberg_plush.png"),
            material = "Eco-friendly cotton blend",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "sundar_pichai_plush",
            name = "Sundar Pichai",
            price = BigDecimal("24.99"),
            description = """
            This Sundar Pichai plush celebrates Google’s CEO with a chibi-style figure dressed in a branded sweater and holding a mini smartphone. Made from sustainably sourced Indian cotton, it’s soft yet sturdy, honoring Pichai’s roots in Chennai. The detailed embroidery and pastel color palette convey a modern, minimalist aesthetic. Ideal for gifting to fans of Google’s innovations or anyone who admires his leadership journey.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/sundar_pichai_plush.png"),
            material = "Sustainably sourced Indian cotton",
            inStock = true,
            countryOfOrigin = "India"
        ),
        Product(
            id = "sam_altman_plush",
            name = "Sam Altman",
            price = BigDecimal("26.99"),
            description = """
            This chibi-style Sam Altman plush features a crisp OpenAI T-shirt and a tiny embroidered AI chip, symbolizing his role in advancing artificial intelligence. Crafted from a recycled polyester blend, it’s lightweight and eco-conscious, much like Altman’s tech-forward vision. The expressive embroidered face and soft pastel tones make it both a comforting companion and a nod to innovation. Perfect for display on any AI enthusiast’s shelf.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/sam_altman_plush.png"),
            material = "Recycled polyester blend",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "john_carmack_plush",
            name = "John Carmack",
            price = BigDecimal("31.49"),
            description = """
            Honor the legendary programmer with this John Carmack plush, clad in an Oculus hoodie and holding a mini VR headset accessory. Made from a high-performance microfiber weave, it’s durable enough for everyday handling yet luxuriously soft. The detailed bronze-framed glasses and embroidered logo pay tribute to Carmack’s pioneering work in gaming and virtual reality. A must-have for fans of VR and gaming history.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/john_carmack_plush.png"),
            material = "High-performance microfiber weave",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "linus_torvalds_plush",
            name = "Linus Torvalds",
            price = BigDecimal("28.99"),
            description = """
            This Linus Torvalds chibi plush sports a classic Tux penguin T-shirt and holds a tiny penguin companion, celebrating his creation of Linux. Woven from a cozy Finnish wool-blend, it evokes Torvalds’s Nordic heritage and the warmth of open-source community. The soft embroidery and friendly expression make it a delightful desk accent. Ideal for developers and open-source advocates alike.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/linus_torvalds_plush.png"),
            material = "Finnish wool-blend",
            inStock = true,
            countryOfOrigin = "Finland"
        ),
        Product(
            id = "tim_cook_plush",
            name = "Tim Cook",
            price = BigDecimal("33.49"),
            description = """
            This Tim Cook plush embodies Apple’s modern elegance with a black crewneck shirt and a miniature iPhone accessory. Crafted from premium Pima cotton, it’s exceptionally soft and long-lasting—just like the products he oversees. Detailed round glasses and subtle stitching reflect Cook’s understated leadership style. A refined addition to any Apple enthusiast’s collection.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/tim_cook_plush.png"),
            material = "Premium Pima cotton",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "richard_hendricks_plush",
            name = "Richard Hendricks",
            price = BigDecimal("22.99"),
            description = """
            Capture the heart of Silicon Valley comedy with this Richard Hendricks plush, wearing a Pied Piper hoodie and clutching a tiny compression icon. Constructed from soft fleece, it’s both cozy and expressive, just like the character itself. The detailed embroidery and curly hair nod to his earnest personality and coding genius. A playful gift for fans of the show and tech culture alike.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/richard_hendricks_plush.png"),
            material = "Soft polyester fleece",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "larry_page_plush",
            name = "Larry Page",
            price = BigDecimal("30.99"),
            description = """
            Celebrate Google’s co-founder with this Larry Page plush, donning a crisp Google T-shirt and holding a tiny Android figurine. Made from organic Egyptian cotton, it’s breathable and luxuriously smooth—reflecting Page’s commitment to innovation and quality. The bright embroidered logo and gentle smile capture his visionary spirit. A standout collectible for any tech aficionado.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/larry_page_plush.png"),
            material = "Organic Egyptian cotton",
            inStock = true,
            countryOfOrigin = "United States"
        ),
        Product(
            id = "lisa_su_plush",
            name = "Lisa Su",
            price = BigDecimal("36.99"),
            description = """
            This Lisa Su plush pays homage to AMD’s CEO in a stylish polo shirt and with a miniature CPU chip accessory. Woven from a silk-cotton blend, it combines softness with a subtle sheen—much like the high-performance hardware she champions. The embroidered “AMD” logo and neat hairstyle details reflect her precision and leadership. A perfect piece for PC builders and tech fans.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/lisa_su_plush.png"),
            material = "Silk-cotton blend",
            inStock = true,
            countryOfOrigin = "Taiwan"
        ),
        Product(
            id = "alan_turing_plush",
            name = "Alan Turing",
            price = BigDecimal("44.99"),
            description = """
            Honor computing’s founding father with this Alan Turing plush, featuring a tweed jacket and an embroidered punch card accessory. Crafted from a heritage tweed-wool blend in the UK, it evokes the era of early computation and Turing’s legacy. The detailed glasses and thoughtful expression celebrate his brilliance and humanity. An elegant collectible for history buffs and tech lovers alike.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/alan_turing_plush.png"),
            material = "Heritage tweed-wool blend",
            inStock = true,
            countryOfOrigin = "United Kingdom"
        ),
        Product(
            id = "linus_sebastian_plush",
            name = "Linus Sebastian",
            price = BigDecimal("23.99"),
            description = """
            This Linus Sebastian plush captures the energy of the LTT founder in a bright orange LTT tee and with a tiny graphics card fabric replica. Made from a durable canvas-cotton blend, it’s built for unboxing marathons and everyday display. The detailed hairstyle and embroidered logo showcase his charismatic on-camera presence. A must-have for tech reviewers and PC enthusiasts.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/linus_sebastian_plush.png"),
            material = "Canvas-cotton blend",
            inStock = true,
            countryOfOrigin = "Canada"
        ),
        Product(
            id = "satya_nadella_plush",
            name = "Satya Nadella",
            price = BigDecimal("27.99"),
            description = """
            Celebrate Microsoft’s modern era with this Satya Nadella plush, dressed in a navy polo and holding a tiny Surface tablet. Constructed from a tech-grade nylon weave, it’s sleek, durable, and lightweight—mirroring Nadella’s transformative leadership. The rounded glasses and thoughtful smile evoke his focus on empathy and innovation. Ideal for showcasing on any office desk.
        """.trimIndent(),
            images = listOf("$IMAGE_URL_BASE/satya_nadella_plush.png"),
            material = "Tech-grade nylon weave",
            inStock = true,
            countryOfOrigin = "India"
        )
    )

    /**
     * Retrieves all hardcoded products.
     * @return Result containing the complete list of [Product] objects (always Result.success in this implementation).
     */
    override suspend fun getAll(): Result<List<Product>> = Result.success(products)

    /**
     * Retrieves a product by its unique id.
     * @param id The [String] identifier of the product to retrieve.
     * @return Result containing the matching [Product] if found, or Result.success(null) if no product matches the given id.
     */
    override suspend fun getById(id: String): Result<Product> {
        return products.firstOrNull { it.id == id }?.let { Result.success(it) } ?: Result.failure(
            ProductNotFoundException("Product with id $id not found"))
    }
}
