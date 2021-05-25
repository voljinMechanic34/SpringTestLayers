package ru.digitalhabbits.homework3.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.DepartmentShortResponse;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.service.DepartmentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DepartmentController.class)
class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource({"testDepartmentResponses"})
    void departments(List<DepartmentShortResponse> departmentShortResponses) throws Exception {
        Mockito.when(departmentService.findAllDepartments()).thenReturn(departmentShortResponses);

        mockMvc.perform(get("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(departmentShortResponses)));
    }

    @ParameterizedTest
    @MethodSource({"departmentResponse"})
    void department(DepartmentResponse departmentResponse) throws Exception {
        Mockito.when(departmentService.getDepartment(departmentResponse.getId())).thenReturn(departmentResponse);

        mockMvc.perform(get("/api/v1/departments/{id}", departmentResponse.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(departmentResponse)));

    }

    @Test
    void createDepartment() throws Exception {
        Integer createdId = 1;
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Hey");

        Mockito.when(departmentService.createDepartment(request)).thenReturn(createdId);

        mockMvc.perform(post("/api/v1/departments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource({"departmentResponse"})
    void updateDepartment(DepartmentResponse departmentResponse) throws Exception {
        Integer updatedId = departmentResponse.getId();
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Hey");

        Mockito.when(departmentService.updateDepartment(updatedId, request)).thenReturn(departmentResponse);

        mockMvc.perform(patch("/api/v1/departments/{id}", updatedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(departmentResponse)));
    }

    @Test
    void deleteDepartment() throws Exception {
        Integer deparmentId = 1;
        doNothing().when(departmentService).deleteDepartment(deparmentId);

        mockMvc.perform(delete("/api/v1/departments/{id}", deparmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void addPersonToDepartment() throws Exception {
        Integer deparmentId = 1;
        Integer personId = 1;
        doNothing().when(departmentService).addPersonToDepartment(deparmentId, personId);

        mockMvc.perform(post("/api/v1/departments/{departmentId}/{personId}", deparmentId, personId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePersonToDepartment() throws Exception {
        Integer deparmentId = 1;
        Integer personId = 1;
        doNothing().when(departmentService).removePersonToDepartment(deparmentId, personId);

        mockMvc.perform(delete("/api/v1/departments/{departmentId}/{personId}", deparmentId, personId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void closeDepartment() throws Exception {
        Integer deparmentId = 1;
        doNothing().when(departmentService).closeDepartment(deparmentId);

        mockMvc.perform(delete("/api/v1/departments/{departmentId}", deparmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    static Stream<DepartmentResponse> departmentResponse(){
        DepartmentResponse departmentResponse = new DepartmentResponse();
        PersonInfo personInfo = new PersonInfo();
        personInfo
                .setFullName("A A A")
                .setId(1);

        PersonInfo personInfo1 = new PersonInfo();
        personInfo1
                .setFullName("b b A")
                .setId(2);

        departmentResponse
                .setId(1)
                .setClosed(false)
                .setName("A")
                .setPersons(Arrays.asList(personInfo, personInfo1));


        return Stream.of(departmentResponse);
    }

    static Stream<List<DepartmentShortResponse>> testDepartmentResponses(){
        List<DepartmentShortResponse> departmentShortResponses = new ArrayList<>();
        DepartmentShortResponse departmentShortResponse = new DepartmentShortResponse();
        departmentShortResponse
                .setName("A")
                .setId(1);

        DepartmentShortResponse departmentShortResponse1 = new DepartmentShortResponse();
        departmentShortResponse1
                .setName("B")
                .setId(2);

        departmentShortResponses.add(departmentShortResponse);
        departmentShortResponses.add(departmentShortResponse1);

        return Stream.of(departmentShortResponses);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}