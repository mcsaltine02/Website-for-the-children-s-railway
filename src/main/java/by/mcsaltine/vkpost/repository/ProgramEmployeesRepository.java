package by.mcsaltine.vkpost.repository;

import by.mcsaltine.vkpost.model.Employee;
import by.mcsaltine.vkpost.model.ProgramEmployees;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramEmployeesRepository extends JpaRepository<ProgramEmployees, Integer> {

    void deleteAllByEmployee(Employee employee);
}
