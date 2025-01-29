package pt.ipt.dam.powerpantry.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import pt.ipt.dam.powerpantry.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Set OnClickListener for the app logo
        val appLogo = view.findViewById<ImageView>(R.id.app_logo)
        appLogo.setOnClickListener {
            // Example: Open your app's website when the logo is clicked
            openUrl("https://www.youtube.com/watch?v=GJDNkVDGM_s&ab_channel=HighValley")
        }

        // Set OnClickListener for the social media icons
        val facebookIcon = view.findViewById<ImageView>(R.id.instagram_icon)
        facebookIcon.setOnClickListener {
            openUrl("https://www.instagram.com/")
        }

        val twitterIcon = view.findViewById<ImageView>(R.id.github_icon)
        twitterIcon.setOnClickListener {
            openUrl("https://github.com/Benquerer/power-pantry")
        }

        val instagramIcon = view.findViewById<ImageView>(R.id.youtube_icon)
        instagramIcon.setOnClickListener {
            openUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley")
        }

        return view
    }

    // Function to open a URL in the browser
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}