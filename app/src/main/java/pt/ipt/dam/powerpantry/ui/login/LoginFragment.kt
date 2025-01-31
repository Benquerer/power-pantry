package pt.ipt.dam.powerpantry.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import pt.ipt.dam.powerpantry.MainActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository

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
        val btnOpenRegister = view.findViewById<TextView>(R.id.tvOpenRegister)

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

        btnOpenRegister.setOnClickListener{
            //init sheet
            val registerSheet = BottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.register_sheet,null)
            registerSheet.setContentView(sheetView)

            //get register button
            val btnRegister = sheetView.findViewById<Button>(R.id.btnRegister)

            btnRegister.setOnClickListener{
                val username = sheetView.findViewById<EditText>(R.id.etRegisterUsername)?.text.toString()
                val email = sheetView.findViewById<EditText>(R.id.etRegisterEmail)?.text.toString()
                val passwd = sheetView.findViewById<EditText>(R.id.etRegisterPassword)?.text.toString()
                val passwdConfirm = sheetView.findViewById<EditText>(R.id.etRegisterConfirmPassword)?.text.toString()

                //check fields
                if(username.isNullOrEmpty() || email.isNullOrEmpty() || passwd.isNullOrEmpty() || passwdConfirm.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Make sure all fields are filled", Toast.LENGTH_SHORT).show()
                }else{
                    //check pass match
                    if(!passwd.matches(passwdConfirm.toRegex())){
                        Toast.makeText(requireContext(), "Password confirmation failed: Make sure the passwords are the same", Toast.LENGTH_LONG).show()
                    }else{
                        //check if user already exists
                        checkUsernameAvailable(username) { isAvailable ->
                            if (isAvailable) {
                                registerUser("Registrando user")
                                //clear inputs
                                sheetView.findViewById<EditText>(R.id.etRegisterUsername).setText("")
                                sheetView.findViewById<EditText>(R.id.etRegisterEmail).setText("")
                                sheetView.findViewById<EditText>(R.id.etRegisterPassword).setText("")
                                sheetView.findViewById<EditText>(R.id.etRegisterConfirmPassword).setText("")

                                //////// CONTINUAR AQUI AMANHA


                            } else {
                                Toast.makeText(requireContext(), "Username is already taken! Please pick another one", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }


            }

            //show sheet
            registerSheet.show()
        }
        return view
    }

    fun checkUsernameAvailable(username: String, onResult: (Boolean) -> Unit) {
        DataRepository.checkUserExists(
            username,
            onResult = { exists ->
                onResult(!exists) // If user exists (`true`), then username is NOT available (`false`)
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                onResult(false) // Assume unavailable in case of error
            }
        )
    }


    fun registerUser(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}

