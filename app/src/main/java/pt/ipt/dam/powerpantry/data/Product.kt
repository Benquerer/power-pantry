package pt.ipt.dam.powerpantry.data


/**
 * Data class to handle products
 */
data class Product(
    val productCode: Long,
    val productName: String,
    val productBrand: String,
    val productCategory: String,
    val productDescription : String,
    val productPrice: Double,
    val productImage: String
)

/**
 * Wrapper for response on fetching all products
 */
data class AllProductsResponse(val products : List<Product>)

/**
 * Wrapper for response on fetching products by barcode filter (same as AllProductsResponse)
 */
data class CodeProductsResponse(val products : List<Product>)

/**
 * Wrapper for request on posting a product
 */
data class ProductRequest(val product: Product)

/**
 * Wrapper for response on posting a product
 */
data class ProductResponse(val product: Product)



