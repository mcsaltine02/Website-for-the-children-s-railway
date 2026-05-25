package by.mcsaltine.vkpost.controller;

import by.mcsaltine.vkpost.model.ScheduleDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/schedule")
public class ScheduleTrainController {
    private final List<ScheduleDto> schedule = Arrays.asList(
            new ScheduleDto("10:00", "10:05", "10:12", "10:15"),
            new ScheduleDto("10:22", "10:27", "10:34", "10:37"),
            new ScheduleDto("10:45", "10:50", "10:57", "11:00"),
            new ScheduleDto("11:07", "11:12", "11:19", "11:22"),
            new ScheduleDto("11:30", "11:35", "11:42", "11:45"),
            new ScheduleDto("11:52", "11:57", "12:04", "12:07"),

            new ScheduleDto("14:00", "14:05", "14:12", "14:15"),
            new ScheduleDto("14:22", "14:27", "14:32", "14:37"),
            new ScheduleDto("14:45", "14:50", "14:57", "15:00"),
            new ScheduleDto("15:07", "15:12", "15:19", "15:22"),
            new ScheduleDto("15:30", "15:35", "15:42", "15:45"),
            new ScheduleDto("15:52", "15:57", "16:04", "16:07"),

            // === Вечерние рейсы (много для тестирования) ===
            new ScheduleDto("16:15", "16:20", "16:27", "16:30"),
            new ScheduleDto("16:30", "16:35", "16:42", "16:45"),
            new ScheduleDto("16:45", "16:50", "16:57", "17:00"),
            new ScheduleDto("17:00", "17:05", "17:12", "17:15"),
            new ScheduleDto("17:15", "17:20", "17:27", "17:30"),
            new ScheduleDto("17:30", "17:35", "17:42", "17:45"),
            new ScheduleDto("17:45", "17:50", "17:57", "18:00"),
            new ScheduleDto("18:00", "18:05", "18:12", "18:15"),
            new ScheduleDto("18:15", "18:20", "18:27", "18:30"),
            new ScheduleDto("18:30", "18:35", "18:42", "18:45"),
            new ScheduleDto("18:45", "18:50", "18:57", "19:00"),
            new ScheduleDto("19:00", "19:05", "19:12", "19:15"),
            new ScheduleDto("19:15", "19:20", "19:27", "19:30"),
            new ScheduleDto("19:30", "19:35", "19:42", "19:45"),
            new ScheduleDto("19:45", "19:50", "19:57", "20:00")
    );

    @GetMapping("/current")
    @ResponseBody
    public Map<String, Object> getCurrentAndNext() {
        LocalTime now = LocalTime.now();
        int currentIndex = -1;

        // Поиск текущего рейса
        for (int i = 0; i < schedule.size(); i++) {
            ScheduleDto trip = schedule.get(i);
            LocalTime depTime = LocalTime.parse(trip.getDeparture());
            LocalTime returnTime = LocalTime.parse(trip.getReturnToSolar());

            if (now.isAfter(depTime) && now.isBefore(returnTime)) {
                currentIndex = i;
                break;
            }
        }

        // Проверка — закончился ли весь день
        boolean dayIsOver = false;
        if (currentIndex == -1 && !schedule.isEmpty()) {
            LocalTime lastReturn = LocalTime.parse(schedule.get(schedule.size() - 1).getReturnToSolar());
            if (now.isAfter(lastReturn)) {
                dayIsOver = true;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("currentIndex", currentIndex);
        response.put("dayIsOver", dayIsOver);

        if (currentIndex != -1) {
            ScheduleDto trip = schedule.get(currentIndex);
            response.put("currentTrip", trip);

            LocalTime dep = LocalTime.parse(trip.getDeparture());
            LocalTime viti = LocalTime.parse(trip.getMiddle());
            LocalTime pobeda = LocalTime.parse(trip.getArrival());

            String phase = "NONE";
            if (now.isAfter(dep) && now.isBefore(viti)) phase = "SOLAR";
            else if (now.isAfter(viti) && now.isBefore(pobeda)) phase = "VITI";
            else if (now.isAfter(pobeda)) phase = "POBEDA";

            response.put("phase", phase);
        } else {
            response.put("currentTrip", null);
            response.put("phase", null);
        }

        // === поиск следующего рейса ===
        List<ScheduleDto> nextTrips = new ArrayList<>();
        int startIndex = -1;

        if (currentIndex != -1) {
            startIndex = currentIndex + 1;
        } else {
            // Ищем первый рейс, который ещё не отправился
            for (int i = 0; i < schedule.size(); i++) {
                LocalTime depTime = LocalTime.parse(schedule.get(i).getDeparture());
                if (now.isBefore(depTime)) {
                    startIndex = i;
                    break;
                }
            }
        }

        if (startIndex != -1) {
            for (int i = startIndex; i < Math.min(startIndex + 1, schedule.size()); i++) {
                nextTrips.add(schedule.get(i));
            }
        }

        response.put("nextTrips", nextTrips);

        return response;
    }

    @GetMapping("/full")
    @ResponseBody
    public Map<String, Object> getFullSchedule() {
        LocalTime now = LocalTime.now();
        int currentIndex = -1;
        String currentPhase = "NONE";

        for (int i = 0; i < schedule.size(); i++) {
            ScheduleDto trip = schedule.get(i);
            LocalTime depTime = LocalTime.parse(trip.getDeparture());
            LocalTime returnTime = LocalTime.parse(trip.getReturnToSolar());

            if (now.isAfter(depTime) && now.isBefore(returnTime)) {
                currentIndex = i;

                LocalTime dep = LocalTime.parse(trip.getDeparture());
                LocalTime viti = LocalTime.parse(trip.getMiddle());
                LocalTime pobeda = LocalTime.parse(trip.getArrival());

                if (now.isAfter(dep) && now.isBefore(viti)) currentPhase = "SOLAR";
                else if (now.isAfter(viti) && now.isBefore(pobeda)) currentPhase = "VITI";
                else if (now.isAfter(pobeda)) currentPhase = "POBEDA";

                break;
            }
        }



        Map<String, Object> response = new HashMap<>();
        response.put("schedule", schedule);
        response.put("currentIndex", currentIndex);
        response.put("currentPhase", currentPhase);

        return response;
    }
}
