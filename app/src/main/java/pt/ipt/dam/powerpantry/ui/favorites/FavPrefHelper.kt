package pt.ipt.dam.powerpantry.ui.favorites

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class FavPrefHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    companion object{
        private const val PREF_NAME = "FavPreferences"
        private const val MAP_KEY = "fav_map"
    }

    //save the list of a user
    fun saveUserList(username: String, list: List<Long>) {
        //get map
        val currentMap = getMap()
        val updatedMap = currentMap.toMutableMap()
        // put username as key and list as value in map
        updatedMap[username] = list

        //save updated map on preferences
        val json = gson.toJson(updatedMap)
        sharedPreferences.edit().putString(MAP_KEY, json).apply()
    }

    //get list of a user
    fun getUserList(username: String): List<Long> {
        //get map of favorites
        val currentMap = getMap()
        //return full list of a user //empty if not found
        return currentMap[username] ?: emptyList()
    }

    //get the entire map of favorites
    private fun getMap(): Map<String, List<Long>> {
        val json = sharedPreferences.getString(MAP_KEY, null)
        return if (json != null) {
            gson.fromJson(json, HashMap::class.java) as Map<String, List<Long>>
        } else {
            emptyMap() //empty if not found
        }
    }

    //append barcode to a users list
    fun appendUserFavorite(username: String, value: Long) {
        val currentList = getUserList(username).toMutableList()
        if (!currentList.contains(value)) {
            currentList.add(value)
            saveUserList(username, currentList)
        }
    }

    fun removeUserFavorite(username: String, value: Long) {
        val currentList = getUserList(username).toMutableList()
        if (currentList.contains(value)) {
            currentList.remove(value)
            saveUserList(username, currentList)
        }
    }

    fun isCodeInFavorites(username: String, value: Long): Boolean{
        val currentList = getUserList(username)
        return currentList.contains(value)
    }
}