package pt.ipt.dam.powerpantry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Activate Marquee Text
        binding.marqueeText.isSelected = true

        // List of Images for Slider
        val images = listOf(
            R.drawable.ic_github,
            R.drawable.ic_youtube,
            R.drawable.ic_instagram,
            R.drawable.ic_about
        )

        // Set up Image Slider
        val adapter = ImageSliderAdapter(images)
        binding.imageSlider.adapter = adapter

        // ✅ Disable User Interaction Correctly
        binding.imageSlider.isUserInputEnabled = false

        // Start Auto-Scroll
        startAutoScroll()

        return binding.root
    }

    private fun startAutoScroll() {
        autoScrollRunnable = Runnable {
            // ✅ Se o binding for null, sai da função para evitar crashes
            val binding = _binding ?: return@Runnable

            val currentPosition = binding.imageSlider.currentItem
            val nextPosition = (currentPosition + 1) % (binding.imageSlider.adapter?.itemCount ?: 1)

            binding.imageSlider.setCurrentItem(nextPosition, true)

            binding.imageSlider.postDelayed(autoScrollRunnable!!, 5000)
        }

        // ✅ Antes de agendar a execução, verifica se a View ainda existe
        _binding?.imageSlider?.postDelayed(autoScrollRunnable!!, 5000)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        // ✅ Cancela o auto-scroll antes de limpar o binding
        autoScrollRunnable?.let {
            binding.imageSlider.removeCallbacks(it)
        }

        // ✅ Limpa o binding para evitar referências nulas
        _binding = null
    }
}
