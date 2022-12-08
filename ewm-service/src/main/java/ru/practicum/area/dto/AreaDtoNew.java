package ru.practicum.area.dto;

import lombok.*;
import ru.practicum.area.AreaType;

import javax.validation.constraints.NotNull;

/**
 * Data transfer object for {@link ru.practicum.area.Area}
 *
 * @see ru.practicum.area.AreaServiceImpl#createArea(AreaDtoNew)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AreaDtoNew {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
    @NotNull
    private String name;
    @NotNull
    private Float radius;
    @NotNull
    private AreaType type;
}
