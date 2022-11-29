package ru.practicum.compilation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Validated CompilationDtoNew compilationDtoNew) {
        CompilationDto compilationDto = compilationService.createCompilation(compilationDtoNew);
        log.info("Создана подборка {}", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable("compId") long compId) {
        log.info("Удаление поборки с id {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteCompilationsEvent(@PathVariable("compId") long compId, @PathVariable("eventId") long eventId) {
        log.info("Удаление события с id {} из подборки с id{}", eventId, compId);
        compilationService.deleteCompilationsEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addCompilationsEvent(@PathVariable("compId") long compId, @PathVariable("eventId") long eventId) {
        log.info("Добавление события с id {} в подборку с id{}", eventId, compId);
        compilationService.addCompilationsEvent(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable("compId") long compId) {
        compilationService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable("compId") long compId) {
        compilationService.pinCompilation(compId);
    }
}
