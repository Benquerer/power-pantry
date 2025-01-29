package pt.ipt.dam.powerpantry.data

//single row (user)
data class User(
    val id: Int,
    val username: String,
    val email: String,
)

//wrappers
data class AllUsersResponse(val users: List<User>)
data class IdUserResponse(val user : User)
