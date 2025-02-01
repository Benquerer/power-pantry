package pt.ipt.dam.powerpantry.ui.favorites

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavPrefHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    companion object {
        private const val PREF_NAME = "FavPreferences"
        private const val MAP_KEY = "fav_map"
        private const val TAG = "FAVORITE_DEBUG"
    }

    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
        Log.d(TAG, "All preferences cleared.")
    }

    fun saveUserSet(username: String, set: Set<Long>) {
        val currentMap = getMap()
        val updatedMap = currentMap.toMutableMap()
        updatedMap[username] = set
        val json = gson.toJson(updatedMap)
        sharedPreferences.edit().putString(MAP_KEY, json).apply()
        Log.d(TAG, "Saved favorites for $username: $set")
    }

    fun getUserSet(username: String): Set<Long> {
        val currentMap = getMap()
        val userFavorites = currentMap[username] ?: emptyList()
        return userFavorites.toSet()
    }

    private fun getMap(): Map<String, Set<Long>> {
        val json = sharedPreferences.getString(MAP_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<Map<String, Set<Long>>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyMap() // Return empty map if not found
        }
    }

    fun appendUserFavorite(username: String, value: Long) {
        val currentSet = getUserSet(username).toMutableSet()
        if (currentSet.add(value)) {
            saveUserSet(username, currentSet)
            Log.d(TAG, "Added $value to $username favorites.")
        } else {
            Log.d(TAG, "$value is already in $username favorites. Skipping.")
        }
    }

    fun removeUserFavorite(username: String, value: Long) {
        val currentSet = getUserSet(username).toMutableSet()
        if (currentSet.remove(value)) {
            saveUserSet(username, currentSet)
            Log.d(TAG, "Removed $value from $username favorites.")
        } else {
            Log.d(TAG, "$value not found in $username favorites.")
        }
    }

    fun isCodeInFavorites(username: String, value: Long): Boolean {
        val currentSet = getUserSet(username)
        val contains = currentSet.contains(value)
        Log.d(TAG, "Checking if $value is in $username favorites: $contains")
        return contains
    }
}


