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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userPreferences: SharedPreferences
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //get user preferences
        userPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_menu, R.string.close_menu
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //user correct UI based on sharedPreferences
        updateNavHeader()
        updateUserFragments()

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> replaceFragment(LoginFragment())
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_gallery -> replaceFragment(GalleryFragment())
            R.id.nav_favorites -> {
                val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
                replaceFragment(if (isLoggedIn) FavoritesFragment() else FavoritesFragmentGuest())
            }
            R.id.nav_submit -> {
                val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
                replaceFragment(if (isLoggedIn) SubmitFragment() else SubmitFragmentGuest())
            }
            R.id.nav_about -> replaceFragment(AboutFragment())
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    fun updateNavHeader() {
        val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)
        val username = userPreferences.getString("username", "User")

        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_username)
        val headerMessageView = headerView.findViewById<TextView>(R.id.nav_headerMessage)
        val btnLogout = headerView.findViewById<Button>(R.id.btnLogout)

        val menu = navigationView.menu
        val loginMenuItem = menu.findItem(R.id.nav_login)

        userNameTextView.text = if (isLoggedIn) username else getString(R.string.navBar_GuestTitle)
        headerMessageView.text = if (isLoggedIn) getString(R.string.navBar_WelcomeMsg,username) else getString(R.string.navBar_GuestMsg)
        loginMenuItem.isVisible = !isLoggedIn
        btnLogout.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

        btnLogout.setOnClickListener { logoutUser() }
    }


    fun updateUserFragments() {
        val isLoggedIn = userPreferences.getBoolean("isLoggedIn", false)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.favorites_container, if (isLoggedIn) FavoritesFragment() else FavoritesFragmentGuest())
            replace(R.id.submit_container, if (isLoggedIn) SubmitFragment() else SubmitFragmentGuest())
            replace(R.id.fragment_container, HomeFragment())
            commit()
        }
        navigationView.setCheckedItem(R.id.nav_home)
    }

    fun logoutUser() {
        userPreferences.edit().clear().apply()
        updateNavHeader()
        updateUserFragments()
        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
