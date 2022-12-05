package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;
import ru.practicum.event.EventState;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of service-layer interface, containing business-logic and linked to {@link Request}
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional
    public RequestDtoParticipation createRequest(long userId, long eventId) {
        if (requestRepository.existsByEvent_IdAndRequester_Id(userId, eventId)) {
            throw new ValidationException("Нельзя добавить повторный запрос");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(EventMapper.fromEventDtoFull(eventService.getEventByIdFull(eventId)))
                .requester(UserMapper.fromUserDto(userService.getUserById(userId)))
                .status(RequestStatus.PENDING)
                .build();
        if (userId == request.getEvent().getInitiator().getId()) {
            throw new ValidationException("Создатель события не может добавить запрос на участие в своём событии");
        }
        if (!request.getEvent().getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }
        if (request.getEvent().getParticipantLimit().equals(
                requestRepository.countAllByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED))) {
            throw new ForbiddenException("Достигнут лимит учатников события");
        }
        if (request.getEvent().getRequestModeration().equals(false)) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        request = requestRepository.save(request);
        return RequestMapper.toRequestDtoParticipation(request);
    }

    @Override
    @Transactional
    public RequestDtoParticipation cancelRequest(long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Запроса с id %d не существует", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        return RequestMapper.toRequestDtoParticipation(request);
    }

    @Override
    public List<RequestDtoParticipation> getAllRequestsByUser(long userId) {
        return requestRepository.findAllByRequester_Id(userId)
                .stream()
                .map(RequestMapper::toRequestDtoParticipation)
                .collect(Collectors.toList());
    }
}
