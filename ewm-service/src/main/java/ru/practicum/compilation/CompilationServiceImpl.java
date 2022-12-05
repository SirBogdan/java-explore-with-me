package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoNew;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventDtoShort;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.utils.CustomPageRequest;
import ru.practicum.utils.NPEChecker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of service-layer interface, containing business-logic and linked to {@link Compilation}
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationDtoNew compilationDtoNew) {
        NPEChecker.checkObjNullValue(compilationDtoNew);
        List<Long> compilationEvents = compilationDtoNew.getEvents();
        List<Event> events = compilationEvents.stream()
                .map(eventId -> EventMapper.fromEventDtoFull(eventService.getEventByIdFull(eventId)))
                .collect(Collectors.toList());
        List<EventDtoShort> eventDtoShortList = eventService.fromEventToEventDtoShort(events);
        Compilation compilation = CompilationMapper.fromCompilationDtoNew(compilationDtoNew, events);

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation, eventDtoShortList);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteCompilationsEvent(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Подборки с id %d не существует", compId)));

        compilation.getEvents().removeIf(event -> event.getId() == eventId);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addCompilationsEvent(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Подборки с id %d не существует", compId)));
        Event event = EventMapper.fromEventDtoFull(eventService.getEventByIdFull(eventId));
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Подборки с id %d не существует", compId)));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Подборки с id %d не существует", compId)));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.getAllByPinned(pinned, pageable);
        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDto(
                        compilation, eventService.fromEventToEventDtoShort(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Подборки с id %d не существует", compId)));
        List<EventDtoShort> eventDtoShortList = eventService.fromEventToEventDtoShort(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, eventDtoShortList);
    }

}
