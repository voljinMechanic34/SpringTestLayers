package ru.digitalhabbits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonResponse {
    private Integer id;
    private String fullName;
    private Integer age;
    private DepartmentInfo department;
}
