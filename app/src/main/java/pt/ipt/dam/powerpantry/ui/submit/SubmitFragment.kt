package pt.ipt.dam.powerpantry.ui.submit

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.Product
import java.net.URL

/**
 * Fragment for the logged in version of submit screen.
 */
class SubmitFragment : Fragment() {

    /**
     * SharedPreferences for storing user info
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_logged_in, container, false)

        //get reference to spinner
        val spinner = view.findViewById<Spinner>(R.id.spLoggedCategory)
        //populate categories
        val categories: List<String> = listOf("Proteína", "Snack", "Creatina", "Pré-Treino", "Outros")
        //adapter with custom colors
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ) {
            //override getview
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                return view
            }
            //override dropdown
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.my_light_grey
                    )
                )
                textView.textSize = 18f
                return view
            }
        }
        //set adapter on spinner
        spinner.adapter = adapter
        //initialize selectedCategoru (starts as "Outros" in case of problems)
        var selectedCategory = "Outros"
        //add listener to selection on dropdown
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set selected category as item in position selected
                selectedCategory = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        //setup submit button
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitProduct)
        btnSubmit.setOnClickListener {
            //disable button for verifications
            btnSubmit.isEnabled = false
            btnSubmit.setText(R.string.submitting)

            //get all information (already have category)
            val productName = view.findViewById<EditText>(R.id.etLoggedName)?.text.toString().trim()
            val productBrand =
                view.findViewById<EditText>(R.id.etLoggedBrand)?.text.toString().trim()
            val productBarcode =
                view.findViewById<EditText>(R.id.etLoggedBarcode)?.text.toString().trim()
            val productPrice =
                view.findViewById<EditText>(R.id.etLoggedPrice)?.text.toString().trim()
            val productDescription =
                view.findViewById<EditText>(R.id.etLoggedDescription)?.text.toString()
            val productImage =
                view.findViewById<EditText>(R.id.etLoggedImage)?.text.toString().trim()
            //verify info - cases that fail return clickListener
            //check any empty fields
            if (selectedCategory.isNullOrEmpty() || productName.isNullOrEmpty() || productBrand.isNullOrEmpty() || productBarcode.isNullOrEmpty() || productPrice.isNullOrEmpty() || productDescription.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.filled_camps,
                    Toast.LENGTH_SHORT
                ).show()
                //restore button
                btnSubmit.isEnabled = true
                btnSubmit.setText(R.string.submit_logged_submit)
                return@setOnClickListener
            }
            //check name (limit at 50 chars)
            if (productName.length > 20) {
                Toast.makeText(
                    requireContext(),
                    R.string.wrong_product_name,
                    Toast.LENGTH_SHORT
                ).show()
                btnSubmit.isEnabled = true
                btnSubmit.setText(R.string.submit_logged_submit)
                return@setOnClickListener
            }
            //check brand (limit at 50 chars)
            if (productBrand.length > 20) {
                Toast.makeText(
                    requireContext(),
                    R.string.wrong_product_brand,
                    Toast.LENGTH_SHORT
                ).show()
                btnSubmit.isEnabled = true
                btnSubmit.setText(R.string.submit_logged_submit)
                return@setOnClickListener
            }

            //check price (limit at 50.000,00)
            val priceValue = productPrice.toDoubleOrNull()
            if (priceValue == null || priceValue <= 0 || priceValue > 50000.00) {
                Toast.makeText(
                    requireContext(),
                    R.string.wrong_price,
                    Toast.LENGTH_SHORT
                ).show()
                //restore button
                btnSubmit.isEnabled = true
                btnSubmit.setText(R.string.submit_logged_submit)
                return@setOnClickListener
            }
            //check url (allows no image)
            if (productImage.isNotEmpty() && !isUrlValid(productImage)) {
                Toast.makeText(requireContext(), R.string.wrong_image, Toast.LENGTH_SHORT)
                    .show()
                //restore button
                btnSubmit.isEnabled = true
                btnSubmit.setText(R.string.submit_logged_submit)
                return@setOnClickListener
            }


            //build product
            val newProduct = Product(
                productCode = productBarcode.toLong(),
                productName = productName,
                productBrand = productBrand,
                productCategory = selectedCategory,
                productDescription = productDescription,
                productPrice = priceValue,
                productImage = productImage
            )
            //post product & clear input
            DataRepository.createProduct(newProduct) { success ->
                //on success
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        R.string.submit_sucess_message,
                        Toast.LENGTH_SHORT
                    ).show()
                    //clear views
                    listOf(
                        R.id.etLoggedName,
                        R.id.etLoggedBrand,
                        R.id.etLoggedBarcode,
                        R.id.etLoggedName,
                        R.id.etLoggedPrice,
                        R.id.etLoggedImage,
                        R.id.etLoggedDescription
                    ).forEach { id -> view.findViewById<EditText>(id).setText("") }
                    //restore button
                    btnSubmit.isEnabled = true
                    btnSubmit.setText(R.string.submit_logged_submit)
                } else { //onerror
                    //restore button
                    btnSubmit.isEnabled = true
                    btnSubmit.setText(R.string.submit_logged_submit)
                    Toast.makeText(
                        requireContext(),
                        R.string.submit_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        return view
    }

    /**
     * Method for checking if a URL is correctly formatted. The url could be broken.
     *
     * @param url String - The URL to check
     */
    private fun isUrlValid(url: String): Boolean {
        return try {
            //throws ex if fail
            URL(url).toURI()
            // no ex means true
            true
        } catch (e: Exception) {
            //if ex was thrown means false
            false
        }
    }
}
