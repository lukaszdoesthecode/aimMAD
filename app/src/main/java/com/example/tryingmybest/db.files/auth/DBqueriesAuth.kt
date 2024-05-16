package com.example.madness.connections.queries.auth

import com.example.tryingmybest.db.files.auth.AuthData
import java.sql.Connection

class DBqueriesAuth(private val connection: Connection): AuthDAO {
    override fun insertAuth(auth: AuthData): Boolean {
        val query = "CALL insert_auth(?, ?, ?, ?)"
        val preparedStatement = connection.prepareCall(query)

        preparedStatement.setInt(1, auth.userId)
        preparedStatement.setString(2, auth.mail)
        preparedStatement.setString(3, auth.password)
        preparedStatement.setString(4, auth.role)

        val result = !preparedStatement.execute()
        preparedStatement.close()

        return result

    }

    override fun getAuth(mail: String, password: String): Int {
        val query = "CALL get_auth(?, ?)"
        val preparedStatement = connection.prepareCall(query)
        preparedStatement.setString(1, mail)
        preparedStatement.setString(2, password)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt("user_id")
        } else {
            -1
        }
    }

    override fun getRole(userId: Int): String {
        val query = "CALL get_auth_role(?)"
        val preparedStatement = connection.prepareCall(query)
        preparedStatement.setInt(1, userId)

        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getString("role")
        } else {
            ""
        }
    }
}