package io.memoria.jutils.adapter.json.utils;

import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.memoria.jutils.adapter.json.utils.Employee.Manager;
import io.memoria.jutils.core.dto.DTO;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.dto.DTO.nullProperties;
import static java.util.function.Function.identity;

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

  private EngineerDTO Engineer = null;
  private ManagerDTO Manager = null;

  public EmployeeDTO(EngineerDTO engineer) {
    this.Engineer = engineer;
  }

  public EmployeeDTO(ManagerDTO manager) {
    this.Manager = manager;
  }

  @Override
  public Try<Employee> get() {
    if (Engineer != null)
      return Engineer.get().map(identity());
    if (Manager != null)
      return Manager.get().map(identity());
    return Try.failure(nullProperties(Engineer.class.getSimpleName(), Manager.class.getSimpleName()));
  }
}
