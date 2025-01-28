package pt.ipt.dam.powerpantry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.CaptureActivity

class GalleryFragment : Fragment() {

    private val REQUEST_CODE_SCAN = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var barcodeResultTextView: TextView
    private var barcodeResult: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)

        barcodeResultTextView = rootView.findViewById(R.id.scan_result)

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupScanButton(rootView)
        }

        return rootView
    }

    private fun setupScanButton(rootView: View) {
        val scanButton: Button = rootView.findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            // Check if button click is detected
            Toast.makeText(requireContext(), "Button clicked", Toast.LENGTH_SHORT).show()

            // Launch the BarcodeScannerActivity
            val intent = Intent(requireContext(), CaptureActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCAN)
        }
    }


    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupScanButton(requireView())
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to scan barcodes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the result from the BarcodeScannerActivityui
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            val barcode = data?.getStringExtra("SCAN_RESULT")

            // Ensure that barcodeResult is reset each time
            barcodeResult = barcode ?: "No barcode detected"

            // Update the barcode result in the binding
            barcodeResultTextView.text = barcodeResult
        }
    }

}
