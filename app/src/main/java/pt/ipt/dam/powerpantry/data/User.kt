package pt.ipt.dam.powerpantry.data

//single row (user)
data class User(
    val userName: String,
    val eMail: String,
    val passWord: String,
)

//wrappers
data class AllUsersResponse(val users: List<User>)
