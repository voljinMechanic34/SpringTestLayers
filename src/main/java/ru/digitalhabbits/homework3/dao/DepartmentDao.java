package ru.digitalhabbits.homework3.dao;

import org.springframework.data.repository.NoRepositoryBean;
import ru.digitalhabbits.homework3.domain.Department;

@NoRepositoryBean
public interface DepartmentDao
        extends CrudOperations<Department, Integer> {

}
