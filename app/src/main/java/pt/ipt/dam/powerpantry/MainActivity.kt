package pt.ipt.dam.powerpantry

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam.powerpantry.ui.about.AboutFragment
import pt.ipt.dam.powerpantry.ui.favorites.FavoritesFragment
import pt.ipt.dam.powerpantry.ui.gallery.GalleryFragment
import pt.ipt.dam.powerpantry.ui.home.HomeFragment
import pt.ipt.dam.powerpantry.ui.login.LoginFragment
import pt.ipt.dam.powerpantry.ui.submit.SubmitFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_menu, R.string.close_menu
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
