package ru.practicum.event;

import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.user.UserMapper;

/**
 * Mapper to/from DTO, linked to {@link Event}
 */
public class EventMapper {
    public static Event fromEventDtoFull(EventDtoFull eventDtoFull) {
        return Event.builder()
                .id(eventDtoFull.getId())
                .annotation(eventDtoFull.getAnnotation())
                .category(CategoryMapper.fromCategoryDto(eventDtoFull.getCategory()))
                .createdOn(eventDtoFull.getCreatedOn())
                .description(eventDtoFull.getDescription())
                .eventDate(eventDtoFull.getEventDate())
                .initiator(UserMapper.fromUserDtoShort(eventDtoFull.getInitiator()))
                .location(eventDtoFull.getLocation())
                .paid(eventDtoFull.getPaid())
                .participantLimit(eventDtoFull.getParticipantLimit())
                .publishedOn(eventDtoFull.getPublishedOn())
                .requestModeration(eventDtoFull.getRequestModeration())
                .state(eventDtoFull.getState())
                .title(eventDtoFull.getTitle())
                .build();
    }

    public static EventDtoFull toEventDtoFull(Event event, Integer confirmedRequests, Integer views) {
        return EventDtoFull.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDtoShort(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static Event fromEventDtoNew(EventDtoNew eventDtoNew, Category category) {
        return Event.builder()
                .annotation(eventDtoNew.getAnnotation())
                .category(category)
                .description(eventDtoNew.getDescription())
                .eventDate(eventDtoNew.getEventDate())
                .location(eventDtoNew.getLocation())
                .paid(eventDtoNew.getPaid())
                .participantLimit(eventDtoNew.getParticipantLimit())
                .requestModeration(eventDtoNew.getRequestModeration())
                .title(eventDtoNew.getTitle())
                .build();
    }

    public static EventDtoShort toEventDtoShort(Event event, Integer confirmedRequests, Integer views) {
        return EventDtoShort.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDtoShort(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static Event fromEventDtoUpdateRequest(EventDtoUpdateRequest eventDtoUpdateRequest, Category category) {
        return Event.builder()
                .id(eventDtoUpdateRequest.getEventId())
                .annotation(eventDtoUpdateRequest.getAnnotation())
                .category(category)
                .description(eventDtoUpdateRequest.getDescription())
                .eventDate(eventDtoUpdateRequest.getEventDate())
                .paid(eventDtoUpdateRequest.getPaid())
                .participantLimit(eventDtoUpdateRequest.getParticipantLimit())
                .title(eventDtoUpdateRequest.getTitle())
                .build();
    }

    public static Event fromEventDtoUpdateAdmin(EventDtoUpdateAdmin eventDtoUpdateAdmin, Category category) {
        return Event.builder()
                .annotation(eventDtoUpdateAdmin.getAnnotation())
                .category(category)
                .description(eventDtoUpdateAdmin.getDescription())
                .eventDate(eventDtoUpdateAdmin.getEventDate())
                .location(eventDtoUpdateAdmin.getLocation())
                .paid(eventDtoUpdateAdmin.getPaid())
                .participantLimit(eventDtoUpdateAdmin.getParticipantLimit())
                .requestModeration(eventDtoUpdateAdmin.getRequestModeration())
                .title(eventDtoUpdateAdmin.getTitle())
                .build();
    }
}
