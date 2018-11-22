package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.JwtAuthenticationResponse
import kz.scope.hiremeserver.payload.LoginRequest
import kz.scope.hiremeserver.payload.PostRequest
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
import java.util.*

@SpringBootTest(classes = arrayOf(HiremeServerApplication::class))
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objMapper: ObjectMapper

//    @Test
//    @Transactional
//    fun getPost() {
//        // signing in
//        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
//                .content(kz.scope.hiremeserver.controller.asJsonString(LoginRequest(
//                        usernameOrEmail = existingUser.username,
//                        password = existingUser.password)))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk)
//                .andReturn()
//
//        objMapper.registerKotlinModule()
//        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
//        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()
//
//        // without authorization header
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts/1"))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
//
//        // post with non-existent id
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts/0")
//                .header("Authorization", "Bearer ${response.accessToken}"))
//                .andExpect(MockMvcResultMatchers.status().isNotFound)
//
//        // post with existing id = 1
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts/1")
//                .header("Authorization", "Bearer ${response.accessToken}"))
//                .andExpect(MockMvcResultMatchers.status().isOk)
//    }
//
//    @Test
//    @Transactional
//    fun addPost() {
//        // signing in
//        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
//                .content(asJsonString(LoginRequest(
//                        usernameOrEmail = existingUser.username,
//                        password = existingUser.password)))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk)
//                .andReturn()
//
//        objMapper.registerKotlinModule()
//        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
//        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()
//
//        // without authorization header
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .content(asJsonString(PostRequest(
//                        company = true,
//                        author = 1,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList())))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
//
//        // case: author is company, but company does not exist
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = true,
//                        author = 0,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList())))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isExpectationFailed)
//
//        // case: author is company, but it is not yours
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = true,
//                        author = 1,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList())))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
//
//        // case: author is your company, but some of the job offers don't exist
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = true,
//                        author = 9,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList(0))))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isExpectationFailed)
//
//        // case: author is your company, job offers are okay
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = true,
//                        author = 9,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList(1,2))))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isCreated)
//
//        // author is user, but it does not exist
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = false,
//                        author = 0,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList())))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isExpectationFailed)
//
//        // case: author is user, but not you
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = false,
//                        author = 1,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList())))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
//
//        // case: author is user, and it is you, but some of the job offers don't exist
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = false,
//                        author = 1156,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList(0))))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isExpectationFailed)
//
//        // case: author is user, and it is you, job offers are okay
//        mvc.perform(MockMvcRequestBuilders.post("/api/post")
//                .header("Authorization", "Bearer ${response.accessToken}")
//                .content(asJsonString(PostRequest(
//                        company = false,
//                        author = 1156,
//                        title = "Good post",
//                        text = "text of a good post.",
//                        jobOffersIds = Arrays.asList(1))))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isCreated)
//    }
//
//    @Test
//    @Transactional
//    fun getPosts() {
//        // signing in
//        val result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
//                .content(kz.scope.hiremeserver.controller.asJsonString(LoginRequest(
//                        usernameOrEmail = existingUser.username,
//                        password = existingUser.password)))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk)
//                .andReturn()
//
//        objMapper.registerKotlinModule()
//        val response : JwtAuthenticationResponse = objMapper.readValue(result.response.contentAsString)
//        Assertions.assertThat(response.accessToken).isNotNull().isNotEmpty()
//
//        // without authorization header
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts"))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
//
//        // method call
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts")
//                .header("Authorization", "Bearer ${response.accessToken}"))
//                .andExpect(MockMvcResultMatchers.status().isOk)
//    }
}