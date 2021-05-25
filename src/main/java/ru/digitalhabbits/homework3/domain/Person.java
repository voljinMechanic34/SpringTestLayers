package ru.digitalhabbits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "person")
public class Person {

    @Id
    @SequenceGenerator(name="pk_person_sequence",sequenceName="person_sequence", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pk_person_sequence")
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(length = 80)
    private String middleName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column
    private Integer age;

    @Column(name = "departmend_id")
    private Integer deparmentId;
}
