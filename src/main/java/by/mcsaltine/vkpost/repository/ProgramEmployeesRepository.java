package by.mcsaltine.vkpost.repository;

import by.mcsaltine.vkpost.model.Employee;
import by.mcsaltine.vkpost.model.ProgramEmployees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgramEmployeesRepository extends JpaRepository<ProgramEmployees, Integer> {

//    void deleteAllByEmployee(Employee employee);

    @Modifying
    @Query("DELETE FROM ProgramEmployees pe WHERE pe.employee.id = :eId")
    void deleteAllByEmployeeId(@Param("eId") Integer eId);
}
