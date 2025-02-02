package pt.ipt.dam.powerpantry.ui.submit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.MainActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.ui.login.LoginFragment

/**
 * Fragment for the guest version of submit screen.
 */
class SubmitFragmentGuest : Fragment() {

    /**
     * Called when the fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_guest, container, false)
        //get reference to "go to login"button
        val btnLogin: Button = view.findViewById(R.id.btnLoginSubmit)
        //set listener to handle navigation
        btnLogin.setOnClickListener {
            //navigate to login screen
            (activity as? MainActivity)?.apply {
                replaceFragment(LoginFragment())
                navigationView.setCheckedItem(R.id.nav_login)
            }
        }
        return view
    }
}
