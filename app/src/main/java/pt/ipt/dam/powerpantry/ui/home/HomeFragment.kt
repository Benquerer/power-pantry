package pt.ipt.dam.powerpantry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Activate Marquee Text
        binding.marqueeText.isSelected = true

        // Product images for the slider
        val images = listOf(
            R.drawable.ic_instagram,
            R.drawable.ic_youtube,
            R.drawable.ic_favorite_border,
            R.drawable.ic_github
        )

        // Set up Image Slider
        val adapter = ImageSliderAdapter(images)
        binding.imageSlider.adapter = adapter

        // Auto-slide images
        binding.imageSlider.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    postDelayed({ currentItem = (currentItem + 1) % images.size }, 3000)
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
