package pt.ipt.dam.powerpantry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize with a string resource
    val barcodeResult = MutableLiveData<String>(
        application.getString(R.string.default_scan_text)
    )
}
