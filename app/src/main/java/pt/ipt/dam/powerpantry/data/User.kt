package pt.ipt.dam.powerpantry.data

//single row (user)
data class User(
    val id: Int,
    val username: String,
    val email: String,
)

//wrapper for response
data class UsersResponse(val users: List<User>)
