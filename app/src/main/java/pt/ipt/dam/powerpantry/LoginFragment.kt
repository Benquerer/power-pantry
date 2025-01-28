package pt.ipt.dam.powerpantry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val emailInput = view.findViewById<EditText>(R.id.emailInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val registerButton = view.findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            registerUser(username, email, password)
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            loginUser(username, password)
        }

        return view
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Create a list of AuthUserAttribute for the email
        val userAttributes = mutableListOf<AuthUserAttribute>(
            AuthUserAttribute(AuthUserAttributeKey.EMAIL, email)  // Using "email" as key
        )

        // Create AuthSignUpOptions with the user attributes
        val options = AuthSignUpOptions.builder()
            .userAttributes(userAttributes)
            .build()

        // Call the Amplify signUp method
        Amplify.Auth.signUp(
            username,
            password,
            options,
            { result: AuthSignUpResult ->
                // Handle success
                Toast.makeText(context, "Registration successful! Confirm your email.", Toast.LENGTH_LONG).show()
            },
            { error: AuthException ->
                // Handle error
                Toast.makeText(context, "Registration failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun loginUser(username: String, password: String) {
        Amplify.Auth.signIn(
            username,
            password,
            {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                }
            },
            { error: AuthException ->
                activity?.runOnUiThread {
                    Toast.makeText(context, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}
