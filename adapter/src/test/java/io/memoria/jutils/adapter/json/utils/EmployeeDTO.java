package io.memoria.jutils.adapter.json.utils;

import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.memoria.jutils.core.dto.DTO;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.json.JsonException.notFoundType;

public final class EmployeeDTO implements DTO<Employee> {

  public static class EngineerDTO implements DTO<Engineer> {
    private final String name;
    private final List<String> tasks;

    public EngineerDTO(Engineer engineer) {
      this.name = engineer.name();
      this.tasks = engineer.tasks();
    }

    @Override
    public Try<Engineer> get() {
      return Try.of(() -> new Engineer(this.name, this.tasks));
    }
  }

  public static class ManagerDTO implements DTO<Manager> {
    private final String name;
    private final List<EngineerDTO> team;

    public ManagerDTO(Manager manager) {
      this.name = manager.name();
      this.team = manager.team().map(EngineerDTO::new);
    }

    @Override
    public Try<Manager> get() {
      return Try.of(() -> new Manager(name, team.flatMap(EngineerDTO::get)));
    }
  }

  private EngineerDTO Engineer;
  private ManagerDTO Manager;

  public EmployeeDTO(Employee employee) {
    if (employee instanceof Engineer engineer)
      this.Engineer = new EngineerDTO(engineer);
    if (employee instanceof Manager manager)
      this.Manager = new ManagerDTO(manager);
  }

  @Override
  public Try<Employee> get() {
    return DTO.firstNonNull(List.of(this.Engineer, this.Manager), notFoundType(Manager.class, Engineer.class));
  }
}
