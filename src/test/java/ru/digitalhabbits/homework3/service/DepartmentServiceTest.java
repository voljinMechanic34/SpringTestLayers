package ru.digitalhabbits.homework3.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DepartmentServiceImpl.class)
class DepartmentServiceTest {

    @MockBean
    private DepartmentDao departmentDao;

    @MockBean
    private PersonService personService;

    @Autowired
    private DepartmentService departmentService;

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void findAllDepartments(List<Department> departments) {
        Mockito.when(departmentDao.findAll()).thenReturn(departments);

        List<DepartmentShortResponse> allDepartments = departmentService.findAllDepartments();

        assertTrue(departments.size() == allDepartments.size());

        for(int i = 0; i < departments.size(); i++){
            assertThat(departments.get(i).getId())
                    .isEqualTo(allDepartments.get(i).getId());
            assertThat(departments.get(i).getName())
                    .isEqualTo(allDepartments.get(i).getName());
        }
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void getDepartment(List<Department> departments) {
        Department department = departments.get(0);
        List<PersonResponse> personResponses = new ArrayList<>();
        PersonResponse personResponse = new PersonResponse();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setId(department.getId())
                .setName(department.getName());
        personResponse
                .setId(1)
                .setAge(2)
                .setDepartment(departmentInfo)
                .setFullName("Alex Alexov Alexeev");
        personResponses.add(personResponse);

        Mockito.when(departmentDao.findById(department.getId())).thenReturn(department);
        Mockito.when(personService.findAllPersons()).thenReturn(personResponses);

        DepartmentResponse departmentResponse = departmentService.getDepartment(department.getId());
        assertThat(department.getId())
                .isEqualTo(departmentResponse.getId());
        assertThat(department.getName())
                .isEqualTo(departmentResponse.getName());
        assertTrue(personResponses.size() == departmentResponse.getPersons().size());

        for(int i = 0; i < personResponses.size(); i++){
            assertThat(personResponses.get(i).getId())
                    .isEqualTo(departmentResponse.getPersons().get(i).getId());
            assertThat(personResponses.get(i).getFullName())
                    .isEqualTo(departmentResponse.getPersons().get(i).getFullName());
        }

    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void createDepartment(List<Department> departments) {
        Department department = departments.get(0);
        DepartmentRequest request = new DepartmentRequest();
        request.setName(department.getName());

        Mockito.when(departmentDao.create(any(Department.class)))
                .thenReturn(department);

        Integer createdDepartmentId = departmentService.createDepartment(request);
        assertThat(createdDepartmentId).isNotNull();
        assertThat(createdDepartmentId).isGreaterThan(-1);
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void updateDepartment(List<Department> departments) {
        Department department = departments.get(0);
        DepartmentRequest request = new DepartmentRequest();
        request.setName(department.getName());

        List<PersonResponse> personResponses = new ArrayList<>();
        PersonResponse personResponse = new PersonResponse();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setId(department.getId())
                .setName(department.getName());
        personResponse
                .setId(1)
                .setAge(2)
                .setDepartment(departmentInfo)
                .setFullName("Alex Alexov Alexeev");
        personResponses.add(personResponse);

        Mockito.when(departmentDao.update(any(Department.class)))
                .thenReturn(department);
        Mockito.when(personService.findAllPersons()).thenReturn(personResponses);

        DepartmentResponse departmentResponse = departmentService.updateDepartment(department.getId(), request);
        assertThat(department.getId())
                .isEqualTo(departmentResponse.getId());
        assertThat(department.getName())
                .isEqualTo(departmentResponse.getName());
        assertTrue(personResponses.size() == departmentResponse.getPersons().size());

        for(int i = 0; i < personResponses.size(); i++){
            assertThat(personResponses.get(i).getId())
                    .isEqualTo(departmentResponse.getPersons().get(i).getId());
            assertThat(personResponses.get(i).getFullName())
                    .isEqualTo(departmentResponse.getPersons().get(i).getFullName());
        }


    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void deleteDepartment(List<Department> departments) {
        Department department = departments.get(0);

        Mockito.when(departmentDao.delete(any(Integer.class)))
                .thenReturn(department);

        departmentService.deleteDepartment(1);
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void addPersonToDepartment(List<Department> departments) {
        Department department = departments.get(0);
        PersonResponse personResponse = new PersonResponse();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setId(department.getId())
                .setName(department.getName());
        personResponse
                .setId(1)
                .setAge(2)
                .setDepartment(departmentInfo)
                .setFullName("Alex Alexov Alexeev");
        PersonRequest personRequest = new PersonRequest();
        String[] fullName = personResponse.getFullName().split(" ");
        personRequest
                .setAge(personResponse.getAge())
                .setFirstName(fullName[0])
                .setMiddleName(fullName[1])
                .setLastName(fullName[2])
                .setDepartmentId(department.getId());

        Mockito.when(departmentDao.findById(department.getId())).thenReturn(department);
        Mockito.when(personService.getPerson(personResponse.getId()))
                .thenReturn(personResponse);
        Mockito.when(personService.updatePerson(any(Integer.class), any(PersonRequest.class)))
                .thenReturn(personResponse);
        departmentService.addPersonToDepartment(department.getId(),personResponse.getId());
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void removePersonToDepartment(List<Department> departments) {
        Department department = departments.get(0);
        PersonResponse personResponse = new PersonResponse();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setId(department.getId())
                .setName(department.getName());
        personResponse
                .setId(1)
                .setAge(2)
                .setDepartment(departmentInfo)
                .setFullName("Alex Alexov Alexeev");
        PersonRequest personRequest = new PersonRequest();
        String[] fullName = personResponse.getFullName().split(" ");
        personRequest
                .setAge(personResponse.getAge())
                .setFirstName(fullName[0])
                .setMiddleName(fullName[1])
                .setLastName(fullName[2])
                .setDepartmentId(department.getId());

        Mockito.when(departmentDao.findById(department.getId())).thenReturn(department);
        Mockito.when(personService.getPerson(personResponse.getId()))
                .thenReturn(personResponse);
        Mockito.when(personService.updatePerson(any(Integer.class), any(PersonRequest.class)))
                .thenReturn(personResponse.setDepartment(null));
        departmentService.removePersonToDepartment(department.getId(),personResponse.getId());
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void closeDepartment(List<Department> departments) {
        Department department = departments.get(0);
        List<PersonResponse> personResponses = new ArrayList<>();
        PersonResponse personResponse = new PersonResponse();
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo
                .setId(department.getId())
                .setName(department.getName());
        personResponse
                .setId(1)
                .setAge(2)
                .setDepartment(departmentInfo)
                .setFullName("Alex Alexov Alexeev");
        personResponses.add(personResponse);

        PersonRequest personRequest = new PersonRequest();
        String[] fullName = personResponse.getFullName().split(" ");
        personRequest
                .setAge(personResponse.getAge())
                .setFirstName(fullName[0])
                .setMiddleName(fullName[1])
                .setLastName(fullName[2])
                .setDepartmentId(null);

        Mockito.when(departmentDao.findById(department.getId())).thenReturn(department);
        Mockito.when(personService.findAllPersons())
                .thenReturn(personResponses);
        Mockito.when(personService.updatePerson(any(Integer.class), any(PersonRequest.class)))
                .thenReturn(personResponse.setDepartment(null));
        Mockito.when(departmentDao.update(any(Department.class)))
                .thenReturn(department);
        departmentService.closeDepartment(department.getId());
    }

    static Stream<List<Department>> testDataListDepartment(){
        List<Department> departments = new ArrayList<>();
        Department department1 = new Department();
        department1
                .setName("Department A")
                .setId(1)
                .setClosed(false);

        Department department2 = new Department();
        department2
                .setName("Department B")
                .setId(2)
                .setClosed(false);

        departments.add(department1);
        departments.add(department2);

        return Stream.of(departments);
    }
}