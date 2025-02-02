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

/**
 * Fragment for the "About the APP" page
 */
class AboutFragment : Fragment() {

    /**
     * Method for creating the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate the layout and get root view using about layout
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        //set onClick listener on application logo
        val appLogo = view.findViewById<ImageView>(R.id.app_logo)
        appLogo.setOnClickListener {
            //a good video on youtube (demonstration purposes)
            openUrl("https://www.youtube.com/watch?v=GJDNkVDGM_s&ab_channel=HighValley")
        }

        //set onClick listener for each icon in the view
        //each icon opens the intended url
        val instagramIcon = view.findViewById<ImageView>(R.id.instagram_icon)
        instagramIcon.setOnClickListener {
            //just instagram home (demonstration purposes)
            openUrl("https://www.instagram.com/")
        }
        val gitIcon = view.findViewById<ImageView>(R.id.github_icon)
        gitIcon.setOnClickListener {
            //actual project repository
            openUrl("https://github.com/Benquerer/power-pantry")
        }
        val youtubeIcon = view.findViewById<ImageView>(R.id.youtube_icon)
        youtubeIcon.setOnClickListener {
            //a good video on youtube (demonstration purposes)
            openUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley")
        }
        return view
    }

    /**
     * Method for opening a url in phone's browser
     *
     * @param url String - URL to open
     */
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}