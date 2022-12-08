package ru.practicum.area.dto;

import lombok.*;
import ru.practicum.area.AreaType;

/**
 * Data transfer object for {@link ru.practicum.area.Area}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AreaDtoFull {
    private long id;
    private Float lat;
    private Float lon;
    private String name;
    private Float radius;
    private AreaType type;
}
