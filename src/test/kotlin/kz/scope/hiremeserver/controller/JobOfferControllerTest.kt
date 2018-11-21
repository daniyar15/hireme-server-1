package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.CompanyJobOfferRequest
import kz.scope.hiremeserver.payload.JobOfferRequest
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
import java.util.*

@SpringBootTest(classes = arrayOf(HiremeServerApplication::class))
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JobOfferControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objMapper: ObjectMapper

    @Test
    @Transactional
    fun getJobOffer() {
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
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/9"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // job offer with non-existent id
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/0")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // existing job-offer
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/9")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getJobOfferByCompany() {
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
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-company?company_id=5"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // non-existing company case
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-company?company_id=0")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // existing company
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-company?company_id=5")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getJobOfferByPosition() {
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
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-position?position=Designer"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // add some existing role after "role="
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-position?position=Designer")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getJobOfferByLocation() {
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
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-location?location=Astana"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // add some existing skills after "skills="
        mvc.perform(MockMvcRequestBuilders.get("/api/job-offers/find-by-location?location=Astana")
                .header("Authorization", "Bearer ${response.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun createJobOffer() {
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
        mvc.perform(MockMvcRequestBuilders.post("/api/job-offer")
                .content(kz.scope.hiremeserver.controller.asJsonString(JobOfferRequest(
                        company = CompanyJobOfferRequest(
                                company_id = 9),
                        position = "software engineer",
                        responsibilities = "back-end, testing",
                        qualifications = "BSc in CS related majors",
                        locations = Arrays.asList("Astana", "Paris", "London"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // case: non-existent company with company_id = 0
        mvc.perform(MockMvcRequestBuilders.post("/api/job-offer")
                .header("Authorization", "Bearer ${response.accessToken}")
                .content(kz.scope.hiremeserver.controller.asJsonString(JobOfferRequest(
                        company = CompanyJobOfferRequest(
                                company_id = 0),
                        position = "software engineer",
                        responsibilities = "back-end, testing",
                        qualifications = "BSc in CS related majors",
                        locations = Arrays.asList("Astana", "Paris", "London"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed)

        // case: alien company
        mvc.perform(MockMvcRequestBuilders.post("/api/job-offer")
                .header("Authorization", "Bearer ${response.accessToken}")
                .content(kz.scope.hiremeserver.controller.asJsonString(JobOfferRequest(
                        company = CompanyJobOfferRequest(
                                company_id = 5),
                        position = "software engineer",
                        responsibilities = "back-end, testing",
                        qualifications = "BSc in CS related majors",
                        locations = Arrays.asList("Astana", "Paris", "London"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // case: my company, company_id = 9
        mvc.perform(MockMvcRequestBuilders.post("/api/job-offer")
                .header("Authorization", "Bearer ${response.accessToken}")
                .content(kz.scope.hiremeserver.controller.asJsonString(JobOfferRequest(
                        company = CompanyJobOfferRequest(
                                company_id = 9),
                        position = "software engineer",
                        responsibilities = "back-end, testing",
                        qualifications = "BSc in CS related majors",
                        locations = Arrays.asList("Astana", "Paris", "London"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}