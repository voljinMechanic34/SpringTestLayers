package ru.digitalhabbits.homework3.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Person;
import static org.hamcrest.CoreMatchers.is;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;


@ExtendWith(SpringExtension.class)
@Transactional()
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonDao personDao;

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void findById(Person person) {
        entityManager.persist(person);
        entityManager.flush();

        Person foundPerson = personDao.findById(person.getId());

        assertThat(person.getId())
                .isEqualTo(foundPerson.getId());
        assertThat(person.getFirstName())
                .isEqualTo(foundPerson.getFirstName());
        assertThat(person.getMiddleName())
                .isEqualTo(foundPerson.getMiddleName());
        assertThat(person.getLastName())
                .isEqualTo(foundPerson.getLastName());
        assertThat(person.getAge())
                .isEqualTo(foundPerson.getAge());
    }

    @ParameterizedTest
    @MethodSource("testDataListPerson")
    void findAll(List<Person> personList) {
        personList.forEach(person -> entityManager.persist(person));
        entityManager.flush();

        List<Person> foundPersonList = personDao.findAll();
        foundPersonList.sort(Comparator.comparingInt(o -> o.getId()));
        assertTrue(foundPersonList.size() == (personList.size()));
        for(int i = 0 ; i < personList.size(); i ++){
            assertThat(personList.get(i).getId())
                    .isEqualTo(foundPersonList.get(i).getId());
            assertThat(personList.get(i).getAge())
                    .isEqualTo(foundPersonList.get(i).getAge());
            assertThat(personList.get(i).getFirstName())
                    .isEqualTo(foundPersonList.get(i).getFirstName());
            assertThat(personList.get(i).getMiddleName())
                    .isEqualTo(foundPersonList.get(i).getMiddleName());
            assertThat(personList.get(i).getLastName())
                    .isEqualTo(foundPersonList.get(i).getLastName());
        }
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void update(Person person) {
        entityManager.persist(person);
        entityManager.flush();

        person.setAge(-1);
        Person foundPerson = personDao.update(person);

        assertThat(person.getId())
                .isEqualTo(foundPerson.getId());
        assertThat(person.getFirstName())
                .isEqualTo(foundPerson.getFirstName());
        assertThat(person.getMiddleName())
                .isEqualTo(foundPerson.getMiddleName());
        assertThat(person.getLastName())
                .isEqualTo(foundPerson.getLastName());
        assertThat(person.getAge())
                .isEqualTo(foundPerson.getAge());
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void delete(Person person) {
        entityManager.persist(person);
        entityManager.flush();

        Person deletedPerson = personDao.delete(person.getId());
        assertThat(person.getId())
                .isEqualTo(deletedPerson.getId());
    }

    @ParameterizedTest
    @MethodSource("testDataPerson")
    void create(Person person) {
        entityManager.persist(person);
        entityManager.flush();

        Person createdPerson = personDao.create(person);

        assertThat(person.getId())
                .isEqualTo(createdPerson.getId());
        assertThat(person.getFirstName())
                .isEqualTo(createdPerson.getFirstName());
        assertThat(person.getMiddleName())
                .isEqualTo(createdPerson.getMiddleName());
        assertThat(person.getLastName())
                .isEqualTo(createdPerson.getLastName());
        assertThat(person.getAge())
                .isEqualTo(createdPerson.getAge());
    }

    static Stream<Person> testDataPerson(){
        Person person = new Person();
        //person.setId(1);
        person.setFirstName("hey");
        person.setMiddleName("hey2");
        person.setLastName("hey3");
        person.setAge(3);
        return Stream.of(person);
    }

    static Stream<List<Person>> testDataListPerson(){
        Person person = new Person();
        //person.setId(1);
        person.setFirstName("hey");
        person.setMiddleName("hey2");
        person.setLastName("hey3");
        person.setAge(3);

        Person person2 = new Person();
        //person2.setId(2);
        person2.setFirstName("miwe");
        person2.setMiddleName("miwe2");
        person2.setLastName("miwe3");
        person2.setAge(3);

        Person person3 = new Person();
        //person3.setId(3);
        person3.setFirstName("mi");
        person3.setMiddleName("mi2");
        person3.setLastName("mi3");
        person3.setAge(3);

        List<Person> personList = new ArrayList<>();
        personList.add(person);
        personList.add(person2);
        personList.add(person3);

        return Stream.of(personList);
    }


}