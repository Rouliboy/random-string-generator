package com.total.unique.generator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "UNIQUE_ID")
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

  public UniqueID(final String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public enum Status {
    NOT, PROCESSED

  }
}
