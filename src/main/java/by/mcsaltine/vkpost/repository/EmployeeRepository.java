package by.mcsaltine.vkpost.repository;

import by.mcsaltine.vkpost.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

}
