package pt.ipt.dam.powerpantry.data

data class Product(
    val productCode: Long,
    val productName: String,
    val productBrand: String,
    val productCategory: String,
    val productDescription : String,
    val productPrice: Double,
    val productImage: String
)

//wrapper
data class AllProductsResponse(val products : List<Product>)
data class CodeProductsResponse(val products : List<Product>)
data class ProductRequest(val product: Product)
data class ProductResponse(val product: Product)



