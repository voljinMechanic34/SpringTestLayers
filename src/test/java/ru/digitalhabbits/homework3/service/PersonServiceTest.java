package ru.digitalhabbits.homework3.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PersonServiceImpl.class)
class PersonServiceTest {

    @MockBean
    private PersonDao personDao;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private PersonService personService;

    @ParameterizedTest
    @MethodSource({"testDataListPerson"})
    void findAllPersons(List<Pair<Person,DepartmentResponse>> pairList) {
        List<Person> personList = new ArrayList<>();
        pairList.forEach(pair -> {
            Person person = pair.getKey();
            DepartmentResponse departmentResponse = pair.getValue();
            personList.add(person);
            Mockito.when(departmentService.getDepartment(person.getDeparmentId()))
                    .thenReturn(departmentResponse);
        });

        Mockito.when(personDao.findAll())
                .thenReturn(personList);

        List<PersonResponse> personResponses = personService.findAllPersons();
        personResponses.sort(Comparator.comparingInt(o -> o.getId()));

        assertTrue(personResponses.size() == (pairList.size()));
        for(int i = 0 ; i < personResponses.size(); i ++) {
            Person person = pairList.get(i).getKey();
            assertThat(person.getId())
                    .isEqualTo(personResponses.get(i).getId());
            assertThat(person.getAge())
                    .isEqualTo(personResponses.get(i).getAge());
            assertThat(person.getDeparmentId())
                    .isEqualTo(personResponses.get(i).getDepartment().getId());
            assertThat(person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName())
                    .isEqualTo(personResponses.get(i).getFullName());
        }
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void getPerson(Pair<Person,DepartmentResponse> departmentResponsePair) {
        Person person = departmentResponsePair.getKey();
        DepartmentResponse departmentResponse = departmentResponsePair.getValue();

        Mockito.when(personDao.findById(person.getId()))
                .thenReturn(person);
        Mockito.when(departmentService.getDepartment(person.getDeparmentId()))
                .thenReturn(departmentResponse);

        PersonResponse servicePerson = personService.getPerson(person.getId());

        assertThat(person.getId())
                .isEqualTo(servicePerson.getId());
        assertThat(person.getAge())
                .isEqualTo(servicePerson.getAge());
        assertThat(person.getDeparmentId())
                .isEqualTo(servicePerson.getDepartment().getId());
        assertThat(person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName())
                .isEqualTo(servicePerson.getFullName());
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void createPerson(Pair<Person,DepartmentResponse> departmentResponsePair) {
        Person person = departmentResponsePair.getKey();
        DepartmentResponse departmentResponse = departmentResponsePair.getValue();
        PersonRequest request = new PersonRequest();
        request
                .setAge(person.getAge())
                .setLastName(person.getLastName())
                .setMiddleName(person.getMiddleName())
                .setFirstName(person.getFirstName());

        Mockito.when(personDao.create(any(Person.class)))
                .thenReturn(person);
        /*Mockito.when(personDao.create(person))
                .thenReturn(person);*/ //TODO -> НЕ РАБОТАЕТ

        Integer personCreatedId = personService.createPerson(request);
        assertThat(personCreatedId).isNotNull();
        assertThat(personCreatedId).isEqualTo(person.getId());
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void updatePerson(Pair<Person,DepartmentResponse> departmentResponsePair) {
        Person person = departmentResponsePair.getKey();
        DepartmentResponse departmentResponse = departmentResponsePair.getValue();
        PersonRequest request = new PersonRequest();
        request
                .setAge(person.getAge())
                .setLastName(person.getLastName())
                .setMiddleName(person.getMiddleName())
                .setFirstName(person.getFirstName());

        Mockito.when(personDao.update(any(Person.class)))
                .thenReturn(person);
        Mockito.when(departmentService.getDepartment(person.getDeparmentId()))
                .thenReturn(departmentResponse);
        /*Mockito.when(personDao.create(person))
                .thenReturn(person);*/ // -> НЕ РАБОТАЕТ

        PersonResponse updatedPerson = personService.updatePerson(19, request);

        assertThat(person.getId())
                .isEqualTo(updatedPerson.getId());
        assertThat(person.getDeparmentId())
                .isEqualTo(updatedPerson.getDepartment().getId());
        assertThat(person.getAge())
                .isEqualTo(updatedPerson.getAge());
        assertThat(person.getDeparmentId())
                .isEqualTo(updatedPerson.getDepartment().getId());
        assertThat(person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName())
                .isEqualTo(updatedPerson.getFullName());
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void deletePerson(Pair<Person,DepartmentResponse> departmentResponsePair) {
        Person person = departmentResponsePair.getKey();
        Mockito.when(personDao.delete(any(Integer.class)))
                .thenReturn(person);
        personService.deletePerson(1);
    }

    static Stream<Pair<Person,DepartmentResponse>> testDataPerson(){
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(1);
        departmentResponse.setName("Department B");

        Person person = new Person();
        person.setId(1);
        person.setFirstName("hey");
        person.setMiddleName("hey2");
        person.setLastName("hey3");
        person.setAge(3);
        person.setDeparmentId(departmentResponse.getId());

        Pair<Person,DepartmentResponse> pair = Pair.of(person,departmentResponse);

        return Stream.of(pair);
    }

    static Stream<List<Pair<Person,DepartmentResponse>>> testDataListPerson(){
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(1);
        departmentResponse.setName("Department B");

        Person person = new Person();
        person.setId(1);
        person.setFirstName("hey");
        person.setMiddleName("hey2");
        person.setLastName("hey3");
        person.setAge(3);
        person.setDeparmentId(departmentResponse.getId());

        Pair<Person,DepartmentResponse> pair = Pair.of(person,departmentResponse);

        DepartmentResponse departmentResponse1 = new DepartmentResponse();
        departmentResponse1.setId(2);
        departmentResponse1.setName("Department CS");

        Person person1 = new Person();
        person1.setId(2);
        person1.setFirstName("hey");
        person1.setMiddleName("hey2");
        person1.setLastName("hey3");
        person1.setAge(4);
        person1.setDeparmentId(departmentResponse1.getId());

        Pair<Person,DepartmentResponse> pair1 = Pair.of(person1, departmentResponse1);

        List<Pair<Person,DepartmentResponse>> pairs = new ArrayList<>();
        pairs.add(pair);
        pairs.add(pair1);

        return Stream.of(pairs);
    }
}