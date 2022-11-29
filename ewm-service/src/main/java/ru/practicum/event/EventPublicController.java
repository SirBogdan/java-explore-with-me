package ru.practicum.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoShort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {

    private final EventService eventService;

    @GetMapping("/events/{id}")
    public EventDtoFull getEventById(@PathVariable("id") long id, HttpServletRequest request) {
        EventDtoFull eventDtoFull = eventService.getEventById(id, request.getRemoteAddr());
        log.info("Получение подробной информации об опубликованном событии {}", eventDtoFull);
        return eventDtoFull;
    }

    @GetMapping("/events")
    public List<EventDtoShort> getEventsFiltered(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categoriesIds,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
        return eventService.getEventsFiltered(text, categoriesIds, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request.getRemoteAddr());
    }
}
