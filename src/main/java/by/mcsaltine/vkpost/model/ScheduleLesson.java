package by.mcsaltine.vkpost.model;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)   // делает геттеры без "get" → id(), username()
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ScheduleLesson{
        String day;
        String cabinet;
        String teacher;
        String time;
        String group;
}
