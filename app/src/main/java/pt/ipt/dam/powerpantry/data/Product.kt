package pt.ipt.dam.powerpantry.data

data class Product(
    val id: Int,
    val productCode: Long,
    val productName: String,
    val productBrand: String,
    val productCategory: String,
    val productDescription : String,
    val productPrice: Double,
    val productImage: Int
)

//wrapper
data class AllProductsResponse(val products : List<Product>)
data class CodeProductsResponse(val products : List<Product>)


