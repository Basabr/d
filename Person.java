package social;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Person {

  @Id
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  public Person() {
    // Default constructor needed by JPA
  }

  public Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public String toString() {
    return code + " " + name + " " + surname;
  }
}
