package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventDtoShort;

import java.util.List;

/**
 * Data transfer object for {@link ru.practicum.compilation.Compilation}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CompilationDto {
    private long id;
    private String title;
    private Boolean pinned;
    private List<EventDtoShort> events;
}
