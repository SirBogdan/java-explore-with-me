package ru.practicum.request;

/**
 * Mapper to/from DTO, linked to {@link Request}
 */
public class RequestMapper {

    public static RequestDtoParticipation toRequestDtoParticipation(Request request) {
        return RequestDtoParticipation.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
