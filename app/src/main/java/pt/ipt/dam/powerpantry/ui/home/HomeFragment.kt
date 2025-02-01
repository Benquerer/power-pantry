package pt.ipt.dam.powerpantry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import pt.ipt.dam.powerpantry.databinding.FragmentHomeBinding
import pt.ipt.dam.powerpantry.ui.home.ImageSliderAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var autoScrollRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        val view = binding.root

        // Clickable Icons - Open URLs
        binding.sheetyLogo.setOnClickListener { openUrl("https://sheety.co/") }
        binding.iptLogo.setOnClickListener { openUrl("https://www.ipt.pt/") }
        binding.informaticaLogo.setOnClickListener { openUrl("https://portal2.ipt.pt/pt/cursos/Licenciaturas/L_-_EI/") }


        // Activate Marquee Text
        binding.marqueeText.isSelected = true

        // List of Images for Slider
        val images = listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4
        )

        // Set up Image Slider
        val adapter = ImageSliderAdapter(images)
        binding.imageSlider.adapter = adapter


        binding.imageSlider.isUserInputEnabled = false

        // Start Auto-Scroll
        startAutoScroll()

        return view

    }

    private fun startAutoScroll() {
        autoScrollRunnable = Runnable {

            val binding = _binding ?: return@Runnable

            val currentPosition = binding.imageSlider.currentItem
            val nextPosition = (currentPosition + 1) % (binding.imageSlider.adapter?.itemCount ?: 1)

            binding.imageSlider.setCurrentItem(nextPosition, true)

            binding.imageSlider.postDelayed(autoScrollRunnable!!, 5000)
        }


        _binding?.imageSlider?.postDelayed(autoScrollRunnable!!, 5000)
    }

    // Function to open a URL in the browser
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }



    override fun onDestroyView() {
        super.onDestroyView()


        autoScrollRunnable?.let {
            binding.imageSlider.removeCallbacks(it)
        }


        _binding = null
    }
}
