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

    //vals to hold full product list
    private val _products = MutableLiveData<List<Product>>(emptyList()) //private
    val products : LiveData<List<Product>> get() = _products //public

    //val for filtered list
    val filteredProducts = MutableLiveData<List<Product>>()

    //observer
    init{
        searchQuery.observeForever {
            filterProducts()
        }
    }

    fun setProducts(newProducts : List<Product>){
        if(_products.value.isNullOrEmpty()){
            _products.value = newProducts
            filterProducts()
        }
    }

    fun forceSetProducts(newProducts : List<Product>){
        _products.value = newProducts
        filterProducts()
    }

    private fun filterProducts(){
        val query = searchQuery.value?.lowercase() ?:""
        filteredProducts.value = _products.value?.filter { product ->
            product.productName.lowercase().contains(query) ||
                    product.productBrand.lowercase().contains(query) ||
                    product.productCode.toString().lowercase().contains(query) ||
                    product.productCategory.lowercase().contains(query)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchQuery.removeObserver { filterProducts() }
    }

}
