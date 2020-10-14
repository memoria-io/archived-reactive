package io.memoria.jutils.adapter.yaml;

import io.memoria.jutils.core.dto.DTO;
import io.vavr.control.Try;

public class EmployeeDTO implements DTO<Employee> {
  public EmployeeDTO() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String name;

  EmployeeDTO(Employee emp) {
    this.name = emp.name();
  }

  @Override
  public Try<Employee> get() {
    return Try.success(new Employee(name));
  }
}
