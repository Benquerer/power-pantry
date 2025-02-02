package pt.ipt.dam.powerpantry.ui.favorites

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.Product
import pt.ipt.dam.powerpantry.databinding.FragmentFavoritesLoggedInBinding
import pt.ipt.dam.powerpantry.ui.gallery.GalleryRecyclerViewAdapter


/**
 * Fragment for the logged in version of the  "Favorites" page
 */
class FavoritesFragment : Fragment() {

    /**
     * View binding
     */
    private lateinit var binding: FragmentFavoritesLoggedInBinding

    /**
     * View model for data handling
     */
    private lateinit var favoritesViewModel: FavoritesViewModel

    /**
     * Recycler view for display
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Adapter for recycler view
     */
    private lateinit var favoritesAdapter: GalleryRecyclerViewAdapter

    /**
     * Swipe refresh layout to handle refreshing in recycler view
     */
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    /**
     * Shared preferences of user preferences
     */
    private lateinit var userPreferences: SharedPreferences

    /**
     * Favorites helper
     */
    private lateinit var favPrefHelper: FavPrefHelper

    /**
     * Method for creating the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init viewmodel
        favoritesViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(FavoritesViewModel::class.java)

        //initialize view binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites_logged_in, container, false)
        binding.favViewModel = favoritesViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //get shared preferences and start favhelper
        userPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        favPrefHelper = FavPrefHelper(requireContext())
        //init swipe refresh
        swipeRefreshLayout = binding.LoggedSwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshFavorites()
        }
        //init recycler view
        recyclerView = binding.rvLoggedFav
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoritesAdapter = GalleryRecyclerViewAdapter(emptyList()) { product -> showProductDetails(product) }
        recyclerView.adapter = favoritesAdapter
        //observe changes in favorites list
        favoritesViewModel.filteredProducts.observe(viewLifecycleOwner) { favoriteList ->
            favoritesAdapter = GalleryRecyclerViewAdapter(favoriteList) { product -> showProductDetails(product) }
            recyclerView.adapter = favoritesAdapter
        }

        //search functionality on searchbar
        binding.etLoggedFavSearchBar.addTextChangedListener(object : TextWatcher {
            //change value of query in viewmodel when the searchbar text changes
            override fun afterTextChanged(s: Editable?) {
                favoritesViewModel.searchQuery.value = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //initial fetch in api for favorites
        loadFavorites()

        return binding.root
    }

    /**
     * Method for fetching the user's favorites from api
     */
    private fun loadFavorites(){
        if(isAdded){
            //only fetch with this if list is null
            if(favoritesViewModel.products.value.isNullOrEmpty()){
                DataRepository.fetchAllProducts(
                    //handle onresult callback
                    onResult = { products ->
                        if(isAdded){
                            //get username of logged user
                            val username = userPreferences.getString("username", "guest") ?: "guest"
                            //get favorites list (barcodes of favorited products)
                            val favoritesList = favPrefHelper.getAllUserFavorites(username)
                            //get favorites products by filtering products with barcodes in favorites list
                            val favoriteProducts = products.filter { it.productCode in favoritesList }
                            //set in view model
                            favoritesViewModel.setFavorites(favoriteProducts)
                            //set refreshing false (just to be sure)
                            swipeRefreshLayout.isRefreshing = false
                            //log api action
                            Log.d("PowerPantry_API", "FETCHED DATA via loadFavorites")
                        }
                    },
                    //handle onerror callback
                    onError = { errorMessage ->
                        //set refreshing false (just to be sure)
                        swipeRefreshLayout.isRefreshing = false
                    }
                )
            }else{
                Log.d("PowerPantry_API", "No fetch done - list not null")
            }
        }else{
            Log.d("PowerPantry_API", "Fragment is not attached")
        }
    }

    /**
     * Method for refreshing the favorites list (forces change)
     */
    private fun refreshFavorites(){
        if(isAdded){
            DataRepository.fetchAllProducts(
                //handle onresult callback
                onResult = { products ->
                    if(isAdded){
                        //get username of logged user
                        val username = userPreferences.getString("username", "guest") ?: "guest"
                        //get favorites list (barcodes of favorited products)
                        val favoritesList = favPrefHelper.getAllUserFavorites(username)
                        //get favorites products by filtering products with barcodes in favorites list
                        val favoriteProducts = products.filter { it.productCode in favoritesList }
                        //set in view model
                        favoritesViewModel.forceSetFavorites(favoriteProducts)
                        //stop refreshing
                        swipeRefreshLayout.isRefreshing = false
                        Log.d("PowerPantry_API", "FETCHED DATA via refreshFavorites")
                    }
                },
                //handle onerror callback
                onError = { errorMessage ->
                    Log.e("PowerPantry_API",errorMessage)
                    //stop refreshing
                    swipeRefreshLayout.isRefreshing = false
                }
            )
        }else{
            Log.d("PowerPantry_API", "Fragment is not attached.")
        }
    }

    /**
     * Method for showing the product details in a bottom sheet
     *
     * @param product Product - The product to show details for
     */
    private fun showProductDetails(product: Product){

        //sheet dialog
        val sheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.product_sheet,null)

        //view references
        val productImage = view.findViewById<ImageView>(R.id.ivDetailedImage)
        val productName = view.findViewById<TextView>(R.id.tvDetailedName)
        val productBrand = view.findViewById<TextView>(R.id.tvDetailedBrand)
        val productDescription = view.findViewById<TextView>(R.id.tvDetailedDescription)
        val productPrice = view.findViewById<TextView>(R.id.tvDetailedPrice)
        val productCategory = view.findViewById<TextView>(R.id.tvDetailedCategory)
        val productCode = view.findViewById<TextView>(R.id.tvDetailedCode)

        //set values in sheet
        productName.text = product.productName
        productBrand.text = product.productBrand
        productDescription.text = product.productDescription
        productPrice.text = String.format("%.2f",product.productPrice) + "€" //make sure the price is formated
        productCategory.text = product.productCategory
        productCode.text = "${product.productCode}"
        //set image from url
        Glide.with(view.context)
            .load(product.productImage)
            .placeholder(R.drawable.ic_placeholder_img)
            .error(R.drawable.ic_image_error)
            .into(productImage)

        //handle favorite
        val favIcon = view.findViewById<ImageView>(R.id.ivFavorited)
        //check user login
        userPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val isLogged = userPreferences.getBoolean("isLoggedIn", false)
        if(isLogged){
            //get user favorites list
            val prefHelper = FavPrefHelper(requireContext())
            //check if product is favorited
            val username = userPreferences.getString("username", "guest") ?: "guest"
            var isFavorited = prefHelper.isCodeInFavorites(username,product.productCode)
            //favorite state when opening
            if (isFavorited) {
                favIcon.setImageResource(R.drawable.ic_favorite_full)
            } else {
                favIcon.setImageResource(R.drawable.ic_favorite_border)
            }
            //handle clicks
            favIcon.setOnClickListener{
                //change favorite state
                isFavorited = !isFavorited
                if(isFavorited){
                    //add to favorites
                    prefHelper.appendUserFavorite(username, product.productCode)
                    favIcon.setImageResource(R.drawable.ic_favorite_full)
                }else{
                    //remove
                    prefHelper.removeUserFavorite(username, product.productCode)
                    favIcon.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }else{
            favIcon.setImageResource(R.drawable.ic_halfstar)
            favIcon.setOnClickListener{
                //create alert dialog to inform user of member-only action
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.members_only_title)
                builder.setMessage(R.string.members_only_text)
                builder.setPositiveButton(R.string.ok){dialog,_ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
        //listener for onClick - refresh favorites when closing detailed view
        sheetDialog.setOnDismissListener{
            refreshFavorites()
        }
        //display sheet
        sheetDialog.setContentView(view)
        sheetDialog.show()

    }

}
