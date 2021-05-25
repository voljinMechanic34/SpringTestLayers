package ru.digitalhabbits.homework3.service;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.model.*;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl
        implements DepartmentService {
    private final DepartmentDao departmentDao;
    private final PersonService personService;

    public DepartmentServiceImpl(DepartmentDao departmentDao, @Lazy PersonService personService) {
        this.departmentDao = departmentDao;
        this.personService = personService;
    }

    @Nonnull
    @Override
    public List<DepartmentShortResponse> findAllDepartments() {
        return departmentDao.findAll().stream()
                .map(department -> new DepartmentShortResponse()
                         .setId(department.getId())
                         .setName(department.getName())
                )
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public DepartmentResponse getDepartment(@Nonnull Integer id) {
        //  Если не найдено, отдавать 404:NotFound
        Department department = departmentDao.findById(id);
        if(department == null)
            throw new EntityNotFoundException("Department not found, id:" + id);
        return buildDepartmentResponse(department);

    }

    @Nonnull
    @Override
    public Integer createDepartment(@Nonnull DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.getName());
        return departmentDao.create(department).getId();
    }

    @Nonnull
    @Override
    public DepartmentResponse updateDepartment(@Nonnull Integer id, @Nonnull DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.getName());
        department.setId(id);
        Department updatedDepartment = departmentDao.update(department);
        if(updatedDepartment == null)
            throw new EntityNotFoundException("Department not found, id:" + id);
       return buildDepartmentResponse(department);
    }

    @Override
    public void deleteDepartment(@Nonnull Integer id) {
        departmentDao.delete(id);
    }

    @Override
    public void addPersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        Department department = departmentDao.findById(departmentId);
        if(department == null){
            throw new EntityNotFoundException("Department not found, id:" + departmentId);
        }
        if(department.isClosed())
            throw new HttpClientErrorException(HttpStatus.CONFLICT, "Department is closed");

        PersonResponse person = personService.getPerson(personId);
        PersonRequest personRequest = new PersonRequest();
        String[] fullName = person.getFullName().split(" ");
        personRequest
                .setAge(person.getAge())
                .setFirstName(fullName[0])
                .setMiddleName(fullName[1])
                .setLastName(fullName[2])
                .setDepartmentId(departmentId);
       personService.updatePerson(personId, personRequest);
    }

    @Override
    public void removePersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        Department department = departmentDao.findById(departmentId);
        if(department == null){
            throw new EntityNotFoundException("Department not found, id:" + departmentId);
        }
        PersonResponse person = personService.getPerson(personId);
        PersonRequest personRequest = new PersonRequest();
        String[] fullName = person.getFullName().split(" ");
        personRequest
                .setAge(person.getAge())
                .setFirstName(fullName[0])
                .setMiddleName(fullName[1])
                .setLastName(fullName[2])
                .setDepartmentId(null);
        personService.updatePerson(personId, personRequest);
    }

    @Override
    public void closeDepartment(@Nonnull Integer id) {
        // удаление всех людей из департамента и установка отметки на департаменте,
        //  что он закрыт для добавления новых людей. Если не найдено, отдавать 404:NotFound
        Department department = departmentDao.findById(id);
        if(department == null){
            throw new EntityNotFoundException("Department not found, id:" + id);
        }
        List<PersonResponse> personInfoList = personService.findAllPersons()
                .stream()
                .filter(personResponse -> personResponse.getDepartment()!= null && personResponse.getDepartment().getId() != null)
                .filter(personResponse -> personResponse.getDepartment().getId().equals(department.getId()))
                .collect(Collectors.toList());

        personInfoList.forEach(person -> {
            PersonRequest personRequest = new PersonRequest();
            String[] fullName = person.getFullName().split(" ");
            personRequest
                    .setAge(person.getAge())
                    .setFirstName(fullName[0])
                    .setMiddleName(fullName[1])
                    .setLastName(fullName[2])
                    .setDepartmentId(null);
            personService.updatePerson(person.getId(), personRequest);
        });
        department.setClosed(true);
        departmentDao.update(department);
    }

    private DepartmentResponse buildDepartmentResponse(Department department){
        DepartmentResponse response = new DepartmentResponse();
        List<PersonInfo> personInfoList = personService.findAllPersons()
                .stream()
                .filter(personResponse -> personResponse.getDepartment() != null &&  personResponse.getDepartment().getId() != null)
                .filter(personResponse -> personResponse.getDepartment().getId().equals(department.getId()))
                .map(personResponse ->
                        new PersonInfo()
                        .setId(personResponse.getId())
                        .setFullName(personResponse.getFullName()))
                .collect(Collectors.toList());

        response
                .setId(department.getId())
                .setClosed(department.isClosed())
                .setName(department.getName())
                .setPersons(personInfoList);
        return response;
    }
}
