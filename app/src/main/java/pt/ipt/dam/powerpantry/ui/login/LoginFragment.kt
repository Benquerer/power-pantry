package pt.ipt.dam.powerpantry.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam.powerpantry.HomeFragment
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.ui.favorites.FavoritesFragment
import pt.ipt.dam.powerpantry.ui.favorites.FavoritesFragmentGuest
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragment
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragmentGuest

class LoginFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Check if the user is logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            val username = sharedPreferences.getString("username", "") ?: ""
            val email = sharedPreferences.getString("email", "") ?: ""
            updateNavigationDrawer(username, email)
            changePagesToLoggedIn()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = "$username@powerpantry.com" // Simulated email format

            if (username.isNotEmpty() && etPassword.text.isNotEmpty()) {
                // Save login data
                sharedPreferences.edit().apply {
                    putString("username", username)
                    putString("email", email)
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                // Update Navigation Drawer UI
                updateNavigationDrawer(username, email)

                // Navigate back to home
                parentFragmentManager.popBackStack()

                // Change the pages to user-specific versions
                changePagesToLoggedIn()
            }
        }

        return view
    }

    private fun updateNavigationDrawer(username: String, email: String) {
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        val profileImage = headerView.findViewById<ImageView>(R.id.nav_profile_image)
        val tvUsername = headerView.findViewById<TextView>(R.id.nav_username)
        val tvEmail = headerView.findViewById<TextView>(R.id.nav_email)
        val btnLogout = headerView.findViewById<Button>(R.id.btnLogout)

        // Update UI
        profileImage.setImageResource(R.drawable.ic_github) // Change to logged-in profile
        tvUsername.text = username
        tvEmail.text = email

        // Show Logout Button
        btnLogout.visibility = View.VISIBLE
        btnLogout.setOnClickListener {
            logout()
        }

        // Hide Login Button from menu
        val menu = navView.menu
        val loginItem = menu.findItem(R.id.nav_login)
        loginItem.isVisible = false // Hide login button when logged in
    }

    private fun logout() {
        // Clear saved data
        sharedPreferences.edit().clear().apply()

        // Reset Navigation Drawer to Guest Mode
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        val profileImage = headerView.findViewById<ImageView>(R.id.nav_profile_image)
        val tvUsername = headerView.findViewById<TextView>(R.id.nav_username)
        val tvEmail = headerView.findViewById<TextView>(R.id.nav_email)
        val btnLogout = headerView.findViewById<Button>(R.id.btnLogout)

        profileImage.setImageResource(R.drawable.ic_guest) // Reset to guest icon
        tvUsername.text = getString(R.string.navBar_GuestTitle)
        tvEmail.text = getString(R.string.navBar_GuestMsg)

        btnLogout.visibility = View.GONE // Hide logout button

        // Revert Pages to Guest Versions
        changePagesToGuest()

        // Show the login item in the menu
        val menu = navView.menu
        val loginItem = menu.findItem(R.id.nav_login)
        loginItem.isVisible = true // Show login button when logged out
    }

    private fun changePagesToLoggedIn() {
        // Replace fragments with user-specific versions
        val transaction = parentFragmentManager.beginTransaction()

        // Replacing FavoritesFragment with the logged-in version
        transaction.replace(R.id.fragment_container, FavoritesFragment())

        // Replacing SubmitFragment with the logged-in version
        transaction.replace(R.id.fragment_container, SubmitFragment())

        // Opens the HomeFragment after replacing the other fragments
        //transaction.replace(R.id.fragment_container, HomeFragment())

        transaction.addToBackStack(null) // Optional, if you want to keep the back stack
        transaction.commit()
        
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        navView.setCheckedItem(R.id.nav_home)  // Set Home item as selected
    }

    private fun changePagesToGuest() {
        // Replace fragments with guest versions
        val transaction = parentFragmentManager.beginTransaction()

        // Replacing FavoritesFragment with the guest version
        transaction.replace(R.id.fragment_container, FavoritesFragmentGuest())

        // Replacing SubmitFragment with the guest version
        transaction.replace(R.id.fragment_container, SubmitFragmentGuest())

        // Opens the HomeFragment after replacing the other fragments
        //transaction.replace(R.id.fragment_container, HomeFragment())

        transaction.addToBackStack(null) // Optional, if you want to keep the back stack
        transaction.commit()

        // Update NavigationView to highlight the Home item
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        navView.setCheckedItem(R.id.nav_home)  // Ensure Home is selected
    }

}
