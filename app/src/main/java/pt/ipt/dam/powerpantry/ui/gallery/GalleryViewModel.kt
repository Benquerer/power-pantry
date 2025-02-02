package pt.ipt.dam.powerpantry.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.data.Product

/**
 * View model for the gallery fragment
 */
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * LiveData for the barcode result - default is string from resources
     */
    val barcodeResult = MutableLiveData<String>(application.getString(R.string.default_scan_text))

    /**
     * LiveData for the search query - default is empty
     */
    val searchQuery = MutableLiveData<String>("")

    /**
     * LiveData for the list of products - default is empty
     */
    private val _products = MutableLiveData<List<Product>>(emptyList())

    /**
     * Public getter for the list of products
     */
    val products : LiveData<List<Product>> get() = _products

    /**
     * LiveData for the filtered list of products - default is empty
     */
    val filteredProducts = MutableLiveData<List<Product>>()

    /**
     * Query Observer to handle updates in search bar on Gallery
     */
    init{
        searchQuery.observeForever {
            filterProducts()
        }
    }

    /**
     * Method for setting the products list. Only sets if the list is empty.
     *
     * @param newProducts List<Product> - The new list of products to set.
     */
    fun setProducts(newProducts : List<Product>){
        if(_products.value.isNullOrEmpty()){
            _products.value = newProducts
            filterProducts()
        }
    }

    /**
     * Method for forcing setting the products list.
     *
     * @param newProducts List<Product> - The new list of products to set.
     */
    fun forceSetProducts(newProducts : List<Product>){
        _products.value = newProducts
        filterProducts()
    }

    /**
     * Method for filtering the products based on the search query.
     */
    private fun filterProducts(){
        val query = searchQuery.value?.lowercase() ?:""
        filteredProducts.value = _products.value?.filter { product ->
            product.productName.lowercase().contains(query) ||
                    product.productBrand.lowercase().contains(query) ||
                    product.productCode.toString().lowercase().contains(query) ||
                    product.productCategory.lowercase().contains(query)
        }
    }

    /**
     * Method for clearing the query observer when the view model is cleared
     */
    override fun onCleared() {
        super.onCleared()
        searchQuery.removeObserver { filterProducts() }
    }

}
