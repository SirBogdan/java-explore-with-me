package ru.practicum.event;


import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.area.Area;
import ru.practicum.area.AreaMapper;
import ru.practicum.area.AreaService;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.*;
import ru.practicum.stats.HitDtoCreate;
import ru.practicum.stats.StatsClient;
import ru.practicum.stats.ViewStats;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.utils.CustomPageRequest;
import ru.practicum.utils.NPEChecker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of service-layer interface, containing business-logic and linked to {@link Event}
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {

    /**
     * Name of Application
     */
    private static final String EWM = "explore-with-me";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Minimum value of time, that should have pass between creation of even and start of event
     */
    private static final int MIN_CREATED_START_INTERVAL = 2;
    /**
     * Minimum value of time, that should have pass between publication of even and start of event
     */
    private static final int MIN_PUBLISH_START_INTERVAL = 1;
    /**
     * Constant way of sorting getting events by event date
     */
    private static final String SORT_BY_DATE = "EVENT_DATE";
    /**
     * Constant way of sorting getting events by amount of views
     */
    private static final String VIEWS = "VIEWS";


    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final AreaService areaService;

    @Override
    @Transactional
    public EventDtoFull createEvent(EventDtoNew eventDtoNew, long initiatorId) {
        NPEChecker.checkObjNullValue(eventDtoNew);
        Category category = CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(eventDtoNew.getCategory()));
        Event event = EventMapper.fromEventDtoNew(eventDtoNew, category);
        event.setCategory(CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(event.getCategory().getId())));
        event.setCreatedOn(LocalDateTime.now());
        if (event.getCreatedOn().plusHours(MIN_CREATED_START_INTERVAL).isAfter(event.getEventDate())) {
            throw new ValidationException(
                    "Время начала события должно быть позже времени создания события минимум на 2 часа.");
        }
        event.setInitiator(UserMapper.fromUserDto(userService.getUserById(initiatorId)));
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);

        return EventMapper.toEventDtoFull(event, 0, 0);
    }

    @Override
    @Transactional
    public EventDtoFull updateEventPrivate(EventDtoUpdateRequest eventDtoUpdateRequest, long userId) {
        NPEChecker.checkObjNullValue(eventDtoUpdateRequest);
        Category category = CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(eventDtoUpdateRequest.getCategory()));
        Event event = EventMapper.fromEventDtoUpdateRequest(eventDtoUpdateRequest, category);
        long eventId = eventDtoUpdateRequest.getEventId();
        Event eventFromDb = eventRepository.findById(event.getId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        validateEventForUpdate(eventFromDb, event, userId);

        event.setCategory(CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(event.getCategory().getId())));
        event.setCreatedOn(eventFromDb.getCreatedOn());
        event.setInitiator(eventFromDb.getInitiator());
        event.setLat(eventFromDb.getLat());
        event.setLon(eventFromDb.getLon());
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        event.setRequestModeration(eventFromDb.getRequestModeration());
        event.setState(EventState.PENDING);

        event = eventRepository.save(event);
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    private Integer getConfirmedRequests(long eventId) {
        return requestRepository.countAllByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private int getEventViews(Event event) {
        NPEChecker.checkObjNullValue(event);
        List<ViewStats> viewStatsList = statsClient.getStats(
                event.getCreatedOn().format(FORMATTER), event.getEventDate().format(FORMATTER),
                List.of("/events/" + event.getId()), false);
        int result = 0;
        if (!viewStatsList.isEmpty()) {
            result = viewStatsList.get(0).getHits().intValue();
        }
        return result;
    }

    @Override
    public EventDtoFull getEventByIdPrivate(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        if (userId != event.getInitiator().getId()) {
            throw new ForbiddenException(String.format(
                    "У пользователя с id %d нет доступа к событию с id %d", userId, event.getId()));
        }
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    public EventDtoFull getEventByIdFull(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    public List<EventDtoShort> getAllEventsByInitiator(long userId, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        return eventRepository.findAllByInitiator_Id(userId, pageable)
                .stream()
                .map(event -> EventMapper.toEventDtoShort(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDtoFull cancelEventByIdPrivate(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        if (userId != event.getInitiator().getId()) {
            throw new ForbiddenException(String.format(
                    "У пользователя с id %d нет доступа к событию с id %d", userId, event.getId()));
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException(
                    "Отменить можно только событие в состоянии ожидания модерации.");
        }
        event.setState(EventState.CANCELED);
        event = eventRepository.save(event);
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    public List<RequestDtoParticipation> getAllRequestsByUser(long eventId) {
        return requestRepository.findAllByEvent_Id(eventId).stream()
                .map(RequestMapper::toRequestDtoParticipation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDtoParticipation confirmRequest(long eventId, long requestId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Запроса с id %d не существует", requestId)));
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("Запрос уже одобрен");
        }
        if (event.getParticipantLimit().equals(
                getConfirmedRequests(eventId))) {
            request.setStatus(RequestStatus.CANCELED);
        }
        request.setStatus(RequestStatus.CONFIRMED);
        request = requestRepository.save(request);

        if (event.getParticipantLimit().equals(
                getConfirmedRequests(eventId))) {
            List<Request> requestList = requestRepository.findAllByEvent_IdAndStatus(eventId, RequestStatus.PENDING);
            requestList.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(requestList);
        }
        return RequestMapper.toRequestDtoParticipation(request);
    }

    @Override
    @Transactional
    public RequestDtoParticipation rejectRequest(long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Запроса с id %d не существует", requestId)));
        request.setStatus(RequestStatus.REJECTED);
        request = requestRepository.save(request);
        return RequestMapper.toRequestDtoParticipation(request);
    }

    @Override
    @Transactional
    public EventDtoFull editEventAdmin(EventDtoUpdateAdmin eventDtoUpdateAdmin, long eventId) {
        NPEChecker.checkObjNullValue(eventDtoUpdateAdmin);
        Category category = CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(eventDtoUpdateAdmin.getCategory()));
        Event event = EventMapper.fromEventDtoUpdateAdmin(eventDtoUpdateAdmin, category);
        event.setId(eventId);

        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        event.setCreatedOn(eventFromDb.getCreatedOn());
        event.setInitiator(eventFromDb.getInitiator());

        event.setCategory(CategoryMapper.fromCategoryDto(
                categoryService.getCategoryById(eventDtoUpdateAdmin.getCategory())));
        event.setState(EventState.PENDING);

        if (event.getLat() == null) {
            event.setLat(eventFromDb.getLat());
            event.setLon(eventFromDb.getLon());
        }
        if (event.getRequestModeration() == null) event.setRequestModeration(eventFromDb.getRequestModeration());
        event = eventRepository.save(event);

        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventDtoFull publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        LocalDateTime now = LocalDateTime.now();
        if (now.plusHours(MIN_PUBLISH_START_INTERVAL).isAfter(event.getEventDate())) {
            throw new ValidationException(
                    "Время начала события должно быть позже времени публикации события минимум на 1 час.");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Публиковать можно только события в состоянии ожидания публикации");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(now);
        event = eventRepository.save(event);

        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventDtoFull rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя отклонить опубликованное событие");
        }

        event.setState(EventState.CANCELED);
        event = eventRepository.save(event);

        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    @Override
    public EventDtoFull getEventById(long eventId, String ip) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Запрашиваемое событие должно быть опубликовано");
        }
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        statsClient.sendHit(new HitDtoCreate(EWM, "/events/" + event.getId(), ip));

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    /**
     * Method provide ability to get information of events, filtered by variety of parameters
     *
     * @param text          text to search in the contents of the annotation and a detailed description of the event
     * @param categoriesIds the list of ids of the categories in which the search will be conducted
     * @param paid          search for paid/free events only
     * @param rangeStart    date and time not earlier than when the event should occur
     * @param rangeEnd      date and time no later than which the event should occur
     * @param onlyAvailable only events that have not reached the limit of participation requests
     * @param sort          Sorting option: by event date or by number of views
     * @param from          the number of events that need to be skipped to form the current list
     * @param areaId        id of searching area
     * @param size          number of events in the list
     * @param ip            ip of requester
     * @return list of events
     */
    @Override
    public List<EventDtoShort> getEventsFiltered(
            String text, List<Long> categoriesIds, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, Long areaId, String sort, Integer from, Integer size, String ip) {
        Pageable pageable = CustomPageRequest.of(from, size);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.of(2030, 1, 1, 0, 0, 0);
        } else {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder(qEvent.state.eq(EventState.PUBLISHED));

        if (text != null) {
            builder.and(qEvent.annotation.containsIgnoreCase(text)
                    .or(qEvent.description.containsIgnoreCase(text)));
        }
        if (categoriesIds != null && !categoriesIds.isEmpty()) builder.and(qEvent.category.id.in(categoriesIds));
        if (paid != null) builder.and(qEvent.paid.eq(paid));
        builder.and(qEvent.eventDate.between(start, end));
        if (areaId != null) {
            Area area = AreaMapper.fromAreaDtoFull(areaService.getAreaById(areaId));
            List<Long> idsInArea = eventRepository.findEventsIdsInArea(area.getLat(), area.getLon(), area.getRadius());
            builder.and(qEvent.id.in(idsInArea));
        }

        List<Event> events = eventRepository.findAll(builder, pageable).toList();
        if (onlyAvailable != null) {
            if (onlyAvailable) {
                events = events.stream()
                        .filter(event -> event.getParticipantLimit() > getConfirmedRequests(event.getId()))
                        .collect(Collectors.toList());
            }
        }
        List<EventDtoShort> eventDtoShortList = fromEventToEventDtoShort(events);
        if (SORT_BY_DATE.equals(sort)) {
            eventDtoShortList.stream()
                    .sorted(Comparator.comparing(EventDtoShort::getEventDate))
                    .collect(Collectors.toList());
        } else if (VIEWS.equals(sort)) {
            eventDtoShortList.stream()
                    .sorted(Comparator.comparing(EventDtoShort::getViews))
                    .collect(Collectors.toList());
        }

        statsClient.sendHit(new HitDtoCreate(EWM, "/events", ip));

        return eventDtoShortList;
    }

    /**
     * Method provide ability to get information of events by Admin, filtered by variety of parameters
     *
     * @param categoriesIds the list of ids of the categories in which the search will be conducted
     * @param rangeStart    date and time not earlier than when the event should occur
     * @param rangeEnd      date and time no later than which the event should occur
     * @param areaId        id of searching area
     * @param from          the number of events that need to be skipped to form the current list
     * @param size          number of events in the list
     * @return list of events
     */
    @Override
    public List<EventDtoFull> getAllEventsByAdmin(
            List<Long> users, List<String> states, List<Long> categoriesIds,
            String rangeStart, String rangeEnd, Long areaId, Integer from, Integer size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.of(2030, 1, 1, 0, 0, 0);
        } else {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }

        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        if (users != null && !users.isEmpty()) builder.and(qEvent.initiator.id.in(users));
        if (states != null) {
            List<EventState> eventStates = states.stream().map(EventState::valueOf).collect(Collectors.toList());
            if (!eventStates.isEmpty()) builder.and(qEvent.state.in(eventStates));
        }
        if (categoriesIds != null && !categoriesIds.isEmpty()) builder.and(qEvent.category.id.in(categoriesIds));
        builder.and(qEvent.eventDate.between(start, end));
        if (areaId != null) {
            Area area = AreaMapper.fromAreaDtoFull(areaService.getAreaById(areaId));
            List<Long> idsInArea = eventRepository.findEventsIdsInArea(area.getLat(), area.getLon(), area.getRadius());
            builder.and(qEvent.id.in(idsInArea));
        }

        List<Event> events = eventRepository.findAll(builder, pageable).toList();

        return events.stream()
                .map(event -> EventMapper.toEventDtoFull(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDtoShort> fromEventToEventDtoShort(List<Event> events) {
        NPEChecker.checkObjNullValue(events);
        return events.stream()
                .map(event -> EventMapper.toEventDtoShort(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

    private void validateEventForUpdate(Event eventFromDb, Event event, long userId) {
        NPEChecker.checkObjNullValue(eventFromDb, event);
        if (userId != eventFromDb.getInitiator().getId()) {
            throw new ForbiddenException(String.format(
                    "У пользователя с id %d нет доступа к событию с id %d", userId, event.getId()));
        }
        if (eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(
                    "Нельзя изменять опубликованное событие");
        }
        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new ValidationException(
                    "Время начала события должно быть позже времени обновления события минимум на 2 часа.");
        }
    }
}
