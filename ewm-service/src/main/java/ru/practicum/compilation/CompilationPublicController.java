package ru.practicum.compilation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

/**
 * Controller to Public endpoints, linked to {@link Compilation}
 */
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(name = "pinned", defaultValue = "false", required = false) boolean pinned,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        List<CompilationDto> compilationDtoList = compilationService.getCompilations(pinned, from, size);
        log.info("Запрошены подборки событий {}", compilationDtoList);
        return compilationDtoList;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationsById(@PathVariable("compId") long compId) {
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        log.info("Запрошена подборка событий {}", compilationDto);
        return compilationDto;
    }
}
