package pt.ipt.dam.powerpantry.ui.favorites

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pt.ipt.dam.powerpantry.R

class FavoritesFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_logged_in, container, false)

        // Show the user's favorite items or any other logic
        // Placeholder content for demonstration
        val tvFavorites = view.findViewById<TextView>(R.id.tvFavorites)
        tvFavorites.text = "Welcome to your Favorite Items Page!"

        return view
    }
}