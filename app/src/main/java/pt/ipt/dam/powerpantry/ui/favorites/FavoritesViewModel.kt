package pt.ipt.dam.powerpantry.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.ipt.dam.powerpantry.data.Product

/**
 * ViewModel for handling favorites related data
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Query for filtering products. Default is empty.
     */
    val searchQuery = MutableLiveData<String>("")

    /**
     * List of products. Default is empty, private list
     */
    private val _products = MutableLiveData<List<Product>>(emptyList())

    /**
     * Public getter for the list of products
     */
    val products : LiveData<List<Product>> get() = _products

    /**
     * Filtered list of products. Default is empty.
     */
    val filteredProducts = MutableLiveData<List<Product>>()

    /**
     * Query Observer to handle updates in search bar on Favorites view
     */
    init{
        searchQuery.observeForever {
            filterProducts()
        }
    }

    /**
     * Method for setting the favorites list. Only sets if the list is empty.
     *
     * @param newProducts List<Product> - The new list of products to set.
     */
    fun setFavorites(newProducts : List<Product>){
        if(_products.value.isNullOrEmpty()){
            _products.value = newProducts
            filterProducts()
        }
    }

    /**
     * Method for forcing setting the favorites list.
     *
     * @param newProducts List<Product> - The new list of products to set.
     */
    fun forceSetFavorites(newProducts : List<Product>){
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
                    product.productCode.toString().lowercase().contains(query)||
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