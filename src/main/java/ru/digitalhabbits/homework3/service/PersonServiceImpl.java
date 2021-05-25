package ru.digitalhabbits.homework3.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.*;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl
        implements PersonService {
    private final PersonDao personDaoImpl;
    private final DepartmentService departmentService;

    public PersonServiceImpl(PersonDao personDaoImpl, @Lazy DepartmentService departmentService) {
        this.personDaoImpl = personDaoImpl;
        this.departmentService = departmentService;
    }

    @Nonnull
    @Override
    public List<PersonResponse> findAllPersons() {
        return personDaoImpl.findAll().stream()
                .map(person -> builderPersonResponse(person))
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public PersonResponse getPerson(@Nonnull Integer id) {
        Person person = personDaoImpl.findById(id);
        if(person == null)
            throw new EntityNotFoundException("Person not found, id:" + id);
        PersonResponse personResponse = builderPersonResponse(person);
        return personResponse;
    }

    @Nonnull
    @Override
    public Integer createPerson(@Nonnull PersonRequest request) {
        Person person = builderPersonRequest(request);
        Person createdPerson = personDaoImpl.create(person);
        return createdPerson.getId();
    }

    @Nonnull
    @Override
    public PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request) {
        Person person = builderPersonRequest(request);
        person.setId(id);
        Person updatedPerson = personDaoImpl.update(person);

        if(updatedPerson == null)
            throw new EntityNotFoundException("Person not found, id:" + id);
        return builderPersonResponse(updatedPerson);
    }

    @Override
    public void deletePerson(@Nonnull Integer id) {
        personDaoImpl.delete(id);
    }

    private PersonResponse builderPersonResponse(Person person){

        DepartmentInfo departmentInfo = null;;
        if(person.getDeparmentId() != null){
            int departmentId = person.getDeparmentId();
            //DepartmentResponse department = departmentService.getDepartment(person.getDeparmentId());
            departmentInfo = new DepartmentInfo();
            departmentInfo.setId(person.getDeparmentId());
        }

        PersonResponse personResponse = new PersonResponse()
                .setId(person.getId())
                .setAge(person.getAge())
                .setFullName(person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName())
                .setDepartment(departmentInfo);
        return personResponse;
    }

    private Person builderPersonRequest(PersonRequest request){
        Person person = new Person();
        person
            .setDeparmentId(request.getDepartmentId())
            .setAge(request.getAge())
            .setFirstName(request.getFirstName())
            .setMiddleName(request.getMiddleName())
            .setLastName(request.getLastName());
        return person;
    }
}
