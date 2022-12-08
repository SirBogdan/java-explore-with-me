package ru.practicum.area;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.area.dto.AreaDtoFull;
import ru.practicum.area.dto.AreaDtoNew;
import ru.practicum.area.dto.AreaDtoUpdate;

/**
 * Controller to Admin endpoints, linked to {@link Area}
 */
@RestController
@RequestMapping(path = "/admin/areas")
@Slf4j
public class AreaAdminController {

    private final AreaService areaService;

    @Autowired
    public AreaAdminController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    public AreaDtoFull createArea(@RequestBody @Validated AreaDtoNew areaDtoNew) {
        AreaDtoFull areaDtoFull = areaService.createArea(areaDtoNew);
        log.info("Создана территория {}", areaDtoFull);
        return areaDtoFull;
    }

    @PatchMapping
    public AreaDtoFull updateArea(@RequestBody @Validated AreaDtoUpdate areaDtoUpdate) {
        AreaDtoFull areaDtoFullUpdated = areaService.updateArea(areaDtoUpdate);
        log.info("Обновлена территория {}", areaDtoFullUpdated);
        return areaDtoFullUpdated;
    }
}
