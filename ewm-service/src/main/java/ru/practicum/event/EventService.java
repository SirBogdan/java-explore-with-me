package ru.practicum.event;

import ru.practicum.event.dto.*;
import ru.practicum.request.RequestDtoParticipation;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Event}
 */
public interface EventService {
    EventDtoFull createEvent(EventDtoNew eventDtoNew, long initiatorId);

    EventDtoFull updateEventPrivate(EventDtoUpdateRequest eventDtoUpdateRequest, long userId);

    EventDtoFull getEventByIdPrivate(long userId, long eventId);

    EventDtoFull getEventByIdFull(long eventId);

    List<EventDtoShort> getAllEventsByInitiator(long userId, int from, int size);

    EventDtoFull cancelEventByIdPrivate(long userId, long eventId);

    List<RequestDtoParticipation> getAllRequestsByUser(long eventId);

    RequestDtoParticipation confirmRequest(long eventId, long requestId);

    RequestDtoParticipation rejectRequest(long requestId);

    EventDtoFull editEventAdmin(EventDtoUpdateAdmin eventDtoUpdateAdmin, long eventId);

    EventDtoFull publishEvent(long eventId);

    EventDtoFull rejectEvent(long eventId);

    EventDtoFull getEventById(long eventId, String ip);

    List<EventDtoShort> getEventsFiltered(
            String text, List<Long> categoriesIds, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, Long areaId, String sort, Integer from, Integer size, String ip);

    List<EventDtoFull> getAllEventsByAdmin(
            List<Long> users, List<String> states, List<Long> categoriesIds,
            String rangeStart, String rangeEnd, Long areaId, Integer from, Integer size);

    List<EventDtoShort> fromEventToEventDtoShort(List<Event> events);
}
