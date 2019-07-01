package com.total.unique.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Table(name = "unique_id_table")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueID {

    @Column(unique = true, length = 10)
    @Size(min = 10, max = 10)
    @Id
    private String uniqueId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT;

    public UniqueID(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public enum Status {
        NOT,PROCESSED
    }
}
