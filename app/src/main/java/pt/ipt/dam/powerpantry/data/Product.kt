package pt.ipt.dam.powerpantry.data

data class Product(
    val id: Int,
    val productCode: Long,
    val productName: String,
    val productPrice: Double,
)

//wrapper
data class AllProductsResponse(val products : List<Product>)
data class IdProductResponse(val product : Product)
data class CodeProductsResponse(val products : List<Product>)


