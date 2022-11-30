package ru.practicum.event;


import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.location.Location;
import ru.practicum.event.location.LocationRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.event.QEvent;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventService {

    private static final String EWM = "explore-with-me";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MIN_CREATED_START_INTERVAL = 2;
    private static final int MIN_PUBLISH_START_INTERVAL = 1;
    private static final String SORT_BY_DATE = "EVENT_DATE";
    private static final String VIEWS = "VIEWS";


    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Transactional
    public EventDtoFull createEvent(EventDtoNew eventDtoNew, long initiatorId) {
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
        Location location = event.getLocation();
        Location locationFromDb = locationRepository.findByLatAndLon(
                location.getLat(), location.getLon());
        if (locationFromDb == null) {
            location = locationRepository.save(event.getLocation());
            event.setLocation(location);
        } else {
            event.setLocation(locationFromDb);
        }
        event = eventRepository.save(event);

        return EventMapper.toEventDtoFull(event, 0, 0);
    }

    @Transactional
    public EventDtoFull updateEventPrivate(EventDtoUpdateRequest eventDtoUpdateRequest, long userId) {
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
        event.setLocation(eventFromDb.getLocation());
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
        List<ViewStats> viewStatsList = statsClient.getStats(
                event.getCreatedOn().format(FORMATTER), event.getEventDate().format(FORMATTER),
                List.of("/events/" + event.getId()), false);
        int result = 0;
        if (!viewStatsList.isEmpty()) {
            result = viewStatsList.get(0).getHits().intValue();
        }
        return result;
    }

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

    public EventDtoFull getEventByIdFull(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "События с id %d не существует", eventId)));
        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

    public List<EventDtoShort> getAllEventsByInitiator(long userId, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        return eventRepository.findAllByInitiator_Id(userId, pageable)
                .stream()
                .map(event -> EventMapper.toEventDtoShort(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

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

    public List<RequestDtoParticipation> getAllRequestsByUser(long eventId) {
        return requestRepository.findAllByEvent_Id(eventId).stream()
                .map(RequestMapper::toRequestDtoParticipation)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public RequestDtoParticipation rejectRequest(long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Запроса с id %d не существует", requestId)));
        request.setStatus(RequestStatus.REJECTED);
        request = requestRepository.save(request);
        return RequestMapper.toRequestDtoParticipation(request);
    }

    @Transactional
    public EventDtoFull editEventAdmin(EventDtoUpdateAdmin eventDtoUpdateAdmin, long eventId) {
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
        if (event.getLocation() == null) event.setLocation(eventFromDb.getLocation());
        if (event.getRequestModeration() == null) event.setRequestModeration(eventFromDb.getRequestModeration());
        event = eventRepository.save(event);

        Integer confirmedRequests = getConfirmedRequests(eventId);
        Integer views = getEventViews(event);

        return EventMapper.toEventDtoFull(event, confirmedRequests, views);
    }

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

    public List<EventDtoShort> getEventsFiltered(
            String text, List<Long> categoriesIds, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort, Integer from, Integer size, String ip) {
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

        List<Event> events = eventRepository.findAll(builder, pageable).toList();
        if (onlyAvailable != null) {
            if (onlyAvailable) {
                events = events.stream()
                        .filter(event -> event.getParticipantLimit() > getConfirmedRequests(event.getId()))
                        .collect(Collectors.toList());
            }
        }
        List<EventDtoShort> eventDtoShortList = fromEventToEventDtoShort(events);
        if (sort != null) {
            if (sort.equals(SORT_BY_DATE)) {
                eventDtoShortList.stream()
                        .sorted(Comparator.comparing(EventDtoShort::getEventDate))
                        .collect(Collectors.toList());
            } else if (sort.equals(VIEWS)) {
                eventDtoShortList.stream()
                        .sorted(Comparator.comparing(EventDtoShort::getViews))
                        .collect(Collectors.toList());
            }
        }

        statsClient.sendHit(new HitDtoCreate(EWM, "/events", ip));

        return eventDtoShortList;
    }

    public List<EventDtoFull> getAllEventsByAdmin(
            List<Long> users, List<String> states, List<Long> categoriesIds,
            String rangeStart, String rangeEnd, Integer from, Integer size) {
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

        List<Event> events = eventRepository.findAll(builder, pageable).toList();

        return events.stream()
                .map(event -> EventMapper.toEventDtoFull(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

    public List<EventDtoShort> fromEventToEventDtoShort(List<Event> events) {
        return events.stream()
                .map(event -> EventMapper.toEventDtoShort(
                        event, getConfirmedRequests(event.getId()), getEventViews(event)))
                .collect(Collectors.toList());
    }

    private void validateEventForUpdate(Event eventFromDb, Event event, long userId) {
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
