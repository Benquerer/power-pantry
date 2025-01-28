package pt.ipt.dam.powerpantry.model

data class User(
    val id: Int? = null, // Optional for newly added rows
    val email: String,
    val password: String,
    val username: String
)


