package kz.scope.hiremeserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kz.scope.hiremeserver.HiremeServerApplication
import kz.scope.hiremeserver.payload.CompanyCreator
import kz.scope.hiremeserver.payload.CompanyRequest
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
class CompanyControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objMapper: ObjectMapper

    @Test
    @Transactional
    fun getCompanyProfile() {
        // company with non-existent id
        mvc.perform(MockMvcRequestBuilders.get("/api/companies/0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // existing company
        mvc.perform(MockMvcRequestBuilders.get("/api/companies/1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun getCompanyProfiles() {
        // wrong name
        mvc.perform(MockMvcRequestBuilders.get("/api/companies/find?name=kek&location=Astana"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // wrong location
        mvc.perform(MockMvcRequestBuilders.get("/api/companies/find?name=Alphabet Inc&location=Gonduraz"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)

        // right parameters
        mvc.perform(MockMvcRequestBuilders.get("/api/companies/find?name=Alphabet Inc&location=Astana"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Transactional
    fun createCompany() {
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
        mvc.perform(MockMvcRequestBuilders.post("/api/company")
                .content(kz.scope.hiremeserver.controller.asJsonString(CompanyRequest(
                        name = "Uzumaki Inc",
                        creator = CompanyCreator(
                                username = "the_7th_hokage",
                                role = "Head"
                        ),
                        logo_url = "#",
                        location = "Hidden Leaf Village",
                        employee_number = 15,
                        specialization = "Search for the philosopher's stones",
                        description = "Nice company")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        // creating company
        mvc.perform(MockMvcRequestBuilders.post("/api/company")
                .header("Authorization", "Bearer ${response.accessToken}")
                .content(kz.scope.hiremeserver.controller.asJsonString(CompanyRequest(
                        name = "Uzumaki Inc",
                        creator = CompanyCreator(
                                username = "the_7th_hokage",
                                role = "Head"
                        ),
                        logo_url = "#",
                        location = "Hidden Leaf Village",
                        employee_number = 15,
                        specialization = "Search for the philosopher's stones",
                        description = "Nice company")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}