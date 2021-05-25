package ru.digitalhabbits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DepartmentResponse {
    private Integer id;
    private String name;
    private boolean closed;
    private List<PersonInfo> persons;
}
