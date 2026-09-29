package com.example.taskmanager.controller;

import com.example.taskmanager.config.SecurityConfig;
import com.example.taskmanager.security.CustomAccessDeniedHandler;
import com.example.taskmanager.security.CustomAuthEntryPoint;
import com.example.taskmanager.security.JwtAuthFilter;
import com.example.taskmanager.service.JwtService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserDetailsServiceImpl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
@Import({
        SecurityConfig.class,
        JwtAuthFilter.class,
        CustomAuthEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtService jwtService;



    @Test
    void getTasks_withoutAuthentication_shouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                        .value("Unauthorized"));
    }



    @Test
    @WithMockUser(roles = "USER")
    void getTasks_asUser_shouldReturnOk()
            throws Exception {

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(roles = "USER")
    void deleteTask_asUser_shouldReturnForbidden()
            throws Exception {

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Forbidden"));
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTask_asAdmin_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }
}