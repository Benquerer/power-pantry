package pt.ipt.dam.powerpantry.model

data class UserResponse(
    val users: List<User> // A list of users fetched from the API
)