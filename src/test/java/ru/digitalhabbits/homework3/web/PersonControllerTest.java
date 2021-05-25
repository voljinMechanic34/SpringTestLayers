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
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;
import ru.digitalhabbits.homework3.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {

    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("testPersonResponses")
    void persons(List<PersonResponse> personResponses) throws Exception {
        Mockito.when(personService.findAllPersons()).thenReturn(personResponses);

        mockMvc.perform(get("/api/v1/persons/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(personResponses)));
        ;

    }

    @ParameterizedTest
    @MethodSource("testPersonResponses")
    void person(List<PersonResponse> personResponses) throws Exception {
        PersonResponse personResponse = personResponses.get(0);

        Mockito.when(personService.getPerson(personResponse.getId())).thenReturn(personResponse);

        mockMvc.perform(get("/api/v1/persons/{id}", personResponse.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personResponse.getId()))
                .andExpect(jsonPath("$.age").value(personResponse.getAge()))
                .andExpect(jsonPath("$.fullName").value(personResponse.getFullName()))
                .andExpect(jsonPath("$.department.id").value(personResponse.getDepartment().getId()))
                .andExpect(jsonPath("$.department.name").value(personResponse.getDepartment().getName()));
    }

    @ParameterizedTest
    @MethodSource("testPersonRequest")
    void createPerson(PersonRequest request) throws Exception {
        Integer personId = 1;
        Mockito.when(personService.createPerson(request)).thenReturn(personId);

        mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource({"testPersonResponses"})
    void updatePerson(List<PersonResponse> personResponses) throws Exception {
        PersonResponse personResponse = personResponses.get(0);
        PersonRequest personRequest = new PersonRequest();
        personRequest
                .setAge(32)
                .setDepartmentId(1)
                .setMiddleName("A")
                .setLastName("B")
                .setFirstName("C");

        Mockito.when(personService.updatePerson(personResponse.getId(), personRequest)).thenReturn(personResponse);

        mockMvc.perform(patch("/api/v1/persons/{id}",personResponse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(personResponse)));
    }

    @Test
    void deletePerson() throws Exception {
        Integer personId = 1;
        doNothing().when(personService).deletePerson(personId);

        mockMvc.perform(delete("/api/v1/persons/{id}",personId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    static Stream<PersonRequest> testPersonRequest(){
        PersonRequest request = new PersonRequest();
        request
                .setAge(32)
                .setFirstName("A")
                .setLastName("B")
                .setMiddleName("C")
                .setDepartmentId(1);

        return Stream.of(request);
    }

    static Stream<List<PersonResponse>> testPersonResponses(){
        List<PersonResponse> personResponses = new ArrayList<>();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setName("A")
                .setId(1);
        PersonResponse personResponse = new PersonResponse();
        personResponse
                .setId(1)
                .setFullName("A B C")
                .setAge(32)
                .setDepartment(departmentInfo);

        DepartmentInfo departmentInfo1 = new DepartmentInfo();
        departmentInfo1
                .setName("B")
                .setId(2);
        PersonResponse personResponse1 = new PersonResponse();
        personResponse1
                .setId(2)
                .setFullName("A B S A C")
                .setAge(12)
                .setDepartment(departmentInfo1);

        personResponses.add(personResponse);
        personResponses.add(personResponse1);
        return Stream.of(personResponses);
    }
}