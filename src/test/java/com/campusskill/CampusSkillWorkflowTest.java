package com.campusskill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CampusSkillWorkflowTest {

    private static final String PASSWORD = "Password@123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private record Account(long id, MockHttpSession session) {
    }

    @Test
    void signupRequiresCollegeEmailAndUniqueAddress() throws Exception {
        String email = uniqueEmail("signup");
        call(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(signupJson("Outsider", "outsider@gmail.com")), null)
                .andExpect(status().isBadRequest());

        signup("Student", email);

        call(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(signupJson("Duplicate", email)), null)
                .andExpect(status().isConflict());
    }

    @Test
    void loginChecksPasswordAndSessionProtectsRoutes() throws Exception {
        String email = uniqueEmail("login");
        signup("Login User", email);

        call(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, "WrongPassword1")), null)
                .andExpect(status().isUnauthorized());

        Account account = login(email, PASSWORD);
        call(get("/api/users/me"), account.session())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        call(get("/api/users/me"), null).andExpect(status().isUnauthorized());
    }

    @Test
    void skillSwapFollowsWorkflowRules() throws Exception {
        Account tutor = signup("Tutor", uniqueEmail("tutor"));
        Account learner = signup("Learner", uniqueEmail("learner"));
        long javaId = skillId(tutor.session(), "Java Programming");
        addSkill(tutor.session(), javaId, "TEACH");

        call(postJson("/api/requests", tutor.session(), Map.of("receiverId", tutor.id(), "skillId", javaId)), null)
                .andExpect(status().isUnauthorized());
        call(postJson("/api/requests", tutor.session(), Map.of("receiverId", tutor.id(), "skillId", javaId)), tutor.session())
                .andExpect(status().isBadRequest());

        call(postJson("/api/requests", learner.session(),
                Map.of("receiverId", tutor.id(), "skillId", skillId(learner.session(), "Photography"))), learner.session())
                .andExpect(status().isBadRequest());

        JsonNode created = body(call(postJson("/api/requests", learner.session(),
                Map.of("receiverId", tutor.id(), "skillId", javaId)), learner.session())
                .andExpect(status().isCreated()));
        long requestId = created.get("id").asLong();
        assertEquals("PENDING", created.get("status").asText());

        call(postJson("/api/requests", learner.session(),
                Map.of("receiverId", tutor.id(), "skillId", javaId)), learner.session())
                .andExpect(status().isConflict());

        String statusUrl = "/api/requests/" + requestId + "/status";
        call(postJson(statusUrl, learner.session(), Map.of("status", "ACCEPTED")), learner.session())
                .andExpect(status().isForbidden());
        call(postJson(statusUrl, tutor.session(), Map.of("status", "COMPLETED")), tutor.session())
                .andExpect(status().isConflict());
        call(postJson(statusUrl, tutor.session(), Map.of("status", "ACCEPTED")), tutor.session())
                .andExpect(status().isOk());
        call(postJson(statusUrl, learner.session(), Map.of("status", "COMPLETED")), learner.session())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        String ratingUrl = "/api/requests/" + requestId + "/rating";
        call(postJson(ratingUrl, tutor.session(), Map.of("rating", 5)), tutor.session())
                .andExpect(status().isForbidden());
        call(postJson(ratingUrl, learner.session(), Map.of("rating", 9)), learner.session())
                .andExpect(status().isBadRequest());
        call(postJson(ratingUrl, learner.session(), Map.of("rating", 5)), learner.session())
                .andExpect(jsonPath("$.rating").value(5));
        call(postJson(ratingUrl, learner.session(), Map.of("rating", 3)), learner.session())
                .andExpect(status().isConflict());

        call(get("/api/users/me"), tutor.session())
                .andExpect(jsonPath("$.averageRating").value(5.0))
                .andExpect(jsonPath("$.ratingCount").value(1));
        call(get("/api/dashboard"), tutor.session())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedExchanges").value(1));
    }

    @Test
    void tutorSearchExcludesSelfAndFiltersBySkill() throws Exception {
        Account tutor = signup("Searchable Tutor", uniqueEmail("search"));
        Account viewer = signup("Viewer", uniqueEmail("viewer"));
        addSkill(tutor.session(), skillId(tutor.session(), "Statistics"), "TEACH");

        JsonNode results = body(call(get("/api/tutors?skill=statist"), viewer.session())
                .andExpect(status().isOk()));
        assertTrue(containsId(results, tutor.id()));
        assertFalse(containsId(results, viewer.id()));

        JsonNode ownResults = body(call(get("/api/tutors?skill=statist"), tutor.session())
                .andExpect(status().isOk()));
        assertFalse(containsId(ownResults, tutor.id()));
    }

    @Test
    void studyGroupMembershipRules() throws Exception {
        Account creator = signup("Group Creator", uniqueEmail("creator"));
        Account joiner = signup("Joiner", uniqueEmail("joiner"));
        Account latecomer = signup("Latecomer", uniqueEmail("late"));

        JsonNode group = body(call(postJson("/api/groups", creator.session(), Map.of(
                "name", "Test Circle " + UUID.randomUUID(),
                "subject", "Java Programming",
                "description", "Testing group rules",
                "maxMembers", 2,
                "mode", "ONLINE")), creator.session()).andExpect(status().isCreated()));
        long groupId = group.get("id").asLong();
        assertEquals(1, group.get("memberCount").asInt());
        assertTrue(group.get("owner").asBoolean());

        call(post("/api/groups/" + groupId + "/leave"), creator.session()).andExpect(status().isConflict());

        call(post("/api/groups/" + groupId + "/join"), joiner.session())
                .andExpect(jsonPath("$.group.full").value(true));
        call(post("/api/groups/" + groupId + "/join"), joiner.session()).andExpect(status().isConflict());
        call(post("/api/groups/" + groupId + "/join"), latecomer.session()).andExpect(status().isConflict());

        call(post("/api/groups/" + groupId + "/leave"), joiner.session())
                .andExpect(jsonPath("$.group.memberCount").value(1));
        call(post("/api/groups/" + groupId + "/join"), latecomer.session()).andExpect(status().isOk());
    }

    @Test
    void adminRoleIsEnforcedAndDeletionCleansUpRelatedRecords() throws Exception {
        Account admin = login("admin@srmap.edu.in", "Admin@12345");
        Account student = signup("Doomed Student", uniqueEmail("doomed"));
        Account classmate = signup("Classmate", uniqueEmail("classmate"));

        call(get("/api/admin/stats"), student.session()).andExpect(status().isForbidden());
        call(get("/api/admin/stats"), admin.session()).andExpect(status().isOk());

        JsonNode group = body(call(postJson("/api/groups", student.session(), Map.of(
                "name", "Doomed Circle " + UUID.randomUUID(),
                "subject", "Photography",
                "maxMembers", 5,
                "mode", "OFFLINE")), student.session()).andExpect(status().isCreated()));
        long groupId = group.get("id").asLong();
        call(post("/api/groups/" + groupId + "/join"), classmate.session()).andExpect(status().isOk());

        call(delete("/api/admin/users/" + admin.id()), admin.session()).andExpect(status().isBadRequest());
        call(delete("/api/admin/users/" + student.id()), admin.session()).andExpect(status().isNoContent());

        call(get("/api/users/me"), student.session()).andExpect(status().isUnauthorized());
        call(get("/api/groups/" + groupId), classmate.session()).andExpect(status().isNotFound());
    }

    @Test
    void publicPreviewIsAvailableWithoutLogin() throws Exception {
        call(get("/api/public/preview"), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").isArray())
                .andExpect(jsonPath("$.tutors").isArray());
    }

    private Account signup(String name, String email) throws Exception {
        ResultActions result = call(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(signupJson(name, email)), null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("STUDENT"));
        return new Account(body(result).get("id").asLong(), sessionOf(result));
    }

    private Account login(String email, String password) throws Exception {
        ResultActions result = call(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, password)), null)
                .andExpect(status().isOk());
        return new Account(body(result).get("id").asLong(), sessionOf(result));
    }

    private MockHttpSession sessionOf(ResultActions result) {
        return (MockHttpSession) result.andReturn().getRequest().getSession(false);
    }

    private long skillId(MockHttpSession session, String skillName) throws Exception {
        JsonNode skills = body(call(get("/api/skills"), session).andExpect(status().isOk()));
        for (JsonNode skill : skills) {
            if (skillName.equals(skill.get("name").asText())) {
                return skill.get("id").asLong();
            }
        }
        throw new AssertionError("Seed skill missing: " + skillName);
    }

    private void addSkill(MockHttpSession session, long skillId, String type) throws Exception {
        call(postJson("/api/users/me/skills", session, Map.of("skillId", skillId, "type", type)), session)
                .andExpect(status().isCreated());
    }

    private boolean containsId(JsonNode array, long id) {
        for (JsonNode item : array) {
            if (item.get("id").asLong() == id) {
                return true;
            }
        }
        return false;
    }

    private MockHttpServletRequestBuilder postJson(String url, MockHttpSession session, Object body) throws Exception {
        return post(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
    }

    private ResultActions call(MockHttpServletRequestBuilder builder, MockHttpSession session) throws Exception {
        if (session != null) {
            builder.session(session);
        }
        return mockMvc.perform(builder);
    }

    private JsonNode body(ResultActions result) throws Exception {
        return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
    }

    private String signupJson(String name, String email) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "name", name,
                "email", email,
                "password", PASSWORD,
                "branch", "B.Tech CSE",
                "year", 2,
                "bio", "Test account"));
    }

    private String loginJson(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of("email", email, "password", password));
    }

    private static String uniqueEmail(String prefix) {
        return prefix + "." + UUID.randomUUID().toString().substring(0, 8) + "@srmap.edu.in";
    }
}
