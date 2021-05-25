package ru.digitalhabbits.homework3.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PersonDaoImpl
        implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Person findById(@Nonnull Integer id) {
        return entityManager.find(Person.class, id);
    }

    @Override
    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p").getResultList();
    }

    @Override
    @Transactional
    public Person update(Person entity) {
        Integer id = entity.getId();
        Person person = entityManager.find(Person.class, id);
        person.setAge(entity.getAge());
        person.setFirstName(entity.getFirstName());
        person.setLastName(entity.getLastName());
        person.setMiddleName(entity.getMiddleName());
        person.setDeparmentId(entity.getDeparmentId());

        return  person;
    }

    @Override
    @Transactional
    public Person create(Person entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public Person delete(Integer integer) {
        Person person = entityManager.find(Person.class, integer);
        entityManager.remove(person);
        return  person;
    }
}
