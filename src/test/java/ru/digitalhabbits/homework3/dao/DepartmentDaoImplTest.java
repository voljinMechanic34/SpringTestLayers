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
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DepartmentDaoImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentDao departmentDao;

    @ParameterizedTest
    @MethodSource("testDataDepartment")
    void findById(Department department) {
        entityManager.persist(department);
        entityManager.flush();

        Department departmentFound = departmentDao.findById(department.getId());

        assertThat(department.getId())
                .isEqualTo(departmentFound.getId());
        assertThat(department.isClosed())
                .isEqualTo(departmentFound.isClosed());
        assertThat(department.getName())
                .isEqualTo(departmentFound.getName());
    }

    @ParameterizedTest
    @MethodSource("testDataListDepartment")
    void findAll(List<Department> departments) {
        departments.forEach(department -> entityManager.persist(department));
        entityManager.flush();

        List<Department> deparmentsFound = departmentDao.findAll();
        deparmentsFound.sort(Comparator.comparingInt(o -> o.getId()));

        assertTrue(deparmentsFound.size() == (departments.size()));
        for(int i = 0 ; i < departments.size(); i ++){
            assertThat(departments.get(i).getId())
                    .isEqualTo(deparmentsFound.get(i).getId());
            assertThat(departments.get(i).getName())
                    .isEqualTo(deparmentsFound.get(i).getName());
            assertThat(departments.get(i).isClosed())
                    .isEqualTo(deparmentsFound.get(i).isClosed());
        }
    }

    @ParameterizedTest
    @MethodSource("testDataDepartment")
    void update(Department department) {
        entityManager.persist(department);
        entityManager.flush();

        department.setName("Department hello");
        Department updatedDepartment = departmentDao.update(department);

        assertThat(department.getId())
                .isEqualTo(updatedDepartment.getId());
        assertThat(department.isClosed())
                .isEqualTo(updatedDepartment.isClosed());
        assertThat(department.getName())
                .isEqualTo(updatedDepartment.getName());
    }

    @ParameterizedTest
    @MethodSource("testDataDepartment")
    void delete(Department department) {
        entityManager.persist(department);
        entityManager.flush();

        Department deletedFound = departmentDao.delete(department.getId());

        assertThat(department.getId())
                .isEqualTo(deletedFound.getId());
    }


    @ParameterizedTest
    @MethodSource("testDataDepartment")
    void create(Department department) {
        entityManager.persist(department);
        entityManager.flush();

        Department createdDepartment = departmentDao.create(department);

        assertThat(department.getId())
                .isEqualTo(createdDepartment.getId());
        assertThat(department.isClosed())
                .isEqualTo(createdDepartment.isClosed());
        assertThat(department.getName())
                .isEqualTo(createdDepartment.getName());
    }

    static Stream<Department> testDataDepartment(){
        Department department = new Department();
        //department.setId(1);
        department.setClosed(false);
        department.setName("department B");
        return Stream.of(department);
    }

    static Stream<List<Department>> testDataListDepartment(){
        Department department = new Department();
        //department.setId(1);
        department.setClosed(false);
        department.setName("department B");

        Department department1 = new Department();
        //department1.setId(2);
        department1.setClosed(false);
        department1.setName("department A");

        Department department2 = new Department();
        //department2.setId(3);
        department2.setClosed(false);
        department2.setName("department C");

        List<Department> departments = new ArrayList<>();
        departments.add(department);
        departments.add(department1);
        departments.add(department2);

        return Stream.of(departments);
    }
}