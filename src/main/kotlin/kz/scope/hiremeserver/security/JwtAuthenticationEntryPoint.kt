package kz.scope.hiremeserver.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by scope team on 07/12/17.
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(httpServletRequest: HttpServletRequest,
                          httpServletResponse: HttpServletResponse,
                          e: AuthenticationException) {
        logger.error("Responding with unauthorized error. Message - {}", e.message)
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            "Sorry, You're not authorized to access this resource.")
    }

    companion object {

        private val logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)
    }
}
