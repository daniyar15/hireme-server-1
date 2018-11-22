package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.JwtAuthenticationResponse
import kz.scope.hiremeserver.payload.LoginRequest
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
class FollowingControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objMapper: ObjectMapper

    @Test
    @Transactional
    fun getFollowingUsers() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/following"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target user
        mvc.perform(MockMvcRequestBuilders.get("/api/non-existent/following")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // ok
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/following")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getFollowedUsers() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/followed"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target user
        mvc.perform(MockMvcRequestBuilders.get("/api/non-existent/followers")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // ok
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/followers")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getFollowingCompanies() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/following-companies"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target user
        mvc.perform(MockMvcRequestBuilders.get("/api/non-existent/following-companies")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // ok
        mvc.perform(MockMvcRequestBuilders.get("/api/the_7th_hokage/following-companies")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getCompanyFollowers() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.get("/api/9/company-followers"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target company
        mvc.perform(MockMvcRequestBuilders.get("/api/0/company-followers")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // ok
        mvc.perform(MockMvcRequestBuilders.get("/api/9/company-followers")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun follow() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.post("/api/bek_zhan/follow"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target user
        mvc.perform(MockMvcRequestBuilders.post("/api/non-existent/follow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // cannot follow yourself
        mvc.perform(MockMvcRequestBuilders.post("/api/the_7th_hokage/follow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // cannot follow twice
        mvc.perform(MockMvcRequestBuilders.post("/api/kirito/follow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // ok
        mvc.perform(MockMvcRequestBuilders.post("/api/sasuke/follow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @Transactional
    fun unfollow() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.delete("/api/spike/unfollow"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target user
        mvc.perform(MockMvcRequestBuilders.delete("/api/non-existent/unfollow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // cannot follow yourself
        mvc.perform(MockMvcRequestBuilders.delete("/api/the_7th_hokage/unfollow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // cannot unfollow if you don't already follow
        mvc.perform(MockMvcRequestBuilders.delete("/api/sasuke/unfollow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // ok
        mvc.perform(MockMvcRequestBuilders.delete("/api/spike/unfollow")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @Transactional
    fun followCompany() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.post("/api/10/follow-company"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target company
        mvc.perform(MockMvcRequestBuilders.post("/api/0/follow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // cannot follow twice
        mvc.perform(MockMvcRequestBuilders.post("/api/9/follow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // ok
        mvc.perform(MockMvcRequestBuilders.post("/api/10/follow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @Transactional
    fun unfollowCompany() {
        // authentication
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
        mvc.perform(MockMvcRequestBuilders.delete("/api/9/unfollow-company"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non existing target company
        mvc.perform(MockMvcRequestBuilders.delete("/api/0/unfollow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // cannot unfollow if you don't already follow
        mvc.perform(MockMvcRequestBuilders.delete("/api/10/unfollow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        // ok
        mvc.perform(MockMvcRequestBuilders.delete("/api/9/unfollow-company")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}