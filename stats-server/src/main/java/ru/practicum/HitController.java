package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitDtoCreate;
import ru.practicum.dto.ViewStats;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public HitDto createHit(@RequestBody @Validated HitDtoCreate hitDtoCreate) {
        HitDto hitDto = hitService.createHit(hitDtoCreate);
        log.info("Сохранение информации о запросу к эндпоинту: {}", hitDto);
        return hitDto;
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        List<ViewStats> viewStatsList = hitService.getStats(start, end, uris, unique);
        log.info("Получена статистика: {}", viewStatsList);
        return viewStatsList;
    }
}
