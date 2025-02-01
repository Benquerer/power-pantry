package pt.ipt.dam.powerpantry.ui.submit

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import pt.ipt.dam.powerpantry.R

class SubmitFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_logged_in, container, false)

        //setup spinner and selection event
        val spinner = view.findViewById<Spinner>(R.id.spLoggedCategory)
        val categories: List<String> = listOf("Proteina","Snack","Creatina","Pre-Treino")
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
        var selectedCategory : String
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }




        return view
    }
}
