package ru.digitalhabbits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "department")
public class Department {

    @Id
    @SequenceGenerator(name="pk_department_sequence",sequenceName="department_sequence", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pk_department_sequence")
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(nullable = false, length = 80, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "BOOL NOT NULL DEFAULT FALSE")
    private boolean closed;
}