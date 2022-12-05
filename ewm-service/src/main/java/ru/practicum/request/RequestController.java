package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to endpoints, linked to {@link Request}
 */
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDtoParticipation createRequest(@PathVariable("userId") long userId,
                                                 @RequestParam(name = "eventId") Long eventId) {
        RequestDtoParticipation requestDtoParticipation = requestService.createRequest(userId, eventId);
        log.info("Создан запрос на участие в событии: {}", requestDtoParticipation);
        return requestDtoParticipation;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDtoParticipation cancelRequest(@PathVariable("requestId") Long requestId) {
        RequestDtoParticipation requestDtoParticipation = requestService.cancelRequest(requestId);
        log.info("Отменен запрос на участие в событии: {}", requestDtoParticipation);
        return requestDtoParticipation;
    }

    @GetMapping
    public List<RequestDtoParticipation> getAllRequestsByUser(@PathVariable("userId") long userId) {
        List<RequestDtoParticipation> requestDtoParticipationList = requestService.getAllRequestsByUser(userId);
        log.info("Запрошен список всех запросов на участие в событиях: {}", requestDtoParticipationList);
        return requestDtoParticipationList;
    }
}
