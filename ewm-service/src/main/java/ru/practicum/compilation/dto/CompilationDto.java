package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventDtoShort;

import java.util.List;

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
