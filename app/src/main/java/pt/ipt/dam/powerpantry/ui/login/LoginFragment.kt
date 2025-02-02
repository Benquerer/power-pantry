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

/**
 * Fragment for the login screen. Handles Login and Registration
 */
class LoginFragment : Fragment() {

    /**
     * SharedPreferences for storing user info
     */
    private lateinit var userPreferences: SharedPreferences

    /**
     * Called when the fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //get views
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnOpenRegister = view.findViewById<TextView>(R.id.tvOpenRegister)

        //get shared preferences
        userPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        //set up login logic in button onclick
        btnLogin.setOnClickListener {
            //get info on inputs
            val username = etUsername.text.toString().trim()
            val passwd = etPassword.text.toString()
            //check if inputs are valid
            if (!username.isNullOrEmpty() && !passwd.isNullOrEmpty()) {
                //disable button and change text
                btnLogin.isEnabled = false
                btnLogin.setText(R.string.logging_in_text)
                //Call LoginUser method from API
                loginUser(username,passwd,
                    //handle onResult
                    onResult = {result ->
                        if(result){
                            //enable button back and change back text
                            btnLogin.isEnabled = true
                            btnLogin.setText(R.string.logged_in_sucess)
                            //set shared preferences
                            userPreferences.edit().apply {
                                //input preferences for logged in user
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
                            Toast.makeText(requireContext(), R.string.login_done, Toast.LENGTH_SHORT).show()
                        }else{
                            //enable button back and change back text
                            btnLogin.isEnabled = true
                            btnLogin.setText(R.string.login_button_text)
                            //wrong password
                            Toast.makeText(requireContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show()
                        }
                    },
                    //handle error
                    onError = {errorMessage ->
                        //enable button back and change back text
                        btnLogin.isEnabled = true
                        btnLogin.setText(R.string.login_button_text)
                        //toast error
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }else{
                //notify user to check inputs
                Toast.makeText(requireContext(), R.string.filled_camps, Toast.LENGTH_SHORT).show()
            }
        }

        //set up register logic in button onclick
        btnOpenRegister.setOnClickListener{
            //initialize sheet
            val registerSheet = BottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.register_sheet,null)
            registerSheet.setContentView(sheetView)

            //get register button
            val btnRegister = sheetView.findViewById<Button>(R.id.btnRegister)

            //registration logic
            btnRegister.setOnClickListener{
                //get references to input fields
                val username = sheetView.findViewById<EditText>(R.id.etRegisterUsername)?.text.toString()
                val email = sheetView.findViewById<EditText>(R.id.etRegisterEmail)?.text.toString()
                val passwd = sheetView.findViewById<EditText>(R.id.etRegisterPassword)?.text.toString()
                val passwdConfirm = sheetView.findViewById<EditText>(R.id.etRegisterConfirmPassword)?.text.toString()

                //check fields
                if(username.isNullOrEmpty() || email.isNullOrEmpty() || passwd.isNullOrEmpty() || passwdConfirm.isNullOrEmpty()){
                    Toast.makeText(requireContext(), R.string.filled_camps, Toast.LENGTH_SHORT).show()
                }else{
                    //check pass match
                    if(!passwd.matches(passwdConfirm.toRegex())){
                        Toast.makeText(requireContext(), R.string.no_equal_password, Toast.LENGTH_SHORT).show()
                    }else{
                        //disable button and change text
                        btnRegister.isEnabled = false
                        btnRegister.setText(R.string.registering_text)
                        //check if user already exists
                        checkUsernameAvailable(username) { isAvailable ->
                            if (isAvailable) {
                                //register
                                registerUser(username,email,passwd) {result ->
                                    if(result){
                                        //close sheet and toast user
                                        registerSheet.dismiss()
                                        Toast.makeText(requireContext(), R.string.register_complete , Toast.LENGTH_LONG).show()
                                    }else{
                                        //toast user
                                        //restore button
                                        btnRegister.isEnabled = true
                                        btnRegister.setText(R.string.register_button)
                                        Toast.makeText(requireContext(), R.string.error_account_created, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), R.string.user_already_chosen , Toast.LENGTH_SHORT).show()
                                //restore button
                                btnRegister.isEnabled = true
                                btnRegister.setText(R.string.register_button)
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

    /**
     * Method for checking if a username is available
     *
     * @param username String - The username to check
     * @param onResult (Boolean) -> Unit - A function to call with the result
     */
    private fun checkUsernameAvailable(username: String, onResult: (Boolean) -> Unit){
        DataRepository.checkUserExists(
            username,
            //handle onResult
            onResult = { exists ->
                //invert result and callback onresult
                onResult(!exists)
            },
            //handle error
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                //callback false in case of errors
                onResult(false)
            }
        )
    }

    /**
     * Method for registering a new user
     *
     * @param username String - The username of the new user
     * @param email String - The email of the new user
     * @param password String - The password of the new user
     * @param onResult (Boolean) -> Unit - A function to call with the result
     */
    private fun registerUser(username : String, email : String, password: String, onResult: (Boolean) -> Unit){
        //hash password
        val hashedPasswd = BCrypt.hashpw(password,BCrypt.gensalt(8))
        //create user
        val newUser = User(userName = username, eMail = email, passWord = hashedPasswd)
        //post user
        DataRepository.createUser(newUser) { result ->
            //callback result
            onResult(result)
        }
    }

    /**
     * Method for logging in a user
     *
     * @param username String - The username of the user
     * @param password String - The password of the user
     * @param onResult (Boolean) -> Unit - A function to call with the result
     * @param onError (String) -> Unit - A function to call with the error message
     */
    private fun loginUser(username : String, password : String, onResult: (Boolean) -> Unit, onError:(String) -> Unit){
        //get user
        DataRepository.getUser(username,
            //if user exists
            onResult = {user->
                //check passwd match
                onResult(BCrypt.checkpw(password,user.passWord))
            }, onError = {errorMessage ->
                //error getting user or user don't exist
                onError(errorMessage)
            }
        )

    }

}

