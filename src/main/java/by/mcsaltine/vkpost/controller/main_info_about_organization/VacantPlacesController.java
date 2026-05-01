package by.mcsaltine.vkpost.controller.main_info_about_organization;

import by.mcsaltine.vkpost.controller.main_info_about_organization.payload.VacantPlacesDto;
import by.mcsaltine.vkpost.repository.TaughtProgramRepository;
import by.mcsaltine.vkpost.service.TaughtProgramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/main-info-about-organization/vacant-places")
public class VacantPlacesController {
    private final TaughtProgramRepository taughtProgramRepository;
    private final TaughtProgramService taughtProgramService;

    @GetMapping
    public String vacantPlaces(Model model) {
        model.addAttribute("taughtPrograms", taughtProgramRepository.findAll());
        return "main_info_about_organization/vacant-places";
    }

    @PostMapping
    public String vacantPlaces(VacantPlacesDto vacantPlacesDto) {
        System.out.println(vacantPlacesDto.toString());
        taughtProgramService.updateVacantPlaces(vacantPlacesDto.tpId(),  vacantPlacesDto.vacantPlaces());
        return "redirect:/main-info-about-organization/vacant-places";
    }
}
