package pt.ipt.dam.powerpantry.ui.gallery

import android.app.Application
import android.app.DownloadManager.Query
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.data.Product

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize with a string resource
    val barcodeResult = MutableLiveData<String>(application.getString(R.string.default_scan_text))
    //val to hold search text
    val searchQuery = MutableLiveData<String>("")
    //val to hold full product list
    val allProducts = MutableLiveData<List<Product>>()

    fun setProducts(products : List<Product>){
        allProducts.value = products
    }

    //filtered product list
    val filteredProducts : LiveData<List<Product>> = MediatorLiveData<List<Product>>().apply {
        addSource(allProducts) { products ->
            value = filterProducts(products, searchQuery.value ?: "")
        }
        addSource(searchQuery) { query ->
            value = filterProducts(allProducts.value ?: emptyList(), query)
        }
    }

    private fun filterProducts(products: List<Product>, query: String): List<Product>{
        return products.filter {
            it.productName.contains(query,ignoreCase = true,) ||
            it.productBrand.contains(query,ignoreCase = true) ||
            it.productCode.toString().contains(query,ignoreCase = true)
        }
    }

}
