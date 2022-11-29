package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.event.Event;
import ru.practicum.event.dto.EventDtoShort;

import java.util.List;

public class CompilationMapper {
/*    public Compilation fromCompilationDto(CompilationDto compilationDto) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .events()
                .build();
    }*/

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
