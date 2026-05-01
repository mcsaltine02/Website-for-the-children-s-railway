package by.mcsaltine.vkpost.repository;

import by.mcsaltine.vkpost.model.TaughtProgram;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TaughtProgramRepository extends CrudRepository<TaughtProgram, Long> {

    @Modifying
    @Query(value = "UPDATE taught_program SET vacant_places = ?1 WHERE tp_id = ?2", nativeQuery = true)
    void update(Integer vacantPlaces, Integer tpId);


}
