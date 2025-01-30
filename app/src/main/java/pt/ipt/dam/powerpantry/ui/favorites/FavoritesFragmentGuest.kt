package pt.ipt.dam.powerpantry.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.R

class FavoritesFragmentGuest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_guest, container, false)

        // Placeholder content for guest users
        val tvFavorites = view.findViewById<TextView>(R.id.tvFavorites)
        tvFavorites.text = "Please log in to view your favorite items!"

        return view
    }
}