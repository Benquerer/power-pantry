package pt.ipt.dam.powerpantry.ui.submit

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.core.view.marginTop
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.DataRepository
import pt.ipt.dam.powerpantry.data.Product

class SubmitFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_logged_in, container, false)

        //setup spinner and selection event
        val spinner = view.findViewById<Spinner>(R.id.spLoggedCategory)
        val categories: List<String> = listOf("Proteína","Snack","Creatina","Pré-Treino","Outros")
        //adapter with custom colors
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, categories) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_light_grey))
                textView.textSize = 18f
                return view
            }
        }
        spinner.adapter = adapter
        var selectedCategory = "Outros"
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        //setup submit button
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitProduct)
        btnSubmit.setOnClickListener{
            //get all information (already have category)
            val productName = view.findViewById<EditText>(R.id.etLoggedName)?.text.toString().trim()
            val productBrand = view.findViewById<EditText>(R.id.etLoggedBrand)?.text.toString().trim()
            val productBarcode = view.findViewById<EditText>(R.id.etLoggedBarcode)?.text.toString().trim()
            val productPrice = view.findViewById<EditText>(R.id.etLoggedPrice)?.text.toString().trim()
            val productDescription = view.findViewById<EditText>(R.id.etLoggedDescription)?.text.toString()
            val productImage = view.findViewById<EditText>(R.id.etLoggedImage)?.text.toString()
            //verify info
            if(selectedCategory.isNullOrEmpty() || productName.isNullOrEmpty() || productBrand.isNullOrEmpty() || productBarcode.isNullOrEmpty()  || productPrice.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Por favor, preenche todos os campos", Toast.LENGTH_SHORT).show()
            }else{
                //build product
                val newProduct = Product(
                    productCode = productBarcode.toLong(),
                    productName = productName,
                    productBrand = productBrand,
                    productCategory = selectedCategory,
                    productDescription = productDescription,
                    productPrice = productPrice.toDouble(),
                    productImage = productImage)
                //post product & clear input
                DataRepository.createProduct(newProduct){success ->
                    if(success){
                        //clear views
                        Toast.makeText(requireContext(), "Sucesso!! Obrigado por contribuires para a base de dados!", Toast.LENGTH_SHORT).show()
                        view.findViewById<EditText>(R.id.etLoggedName).setText("")
                        view.findViewById<EditText>(R.id.etLoggedBrand).setText("")
                        view.findViewById<EditText>(R.id.etLoggedBarcode).setText("")
                        view.findViewById<EditText>(R.id.etLoggedName).setText("")
                        view.findViewById<EditText>(R.id.etLoggedPrice).setText("")
                        view.findViewById<EditText>(R.id.etLoggedImage).setText("")
                    }else{
                        Toast.makeText(requireContext(), "Ocorreu um Erro!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        return view
    }
}
