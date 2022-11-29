package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoNew;
import ru.practicum.event.dto.EventDtoShort;
import ru.practicum.event.dto.EventDtoUpdateRequest;
import ru.practicum.request.RequestDtoParticipation;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    public EventDtoFull createEvent(@RequestBody @Validated EventDtoNew eventDtoNew,
                                    @PathVariable("userId") long userId) {
        EventDtoFull eventDtoFull = eventService.createEvent(eventDtoNew, userId);
        log.info("Создано событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @PatchMapping
    public EventDtoFull updateEventPrivate(@RequestBody @Validated EventDtoUpdateRequest eventDtoUpdateRequest,
                                           @PathVariable("userId") long userId) {
        EventDtoFull eventDtoFull = eventService.updateEventPrivate(eventDtoUpdateRequest, userId);
        log.info("Обновлено событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @GetMapping("/{eventId}")
    public EventDtoFull getEventByIdPrivate(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId) {
        EventDtoFull eventDtoFull = eventService.getEventByIdPrivate(userId, eventId);
        log.info("Запрошена информация о событии: {}", eventDtoFull);
        return eventDtoFull;
    }

    @GetMapping
    public List<EventDtoShort> getAllEventsByInitiator(@PathVariable("userId") long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<EventDtoShort> eventDtoShortList = eventService.getAllEventsByInitiator(userId, from, size);
        log.info("Запрошена информация о всех событиях, созданных пользователем: {}", eventDtoShortList);
        return eventDtoShortList;
    }

    @PatchMapping("/{eventId}")
    public EventDtoFull cancelEventByIdPrivate(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId) {
        EventDtoFull eventDtoFull = eventService.cancelEventByIdPrivate(userId, eventId);
        log.info("Отменено событие: {}", eventDtoFull);
        return eventDtoFull;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDtoParticipation> getAllRequestsByUser(@PathVariable("eventId") long eventId) {
        List<RequestDtoParticipation> requestDtoParticipationList = eventService.getAllRequestsByUser(eventId);
        log.info("Получена информация о запросах на участие в событии текущего пользователя {}",
                requestDtoParticipationList);
        return requestDtoParticipationList;
    }


    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public RequestDtoParticipation confirmRequest(@PathVariable("eventId") long eventId,
                                                  @PathVariable("reqId") long reqId) {
        RequestDtoParticipation requestDtoParticipation = eventService.confirmRequest(eventId, reqId);
        log.info("Подтвержден запрос на участие в событии: {}", requestDtoParticipation);
        return requestDtoParticipation;
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public RequestDtoParticipation rejectRequest(@PathVariable("reqId") long reqId) {
        RequestDtoParticipation requestDtoParticipation = eventService.rejectRequest(reqId);
        log.info("Отклонен запрос на участие в событии: {}", requestDtoParticipation);
        return requestDtoParticipation;
    }
}
