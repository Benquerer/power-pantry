package pt.ipt.dam.powerpantry

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam.powerpantry.ui.about.AboutFragment
import pt.ipt.dam.powerpantry.ui.favorites.FavoritesFragment
import pt.ipt.dam.powerpantry.ui.favorites.FavoritesFragmentGuest
import pt.ipt.dam.powerpantry.ui.gallery.GalleryFragment
import pt.ipt.dam.powerpantry.ui.login.LoginFragment
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragment
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragmentGuest

/**
 * MainActivity for the app.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    /**
     * DrawerLayout for the navigation drawer
     */
    private lateinit var drawerLayout: DrawerLayout

    /**
     * SharedPreferences for storing user info
     */
    private lateinit var userPreferences: SharedPreferences

    /**
     * NavigationView for the navigation drawer
     */
    lateinit var navigationView: NavigationView

    /**
     * Called when the activity is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set layout as main
        setContentView(R.layout.activity_main)
        //get toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        //set toolbar as support action bar
        setSupportActionBar(toolbar)

        //get references to views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        //set this for navigation
        navigationView.setNavigationItemSelectedListener(this)

        //get user preferences
        userPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        //set up toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_menu, R.string.close_menu
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //correct UI based on user preferences
        updateNavHeader()
        updateUserFragments()

        //set navigation view to home fragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param item MenuItem - The selected item
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //handle navigation
        //open fragment based on selected item
        when (item.itemId) {
            R.id.nav_login -> replaceFragment(LoginFragment())
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_gallery -> replaceFragment(GalleryFragment())
            R.id.nav_favorites -> {
                //get login status
                val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
                //load favorites fragment based on login status
                replaceFragment(if (isLoggedIn) FavoritesFragment() else FavoritesFragmentGuest())
            }
            R.id.nav_submit -> {
                //get login status
                val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
                //load submit fragment based on login status
                replaceFragment(if (isLoggedIn) SubmitFragment() else SubmitFragmentGuest())
            }
            R.id.nav_about -> replaceFragment(AboutFragment())
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Replace the current fragment with a new one.
     *
     * @param fragment Fragment - The fragment to replace the current one with
     */
    fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Update the navigation header based on user preferences.
     */
    fun updateNavHeader() {
        //get login status
        val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
        //get username
        val username = userPreferences.getString("username", "User")

        //get references
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_username)
        val headerMessageView = headerView.findViewById<TextView>(R.id.nav_headerMessage)
        val btnLogout = headerView.findViewById<Button>(R.id.btnLogout)
        val menu = navigationView.menu
        val loginMenuItem = menu.findItem(R.id.nav_login)

        //update UI based on login status
        userNameTextView.text = if (isLoggedIn) username else getString(R.string.navBar_GuestTitle)
        headerMessageView.text = if (isLoggedIn) getString(R.string.navBar_WelcomeMsg,username) else getString(R.string.navBar_GuestMsg)
        loginMenuItem.isVisible = !isLoggedIn
        btnLogout.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        //set logout listener
        btnLogout.setOnClickListener { logoutUser() }
    }

    /**
     * Update the user fragments based on user preferences.
     */
    fun updateUserFragments() {
        //get login status
        val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)

        //load fragments based on login status
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.favorites_container, if (isLoggedIn) FavoritesFragment() else FavoritesFragmentGuest())
            replace(R.id.submit_container, if (isLoggedIn) SubmitFragment() else SubmitFragmentGuest())
            replace(R.id.fragment_container, HomeFragment())
            commit()
        }
        navigationView.setCheckedItem(R.id.nav_home)
    }

    /**
     * Logout the user and update the UI.
     */
    fun logoutUser() {
        //clear login preferences
        userPreferences.edit().clear().apply()
        //update ui
        updateNavHeader()
        updateUserFragments()
        //go to home
        navigationView.setCheckedItem(R.id.nav_home)
    }

    /**
     * Handle the back button press.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
