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

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesLoggedInBinding
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: GalleryRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var userPreferences: SharedPreferences
    private lateinit var favPrefHelper: FavPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        favoritesViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(FavoritesViewModel::class.java)

        // Initialize Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites_logged_in, container, false)
        binding.favViewModel = favoritesViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Init SharedPreferences and Fav Helper
        userPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        favPrefHelper = FavPrefHelper(requireContext())

        // Init Swipe Refresh
        swipeRefreshLayout = binding.LoggedSwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshFavorites()
        }

        // Init RecyclerView
        recyclerView = binding.rvLoggedFav
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoritesAdapter = GalleryRecyclerViewAdapter(emptyList()) { product -> showProductDetails(product) }
        recyclerView.adapter = favoritesAdapter

        // Observe favorites
        favoritesViewModel.filteredProducts.observe(viewLifecycleOwner) { favoriteList ->
            favoritesAdapter = GalleryRecyclerViewAdapter(favoriteList) { product -> showProductDetails(product) }
            recyclerView.adapter = favoritesAdapter
        }

        // Search functionality
        binding.etLoggedFavSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                favoritesViewModel.searchQuery.value = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Load initial data
        loadFavorites()

        return binding.root
    }

    private fun loadFavorites(){
        if(isAdded){
            //only fetch if null
            if(favoritesViewModel.products.value.isNullOrEmpty()){
                DataRepository.fetchAllProducts(
                    onResult = { products ->
                        if(isAdded){
                            val username = userPreferences.getString("username", "guest") ?: "guest"
                            val favoritesList = favPrefHelper.getAllUserFavorites(username)
                            val favoriteProducts = products.filter { it.productCode in favoritesList }
                            favoritesViewModel.setFavorites(favoriteProducts)
                            swipeRefreshLayout.isRefreshing = false
                            Log.d("ANDRE_TEST", "FETCHED DATA")
                        }
                    },
                    onError = { errorMessage ->
                        Log.e("ANDRE_TEST",errorMessage)
                        swipeRefreshLayout.isRefreshing = false
                    }
                )
            }else{
                Log.d("ANDRE_TEST", "DID NOT FETCH ANYTHING")
            }
        }else{
            Log.d("ANDRE_TEST", "Fragment is not attached, skipping barcode result update.")
        }
    }

    private fun refreshFavorites(){
        if(isAdded){
            DataRepository.fetchAllProducts(
                onResult = { products ->
                    if(isAdded){
                        val username = userPreferences.getString("username", "guest") ?: "guest"
                        val favoritesList = favPrefHelper.getAllUserFavorites(username)
                        val favoriteProducts = products.filter { it.productCode in favoritesList }
                        favoritesViewModel.forceSetFavorites(favoriteProducts)
                        swipeRefreshLayout.isRefreshing = false
                        Log.d("ANDRE_TEST", "FETCHED DATA via refresh")
                    }
                },
                onError = { errorMessage ->
                    Log.e("ANDRE_TEST",errorMessage)
                    swipeRefreshLayout.isRefreshing = false
                }
            )
        }else{
            Log.d("ANDRE_TEST", "Fragment is not attached, skipping barcode result update.")
        }
    }

    //showing dialog with details
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
        productPrice.text = "$${product.productPrice}"
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
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Member's only action")
                builder.setMessage("The \"Favorites\" is a member-only feature, please login or register to save your favorite products")
                builder.setPositiveButton("Okay"){dialog,_ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
        sheetDialog.setOnDismissListener{
            refreshFavorites()
        }
        //display
        sheetDialog.setContentView(view)
        sheetDialog.show()

    }

}
