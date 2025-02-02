package pt.ipt.dam.powerpantry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.databinding.FragmentHomeBinding
import pt.ipt.dam.powerpantry.ui.home.ImageSliderAdapter

/**
 * Fragment for the Home screen
 */
class HomeFragment : Fragment() {

    /**
     * Binding for the fragment
     */
    private var _binding: FragmentHomeBinding? = null

    /**
     * Getter for the binding
     */
    private val binding get() = _binding!!

    /**
     * Runnable for auto-scrolling the image slider
     */
    private var autoScrollRunnable: Runnable? = null

    /**
     * Called when the fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // get view
        val view = binding.root

        //setup clickable icons using openURL method
        //sheety APO
        binding.sheetyLogo.setOnClickListener { openUrl("https://sheety.co/") }
        //IPT logo
        binding.iptLogo.setOnClickListener { openUrl("https://www.ipt.pt/") }
        //Informatica logo
        binding.informaticaLogo.setOnClickListener { openUrl("https://portal2.ipt.pt/pt/cursos/Licenciaturas/L_-_EI/") }

        //set up Marquee Text
        binding.marqueeText.isSelected = true

        //image list for slider
        val images = listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4
        )

        //set up slider
        val adapter = ImageSliderAdapter(images)
        binding.imageSlider.adapter = adapter

        //disable user input in the slider
        binding.imageSlider.isUserInputEnabled = false

        //start auto-scrolling on image slider
        startAutoScroll()

        return view

    }

    /**
     * Starts the auto-scrolling of the image slider
     */
    private fun startAutoScroll() {
        autoScrollRunnable = Runnable {
            val binding = _binding ?: return@Runnable
            //get the current position of the slider
            val currentPosition = binding.imageSlider.currentItem
            //get what the next position should be
            val nextPosition = (currentPosition + 1) % (binding.imageSlider.adapter?.itemCount ?: 1)
            //set new image
            binding.imageSlider.setCurrentItem(nextPosition, true)
            //set delay
            binding.imageSlider.postDelayed(autoScrollRunnable!!, 5000)
        }
        _binding?.imageSlider?.postDelayed(autoScrollRunnable!!, 5000)
    }

    /**
     * Method for opening a URL in the phone's browser
     *
     * @param url String - The URL to open
     */
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    /**
     * Called when the fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollRunnable?.let {
            binding.imageSlider.removeCallbacks(it)
        }
        _binding = null
    }
}
