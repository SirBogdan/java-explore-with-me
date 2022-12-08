package ru.practicum.area;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.area.dto.AreaDtoFull;

import java.util.List;

/**
 * Controller to Public endpoints, linked to {@link Area}
 */
@RestControllerAdvice
@RequestMapping(path = "/areas")
@RequiredArgsConstructor
@Slf4j
public class AreaPublicController {

    private final AreaService areaService;

    @GetMapping
    public List<AreaDtoFull> getAllAreas(
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        List<AreaDtoFull> areaDtoFulls = areaService.getAllAreas(from, size);
        log.info("Запрошен список всех территорий {}", areaDtoFulls);
        return areaDtoFulls;
    }

    @GetMapping("/{locId}")
    public AreaDtoFull getAreaById(@PathVariable long locId) {
        AreaDtoFull areaDtoFull = areaService.getAreaById(locId);
        log.info("Запрошена территория {}", areaDtoFull);
        return areaDtoFull;
    }
}
