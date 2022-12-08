package ru.practicum.area.dto;

import lombok.*;
import ru.practicum.area.AreaType;

import javax.validation.constraints.NotNull;

/**
 * Data transfer object for {@link ru.practicum.area.Area}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AreaDtoUpdate {
    @NotNull
    private long id;
    private String name;
    private Float radius;
    private AreaType type;
}
