package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.event.Event;
import ru.practicum.event.dto.EventDtoShort;

import java.util.List;

/**
 * Mapper to/from DTO, linked to {@link Compilation}
 */
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventDtoShort> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }

    public static Compilation fromCompilationDtoNew(CompilationDtoNew compilationDtoNew, List<Event> events) {
        return Compilation.builder()
                .title(compilationDtoNew.getTitle())
                .pinned(compilationDtoNew.getPinned())
                .events(events)
                .build();
    }
}
