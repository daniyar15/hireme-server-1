package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.*
import org.assertj.core.api.Assertions
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
class UserControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objMapper: ObjectMapper

    @Test
    @Transactional
    fun getCurrentUser() {
        // signing in
        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(kz.scope.hiremeserver.controller.asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.username,
                        password = existingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        objMapper.registerKotlinModule()
        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()


        // without authorization header
        mvc.perform(MockMvcRequestBuilders.get("/api/user/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // accessing profile page
        mvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun postCurrentUserProfile() {
        // authenticating
        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(kz.scope.hiremeserver.controller.asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.username,
                        password = existingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        objMapper.registerKotlinModule()
        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()


        // without authorization header
        mvc.perform(MockMvcRequestBuilders.post("/api/user/me")
                .content(kz.scope.hiremeserver.controller.asJsonString(UserProfile(
                        username = existingUser.username,
                        fullname = existingUser.fullname,
                        location = "Hidden Leaf Village",
                        employment = Employment(
                                position = "hokage",
                                company = "Kanoha inc."),
                        current_role = "ninja",
                        education = Education(
                                university = "Academy",
                                graduation_year = 2020,
                                graduation_month = "June",
                                major = "Ninja",
                                degree = "Genin"),
                        hidden = false,
                        job_type = "Full-time",
                        job_field = "Village's Father",
                        skills = "clones"
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // updating / creating user info
        mvc.perform(MockMvcRequestBuilders.post("/api/user/me")
                .header("Authorization", "Bearer ${response.accessToken}")
                .content(kz.scope.hiremeserver.controller.asJsonString(UserProfile(
                        username = existingUser.username,
                        fullname = existingUser.fullname,
                        location = "Hidden Leaf Village",
                        employment = Employment(
                                position = "hokage",
                                company = "Kanoha inc."),
                        current_role = "ninja",
                        education = Education(
                                university = "Academy",
                                graduation_year = 2020,
                                graduation_month = "June",
                                major = "Ninja",
                                degree = "Genin"),
                        hidden = false,
                        job_type = "Full-time",
                        job_field = "Village's Father",
                        skills = "clones"
                        )))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @Transactional
    fun getCurrentUserProfile() {
        // authenticating
        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(kz.scope.hiremeserver.controller.asJsonString(LoginRequest(
                        usernameOrEmail = existingUser.username,
                        password = existingUser.password)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        objMapper.registerKotlinModule()
        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()

        // without authorization header
        mvc.perform(MockMvcRequestBuilders.get("/api/user/me/profile"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // accessing own user info
        mvc.perform(MockMvcRequestBuilders.get("/api/user/me/profile")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun checkUsernameAvailability() {
        mvc.perform(MockMvcRequestBuilders.get("/api/user/checkUsernameAvailability?username=kek"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun checkEmailAvailability() {
        mvc.perform(MockMvcRequestBuilders.get("/api/user/checkEmailAvailability?email=kek@nu.edu.kz"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getUserProfile() {
        // non-existing user
        mvc.perform(MockMvcRequestBuilders.get("/api/users/johnny_sins"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // existing user
        mvc.perform(MockMvcRequestBuilders.get("/api/users/the_7th_hokage"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}