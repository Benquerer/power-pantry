package pt.ipt.dam.powerpantry.data

/**
 * Data class to handle users
 */
data class User(
    val userName: String,
    val eMail: String,
    val passWord: String,
)

/**
 * Wrapper for response on fetching all users
 */
data class AllUsersResponse(val users: List<User>)

/**
 * Wrapper for response on posting a user
 */
data class UserResponse(val user: User)

/**
 * Wrapper for request for posting a user
 */
data class UserRequest(val user: User)
