package pt.ipt.dam.powerpantry.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pt.ipt.dam.powerpantry.databinding.FragmentGalleryBinding
import com.journeyapps.barcodescanner.CaptureActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository

class GalleryFragment : Fragment() {

    private val REQUEST_CODE_SCAN = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: GalleryRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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

        galleryViewModel.filteredProducts.observe(viewLifecycleOwner) {products ->
            productAdapter = GalleryRecyclerViewAdapter(products) { product ->
                Log.d("ANDRE_TEST", "CLICKED ${product.productName}")
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
        refreshData()

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupScanButton()
        }

        return binding.root
    }

    private fun refreshData(){
        if(isAdded){
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
                val barcode = data?.getStringExtra("SCAN_RESULT") ?: getString(R.string.no_barcode_detected)
                galleryViewModel.barcodeResult.value = barcode
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
                Toast.makeText(requireContext(), "Camera permission is required to scan barcodes.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
