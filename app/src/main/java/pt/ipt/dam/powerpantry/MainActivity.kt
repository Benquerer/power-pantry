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
import pt.ipt.dam.powerpantry.ui.gallery.GalleryFragment
import pt.ipt.dam.powerpantry.ui.login.LoginFragment
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_menu, R.string.close_menu
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ✅ Update UI based on login state
        updateNavHeader()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
            R.id.nav_gallery -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, GalleryFragment())
                    .commit()
            }
            R.id.nav_favorites -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FavoritesFragment())
                    .commit()
            }
            R.id.nav_submit -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SubmitFragment())
                    .commit()
            }
            R.id.nav_about -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AboutFragment())
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // ✅ Update UI based on login/logout
    private fun updateNavHeader() {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_username)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.nav_email)
        val btnLogout = headerView.findViewById<Button>(R.id.btnLogout)

        val menu = navigationView.menu
        val loginMenuItem = menu.findItem(R.id.nav_login)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            userNameTextView.text = sharedPreferences.getString("username", "User")
            userEmailTextView.text = sharedPreferences.getString("email", "user@example.com")

            // ✅ Hide login, show logout
            loginMenuItem.isVisible = false
            btnLogout.visibility = View.VISIBLE
        } else {
            userNameTextView.text = getString(R.string.navBar_GuestTitle)
            userEmailTextView.text = getString(R.string.navBar_GuestMsg)

            // ✅ Show login, hide logout
            loginMenuItem.isVisible = true
            btnLogout.visibility = View.GONE
        }

        // ✅ Logout Button Click Listener
        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Refresh UI
        updateNavHeader()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
