package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository, linked to {@link Request}
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByEvent_IdAndRequester_Id(long eventId, long userId);

    Integer countAllByEvent_IdAndStatus(long eventId, RequestStatus status);

    List<Request> findAllByRequester_Id(long userId);

    List<Request> findAllByEvent_IdAndStatus(long eventId, RequestStatus status);

    List<Request> findAllByEvent_Id(long eventId);
}
