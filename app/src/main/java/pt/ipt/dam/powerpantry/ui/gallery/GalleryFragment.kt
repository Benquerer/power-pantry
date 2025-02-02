package pt.ipt.dam.powerpantry.ui.gallery

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import pt.ipt.dam.powerpantry.databinding.FragmentGalleryBinding
import com.journeyapps.barcodescanner.CaptureActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.Product
import pt.ipt.dam.powerpantry.ui.favorites.FavPrefHelper

class GalleryFragment : Fragment() {

    private val REQUEST_CODE_SCAN = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: GalleryRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    //preferences
    private lateinit var favoritesPreference :SharedPreferences
    private lateinit var userPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewModel
        galleryViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            GalleryViewModel::class.java)



        // Initialize Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        binding.viewModel = galleryViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //init swipe refresh
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { refreshData() }

        //init recycler view
        recyclerView = binding.rvGallery
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = GalleryRecyclerViewAdapter(emptyList()) {}
        recyclerView.adapter = productAdapter

        galleryViewModel.filteredProducts.observe(viewLifecycleOwner) {filteredList ->
            productAdapter = GalleryRecyclerViewAdapter(filteredList) { product ->
                showProductDetails(product)
            }
            recyclerView.adapter = productAdapter
        }

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                galleryViewModel.searchQuery.value = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //fetch initial data
        fetchData()

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupScanButton()
        }

        return binding.root
    }

    private fun fetchData(){
        if(isAdded){
            //only fetch if null
            if(galleryViewModel.products.value.isNullOrEmpty()){
                DataRepository.fetchAllProducts(
                    onResult = { products ->
                        if(isAdded){
                            galleryViewModel.setProducts(products)
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

    private fun refreshData(){
        if(isAdded){
            DataRepository.fetchAllProducts(
                onResult = { products ->
                    if(isAdded){
                        galleryViewModel.forceSetProducts(products)
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

    private fun setupScanButton() {
        val scanButton: ImageButton = binding.ibSearchByScan
        scanButton.setOnClickListener {
            // Launch the BarcodeScannerActivity
            val intent = Intent(requireContext(), CaptureActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCAN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (isAdded) {
                //get code
                val barcode = data?.getStringExtra("SCAN_RESULT") ?: getString(R.string.no_barcode_detected)
                //save barcode (might need)
                galleryViewModel.barcodeResult.value = barcode

                DataRepository.fetchProductByCode(barcode.toLong(),
                    onResult = {product -> showProductDetails(product)  },
                    onNotFound = { message -> productNotFound(message)},
                    onError = {errorMessage -> Log.d("ANDRE_TEST", errorMessage)},
                )

            } else {
                Log.w("ANDRE_TEST", "Fragment is not attached, skipping barcode result update.")
            }
        }

    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupScanButton()
            } else {
                Toast.makeText(requireContext(), "Para fazer scan de código de barras é preciso a permissão da Câmera.", Toast.LENGTH_SHORT).show()
            }
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
        productPrice.text = "${product.productPrice}"
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

    //alert for product not found
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
