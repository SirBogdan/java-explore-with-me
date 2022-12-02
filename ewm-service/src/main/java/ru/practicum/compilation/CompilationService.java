package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Compilation}
 */
public interface CompilationService {
    CompilationDto createCompilation(CompilationDtoNew compilationDtoNew);

    void deleteCompilation(long compId);

    void deleteCompilationsEvent(long compId, long eventId);

    void addCompilationsEvent(long compId, long eventId);

    void unpinCompilation(long compId);

    void pinCompilation(long compId);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);
}
