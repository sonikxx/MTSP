package api.repository

import api.dto.MtspResponse
import api.dto.UserDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        UserDto(
            id = rs.getLong("id"),
            organizationId = rs.getLong("organization_id"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            isAdmin = rs.getBoolean("is_admin"),
            lastActivity = rs.getTimestamp("last_activity").toLocalDateTime()
        )
    }

    fun findByEmail(email: String): UserDto? {
        val sql = "SELECT * FROM mtsp_users WHERE email = ?"
        val users = jdbcTemplate.query(sql, rowMapper, email)
        return users.firstOrNull()
    }

//    fun getSolution(requestId: Long, jdbcTemplate: JdbcTemplate): MtspResponse {
//        // TODO проверить что запрос работает
//        val sql = """
//        SELECT s.status, r.points
//        FROM mtsp_solutions s
//        LEFT JOIN mtsp_routes r ON s.id = r.solution_id
//        WHERE s.id = ?
//    """
//
//        val result = jdbcTemplate.query(sql, RowMapper { rs, _ ->
//            val status = rs.getString("status")
//            val pointsString = rs.getString("points")
//
//            val routes = if (pointsString != null) {
//                pointsString.trim("[]")
//                    .split(",")
//                    .map { it.trim() }
//            } else {
//                emptyList<String>()
//            }
//
//            status to routes
//        }, requestId)
//
//        val status = result.firstOrNull()?.first ?: "INTERMEDIATE"
//        val routes = result.map { it.second }
//
//        // Возвращаем MtspResponse с результатом
//        return MtspResponse(status, routes)
//    }
//    fun save(user: UserDto) {
//        val sql = """
//            INSERT INTO mtsp_users (organization_id, first_name, last_name, email, password_hash, is_admin, last_activity)
//            VALUES (?, ?, ?, ?, ?, ?, ?)
//        """.trimIndent()
//
//        jdbcTemplate.update(
//            sql,
//            user.organizationId,
//            user.firstName,
//            user.lastName,
//            user.email,
//            user.passwordHash,
//            user.lastActivity
//        )
//    }
}
