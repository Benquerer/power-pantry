package pt.ipt.dam.powerpantry.ui.submit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.R

class SubmitFragmentGuest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_guest, container, false)

        // Placeholder content for guest users
        val tvSubmission = view.findViewById<TextView>(R.id.tvSubmission)
        tvSubmission.text = "Please log in to submit your items!"

        return view
    }
}
