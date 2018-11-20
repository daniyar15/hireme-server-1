package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.LoginRequest
import kz.scope.hiremeserver.payload.SignUpRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = arrayOf(HiremeServerApplication::class))
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @Transactional
    fun authenticateUser() {
        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.username,
                        password = "wrong_password")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.email,
                        password = existingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.username,
                        password = existingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun registerUser() {
        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(asJsonString(SignUpRequest(
                        fullname = nonExistingUser.fullname,
                        username = existingUser.username,
                        email = nonExistingUser.email,
                        password = nonExistingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(asJsonString(SignUpRequest(
                        fullname = nonExistingUser.fullname,
                        username = nonExistingUser.username,
                        email = existingUser.email,
                        password = nonExistingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(asJsonString(SignUpRequest(
                        fullname = nonExistingUser.fullname,
                        username = nonExistingUser.username,
                        email = nonExistingUser.email,
                        password = nonExistingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}

fun asJsonString(obj: Any): String {
    try {
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(obj)
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}