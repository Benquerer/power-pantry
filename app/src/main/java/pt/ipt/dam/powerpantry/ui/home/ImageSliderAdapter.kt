package pt.ipt.dam.powerpantry.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.powerpantry.databinding.ItemSliderBinding

/**
 * Adapter for the image slider
 */
class ImageSliderAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    /**
     * SliderViewHolder class for the image slider
     */
    inner class SliderViewHolder(private val binding: ItemSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Int) {
            binding.imageView.setImageResource(image)
        }
    }

    /**
     * Called when the view holder is created
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    /**
     * Called when the view holder is bound
     */
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(images[position])
    }

    /**
     * Returns the number of items in the list
     */
    override fun getItemCount(): Int = images.size
}

