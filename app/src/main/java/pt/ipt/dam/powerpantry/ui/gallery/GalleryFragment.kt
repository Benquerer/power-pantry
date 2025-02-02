package pt.ipt.dam.powerpantry.ui.gallery

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.CaptureActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.Product
import pt.ipt.dam.powerpantry.databinding.FragmentGalleryBinding
import pt.ipt.dam.powerpantry.ui.favorites.FavPrefHelper

/**
 * Fragment for displaying the app's gallery of products
 * Supports search via text and barcode scanning
 */
class GalleryFragment : Fragment() {

    /**
     * Request code for the barcode scanner
     */
    private val REQUEST_CODE_SCAN = 1001

    /**
     * Request code for the camera permission
     */
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    /**
     * Data binding for the fragment
     */
    private lateinit var binding: FragmentGalleryBinding

    /**
     * ViewModel for handling data
     */
    private lateinit var galleryViewModel: GalleryViewModel

    /**
     * Recycler view for displaying products
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Adapter for the recycler view
     */
    private lateinit var productAdapter: GalleryRecyclerViewAdapter

    /**
     * Swipe refresh layout for refreshing data in recycler view
     */
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    /**
     * Shared preferences on user's favorites
     */
    private lateinit var favoritesPreference :SharedPreferences

    /**
     * Shared preferences for user's information
     */
    private lateinit var userPreferences: SharedPreferences

    /**
     * Method for creating the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //initialize viewmodel
        galleryViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            GalleryViewModel::class.java)
        //initialize view binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        binding.viewModel = galleryViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //initialize swipe refresh
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { refreshData() }
        //initialize recycler view and adapter
        recyclerView = binding.rvGallery
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = GalleryRecyclerViewAdapter(emptyList()) {} //list empty by default
        recyclerView.adapter = productAdapter
        //observe changes in gallery (search)
        galleryViewModel.filteredProducts.observe(viewLifecycleOwner) {filteredList ->
            productAdapter = GalleryRecyclerViewAdapter(filteredList) { product ->
                showProductDetails(product)
            }
            recyclerView.adapter = productAdapter
        }
        //search functionality on searchbar
        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                galleryViewModel.searchQuery.value = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //fetch initial data
        fetchData()

        //check if camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //if not ask for it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            //if so setup scan button
            setupScanButton()
        }

        return binding.root
    }

    /**
     * Method for fetching products from API
     */
    private fun fetchData(){
        if(isAdded){
            //only fetch if null
            if(galleryViewModel.products.value.isNullOrEmpty()){
                DataRepository.fetchAllProducts(
                    //handle onresult callback
                    onResult = { products ->
                        if(isAdded){
                            //add products list to viewmodel
                            galleryViewModel.setProducts(products)
                            //set refreshing false (just to be sure)
                            swipeRefreshLayout.isRefreshing = false
                            //log
                            Log.d("PowerPantry_Gallery", "Fetched ${products.size} products from API")
                        }
                    },
                    //handle on error callback
                    onError = { errorMessage ->
                        //log error
                        Log.e("PowerPantry_Gallery",errorMessage)
                        //set refreshing false (just to be sure)
                        swipeRefreshLayout.isRefreshing = false
                    }
                )
            }else{
                Log.d("PowerPantry_Gallery", "Did not fetch data - list not null")
            }
        }else{
            Log.d("PowerPantry_Gallery", "Fragment is not attached, skipping barcode result update.")
        }
    }

    /**
     * Method for refreshing the data in the recycler view
     */
    private fun refreshData(){
        if(isAdded){
            DataRepository.fetchAllProducts(
                //handle onresult callback
                onResult = { products ->
                    if(isAdded){
                        //set products list on viewmodel
                        galleryViewModel.forceSetProducts(products)
                        //set refreshing false (just to be sure)
                        swipeRefreshLayout.isRefreshing = false
                        Log.d("PowerPantry_Gallery", "FETCHED DATA via refresh")
                    }
                },
                onError = { errorMessage ->
                    //log
                    Log.e("PowerPantry_Gallery",errorMessage)
                    //set refreshing false (just to be sure)
                    swipeRefreshLayout.isRefreshing = false
                }
            )
        }else{
            Log.d("PowerPantry_Gallery", "Fragment is not attached, skipping barcode result update.")
        }
    }

    /**
     * Method for setting up the scan button
     */
    private fun setupScanButton() {
        //get reference to scan button
        val scanButton: ImageButton = binding.ibSearchByScan
        //set listener to launch activity
        scanButton.setOnClickListener {
            // Launch the BarcodeScannerActivity
            val intent = Intent(requireContext(), CaptureActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCAN)
        }
    }

    /**
     * Method for handling the result of the barcode scanner
     *
     * @param requestCode The request code passed in `startActivityForResult()`
     * @param resultCode The integer result code returned by the child activity
     * @param data An Intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle the result of the barcode scanner
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (isAdded) {
                //get code
                val barcode = data?.getStringExtra("SCAN_RESULT") ?: getString(R.string.no_barcode_detected)
                //save barcode (might need)
                galleryViewModel.barcodeResult.value = barcode
                //get product by barcode
                DataRepository.fetchProductByCode(barcode.toLong(),
                    //show product details
                    onResult = {product -> showProductDetails(product)  },
                    //show dialog for not found
                    onNotFound = { message -> productNotFound(message)},
                    //log error
                    onError = {errorMessage -> Log.d("PowerPantry_Gallery", errorMessage)},
                )
            } else {
                Log.d("PowerPantry_Gallery", "Fragment is not attached, skipping barcode result update.")
            }
        }
    }

    /**
     * Method for handling the result of the camera permission request
     *
     * @param requestCode Int - The request code passed in requestPermissions
     * @param permissions Array<out String> - The requested permissions
     * @param grantResults IntArray - The grant results for the corresponding permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //handle result
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if granted, setup scan button
                setupScanButton()
            } else {
                //if not granted, show toast
                Toast.makeText(requireContext(), "Para fazer scan de código de barras é preciso a permissão da Câmera.", Toast.LENGTH_SHORT).show()
            }
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
        productPrice.text = String.format("%.2f",product.productPrice) + "€"
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
            //user is not logged in
            //set icon to half star (member-only)
            favIcon.setImageResource(R.drawable.ic_halfstar)
            //setup onclick to show alert of member-only feature
            favIcon.setOnClickListener{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Ação disponível só para membros.")
                builder.setMessage("Os \"Favoritos\" são uma funcionalidade disponível só para membros, para acessares os teus produtos favoritos entra na tua conta ou regista-te na aplicação.")
                builder.setPositiveButton("Ok"){dialog,_ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
        //display
        sheetDialog.setContentView(view)
        sheetDialog.show()

    }

    /**
     * Method for showing a dialog for product not found
     *
     * @param message String - The message to show in the dialog
     */
    private fun productNotFound( message:String ){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Produto não encontrado.")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

}
