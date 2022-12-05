package ru.practicum.request;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Request}
 */
public interface RequestService {
    RequestDtoParticipation createRequest(long userId, long eventId);

    RequestDtoParticipation cancelRequest(long requestId);

    List<RequestDtoParticipation> getAllRequestsByUser(long userId);
}
