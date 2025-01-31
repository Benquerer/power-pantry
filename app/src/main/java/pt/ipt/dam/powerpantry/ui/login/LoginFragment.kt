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
import org.mindrot.jbcrypt.BCrypt
import pt.ipt.dam.powerpantry.MainActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.User

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

        //login logic
        btnLogin.setOnClickListener {
            //get info on inputs
            val username = etUsername.text.toString().trim()
            val passwd = etPassword.text.toString()
            if (!username.isNullOrEmpty() && !passwd.isNullOrEmpty()) {
                loginUser(username,
                    passwd, onResult = {result ->
                        if(result){
                            //set shared preferences
                            sharedPreferences.edit().apply {
                                putString("username", username)
                                putBoolean("isLoggedIn", true)
                                apply()
                            }
                            //update app UI
                            (activity as? MainActivity)?.apply {
                                updateNavHeader()
                                updateUserFragments()
                            }

                            parentFragmentManager.popBackStack()

                            //login done
                            Toast.makeText(requireContext(), "LOGIN DONE", Toast.LENGTH_SHORT).show()
                        }else{
                            //wrong password
                            Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = {errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }else{
                Toast.makeText(requireContext(), "Make sure all fields are filled", Toast.LENGTH_SHORT).show()
            }
        }

        btnOpenRegister.setOnClickListener{
            //init sheet
            val registerSheet = BottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.register_sheet,null)
            registerSheet.setContentView(sheetView)

            //get register button
            val btnRegister = sheetView.findViewById<Button>(R.id.btnRegister)

            //registration logic
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
                        Toast.makeText(requireContext(), "The passwords don't match", Toast.LENGTH_SHORT).show()
                    }else{
                        //disable button
                        btnRegister.isEnabled = false
                        btnRegister.text = "Registering..."
                        //check if user already exists
                        checkUsernameAvailable(username) { isAvailable ->
                            if (isAvailable) {
                                //register
                                registerUser(username,email,passwd) {result ->
                                    if(result){
                                        registerSheet.dismiss()
                                        Toast.makeText(requireContext(), "Registration completed", Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(requireContext(), "ERROR IN CREATION API SIDE", Toast.LENGTH_LONG).show()
                                    }

                                }
                            } else {
                                Toast.makeText(requireContext(), "Username is already taken! Please pick another one", Toast.LENGTH_SHORT).show()
                                //restore button
                                btnRegister.isEnabled = true
                                btnRegister.text = "Register Now"
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

    fun checkUsernameAvailable(username: String, onResult: (Boolean) -> Unit){
        DataRepository.checkUserExists(
            username,
            onResult = { exists ->
                onResult(!exists)
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                onResult(false)
            }
        )
    }


    fun registerUser(username : String, password : String, email: String, onResult: (Boolean) -> Unit){
        onResult(true)
        //hash password
        val hashedPasswd = BCrypt.hashpw(password,BCrypt.gensalt(5))
        //create user
        val newUser = User(userName = username, eMail = email, passWord = hashedPasswd)
        //post user
    }

    fun loginUser(username : String, password : String, onResult: (Boolean) -> Unit, onError:(String) -> Unit){
        //get user
        DataRepository.getUser(username,
            //if user exists
            onResult = {user->
                //check passwd match
                if(password.matches(user.passWord.toRegex())){
                    onResult(true)
                }else{
                    onResult(false)
                }
            }, onError = {errorMessage ->
                //error getting user or user dont exist
                onError(errorMessage)
            }
        )

    }

}

