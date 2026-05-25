package by.mcsaltine.vkpost.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDto {
    private String departure;      // Солнечная (отправление)
    private String middle;         // Вити Черевичкина
    private String arrival;        // Победа
    private String returnToSolar;  // Прибытие обратно на Солнечную

    public ScheduleDto(String dep, String mid, String arr, String ret) {
        this.departure = dep;
        this.middle = mid;
        this.arrival = arr;
        this.returnToSolar = ret;
    }
}