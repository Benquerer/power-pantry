package pt.ipt.dam.powerpantry.ui.favorites

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Helper class for handling favorites in shared preferences
 */
class FavPrefHelper(context: Context) {

    /**
     * Shared preferences for storing favorites
     */
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Gson for converting objects to JSON
     */
    private val gson: Gson = Gson()

    /**
     * Companion object for constants
     */
    companion object {
        /**
         * Name of the shared preferences file
         */
        private const val PREF_NAME = "FavPreferences"

        /**
         * Key for the map in shared preferences
         */
        private const val MAP_KEY = "fav_map"
    }

    /**
     * Method for saving a user's favorites
     *
     * @param username String -  The username of the user
     * @return Set<Long> - The set of favorites for the user
     */
    private fun saveUserSet(username: String, set: Set<Long>) {
        //get user map
        val currentMap = getMap()
        //create updatable copy of current map
        val updatedMap = currentMap.toMutableMap()
        //update map
        updatedMap[username] = set
        //parse map to json
        val json = gson.toJson(updatedMap)
        //save in user preferences
        sharedPreferences.edit().putString(MAP_KEY, json).apply()
        //log
        Log.d("PowerPantry_FavHelper", "Saved favorites for $username: $set")
    }

    /**
     * Method for getting a user's favorites
     *
     * @param username String - The username of the user
     * @return Set<Long> - The set of favorites for the user
     */
    private fun getUserSet(username: String): Set<Long> {
        //get user map
        val currentMap = getMap()
        //get user favorites
        val userFavorites = currentMap[username] ?: emptyList()
        //turn to set and return
        return userFavorites.toSet()
    }

    /**
     * Method for getting the map of users and their favorites
     *
     * @return Map<String, Set<Long>> - The map of users and their favorites
     */
    private fun getMap(): Map<String, Set<Long>> {
        //get json string from shared preferences
        val json = sharedPreferences.getString(MAP_KEY, null)
        return if (json != null) {
            //process json to map
            val type = object : TypeToken<Map<String, Set<Long>>>() {}.type
            gson.fromJson(json, type)
        } else {
            //return map if no json found
            emptyMap()
        }
    }

    /**
     * Method for appending a value to a user's favorites
     *
     * @param username String - The username of the user
     * @param value Long - The value to append
     */
    fun appendUserFavorite(username: String, value: Long) {
        //get favorites set
        val currentSet = getUserSet(username).toMutableSet()
        //try to add value
        if (currentSet.add(value)) {
            //save set if successful
            saveUserSet(username, currentSet)
            Log.d("PowerPantry_FavHelper", "Added $value to $username favorites.")
        } else {
            //log value already in set
            Log.d("PowerPantry_FavHelper", "$value is already in $username favorites. Skipping.")
        }
    }

    /**
     * Method for removing a value from a user's favorites
     *
     * @param username String - The username of the user
     * @param value Long - The value to remove
     */
    fun removeUserFavorite(username: String, value: Long) {
        //get favorites set
        val currentSet = getUserSet(username).toMutableSet()
        //try to remove the value
        if (currentSet.remove(value)) {
            //save set if successful
            saveUserSet(username, currentSet)
            Log.d("PowerPantry_FavHelper", "Removed $value from $username favorites.")
        } else {
            //log no value in set
            Log.d("PowerPantry_FavHelper", "$value not found in $username favorites.")
        }
    }

    /**
     * Method for checking if a value is in a user's favorites
     *
     * @param username String - The username of the user
     * @param value Long - The value to check
     * @return Boolean - Whether the value is in the user's favorites
     */
    fun isCodeInFavorites(username: String, value: Long): Boolean {
        //get user set
        val currentSet = getUserSet(username)
        //check is set has value
        val contains = currentSet.contains(value)
        Log.d("PowerPantry_FavHelper", "Checking if $value is in $username favorites: $contains")
        //return boolean (is in favorite or not)
        return contains
    }

    /**
     * Method for getting a user's favorites as a list
     *
     * @param username String - The username of the user
     * @return List<Long> - The list of favorites for the user
     */
    fun getAllUserFavorites(username: String): List<Long> {
        //return list of users favorite
        return getUserSet(username).toList()
    }
}


