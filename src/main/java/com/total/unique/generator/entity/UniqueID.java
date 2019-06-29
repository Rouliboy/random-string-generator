package com.total.unique.generator.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Table(name = "UNIQUE_ID")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueID {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @Size(min = 10, max = 10)
    private String uniqueId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT;

    public UniqueID(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public enum Status {
        NOT,PROCESSED;

    }
}
