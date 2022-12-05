package ru.practicum.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoUpdateAdmin;

import java.util.List;

/**
 * Controller to Admin endpoints, linked to {@link Event}
 */
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    @PutMapping("/{eventId}")
    public EventDtoFull editEventAdmin(@RequestBody EventDtoUpdateAdmin eventDtoUpdateAdmin,
                                       @PathVariable("eventId") long eventId) {
        System.out.println(eventDtoUpdateAdmin);
        EventDtoFull eventDtoFull = eventService.editEventAdmin(eventDtoUpdateAdmin, eventId);
        log.info("Обновлено событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @PatchMapping("/{eventId}/publish")
    public EventDtoFull publishEvent(@PathVariable("eventId") long eventId) {
        EventDtoFull eventDtoFull = eventService.publishEvent(eventId);
        log.info("Опубликовано событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @PatchMapping("/{eventId}/reject")
    public EventDtoFull rejectEvent(@PathVariable("eventId") long eventId) {
        EventDtoFull eventDtoFull = eventService.rejectEvent(eventId);
        log.info("Отклонено событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @GetMapping
    public List<EventDtoFull> getAllEventsByAdmin(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Long> categoriesIds,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        List<EventDtoFull> eventDtoFulls = eventService.getAllEventsByAdmin(
                users, states, categoriesIds, rangeStart, rangeEnd, from, size);
        log.info("Запрошен список событий админом {}", eventDtoFulls);
        return eventDtoFulls;
    }
}
