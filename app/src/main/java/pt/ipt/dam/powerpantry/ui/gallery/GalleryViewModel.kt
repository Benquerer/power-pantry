package pt.ipt.dam.powerpantry.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pt.ipt.dam.powerpantry.R

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize with a string resource
    val barcodeResult = MutableLiveData<String>(
        application.getString(R.string.default_scan_text)
    )
}
