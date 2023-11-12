package com.softserve.itacademy.Tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("nick@mail.com")
    public void testHomePage()throws Exception{
        mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div[1]/nav/div/div/div/a").string("Nick"));
    }

    @Test
    @WithUserDetails("nick@mail.com")
    @DisplayName("Test update another user with role USER")
    public void testUpdateAnotherUserWithRoleUser()throws Exception{
        mockMvc.perform(get("/users/{id}/update", 6))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("403 / Forbidden")));
    }

    @Test
    @WithUserDetails("nick@mail.com")
    @DisplayName("Test delete another user with role USER")
    public void testDeleteAnotherUserWithRoleUser()throws Exception{
        mockMvc.perform(get("/users/{id}/delete", 6))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("403 / Forbidden")));
    }

    @Test
    @WithUserDetails("mike@mail.com")
    @DisplayName("Test update another user with role ADMIN")
    public void testUpdateAnotherUserWithRoleAdmin()throws Exception{
        mockMvc.perform(get("/users/{id}/update", 6))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Update Existing User")));
    }

    @Test
    @WithUserDetails("mike@mail.com")
    @DisplayName("Test delete another user with role ADMIN")
    @Transactional
    public void testDeleteAnotherUserWithRoleAdmin()throws Exception{
        mockMvc.perform(get("/users/{id}/delete", 6))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));
    }
}
