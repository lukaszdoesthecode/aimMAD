package com.example.madness.connections.queries.user

import com.example.tryingmybest.db.files.user.UserData

class DBqueriesUser(private val connection: java.sql.Connection): UserDAO {
    /**
     * Inserts a new user into the database
     * @param user the user to be inserted
     * @return true if the user was inserted successfully, false otherwise
     */
    override fun insertUser(user: UserData): Boolean {
        val query = "CALL insert_user(?)"
        val preparedStatement = connection.prepareCall(query)
        preparedStatement.setString(1, user.username)

        val result = !preparedStatement.execute()
        preparedStatement.close()

        return result
    }

    override fun getUserId(username: String): Int {
        /**
         * Gets the user id of a user given its username
         * @param username the username of the user
         * @return the user id of the user, -1 if the user does not exist
         */
        val query = "CALL get_user_id(?)"
        val preparedStatement = connection.prepareCall(query)
        preparedStatement.setString(1, username)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt("user_id")
        } else {
            -1
        }
    }
}