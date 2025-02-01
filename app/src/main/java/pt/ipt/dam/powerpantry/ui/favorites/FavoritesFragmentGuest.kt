package pt.ipt.dam.powerpantry.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.MainActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.ui.login.LoginFragment

class FavoritesFragmentGuest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_guest, container, false)

        val btnLogin: Button = view.findViewById(R.id.btnLoginfavorite)

        btnLogin.setOnClickListener {
            (activity as? MainActivity)?.apply {
                replaceFragment(LoginFragment())
                navigationView.setCheckedItem(R.id.nav_login)
            }
        }

        return view
    }
}