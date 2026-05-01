package by.mcsaltine.vkpost.service;

import by.mcsaltine.vkpost.controller.main_info_about_organization.payload.VacantPlacesDto;
import by.mcsaltine.vkpost.model.TaughtProgram;
import by.mcsaltine.vkpost.repository.TaughtProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaughtProgramService {

    public final TaughtProgramRepository taughtProgramRepository;

    @Transactional
    public void updateVacantPlaces(Integer tpId, Integer vacantPlaces) {
        taughtProgramRepository.update(vacantPlaces, tpId);
    }

}
