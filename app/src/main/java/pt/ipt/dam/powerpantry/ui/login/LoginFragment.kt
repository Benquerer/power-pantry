package pt.ipt.dam.powerpantry.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam.powerpantry.MainActivity
import pt.ipt.dam.powerpantry.R

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

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = "$username@powerpantry.com"

            if (username.isNotEmpty() && etPassword.text.isNotEmpty()) {
                sharedPreferences.edit().apply {
                    putString("username", username)
                    putString("email", email)
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                (activity as? MainActivity)?.apply {
                    updateNavHeader()
                    updateUserFragments()
                }

                parentFragmentManager.popBackStack()
            }
        }

        return view
    }
}

