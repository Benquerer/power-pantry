package pt.ipt.dam.powerpantry.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        swipeRefreshLayout.setOnRefreshListener {
            // Trigger data refresh when swipe-to-refresh is pulled
            refreshData()
        }

        //init recycler view
        recyclerView = binding.rvGallery
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //adapter (empty list)
        productAdapter = GalleryRecyclerViewAdapter(emptyList()) {}
        recyclerView.adapter = productAdapter
        //fetch
        DataRepository.fetchAllProducts(
            onResult = { products ->
                productAdapter = GalleryRecyclerViewAdapter(products) {product ->
                    Toast.makeText(requireContext(), "Clicked on: ${product.productName}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = productAdapter
                Toast.makeText(requireContext(), "FETCHED DATA", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Log.e("GALLERY ERROR",errorMessage)
            }
        )

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupScanButton()
        }

        return binding.root
    }

    private fun refreshData(){
        DataRepository.fetchAllProducts(
            onResult = { products ->
                productAdapter = GalleryRecyclerViewAdapter(products) {product ->
                    Toast.makeText(requireContext(), "Clicked on: ${product.productName}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = productAdapter
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), "FETCHED DATA", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Log.e("GALLERY ERROR",errorMessage)
                swipeRefreshLayout.isRefreshing = false
            }
        )
    }

    private fun setupScanButton() {
        val scanButton: Button = binding.scanButton
        scanButton.setOnClickListener {
            // Launch the BarcodeScannerActivity
            val intent = Intent(requireContext(), CaptureActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCAN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            val barcode = data?.getStringExtra("SCAN_RESULT") ?: getString(R.string.no_barcode_detected)
            galleryViewModel.barcodeResult.value = barcode
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
