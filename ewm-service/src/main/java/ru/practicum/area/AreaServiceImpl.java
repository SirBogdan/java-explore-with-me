package ru.practicum.area;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.area.dto.AreaDtoFull;
import ru.practicum.area.dto.AreaDtoNew;
import ru.practicum.area.dto.AreaDtoUpdate;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.utils.CustomPageRequest;
import ru.practicum.utils.NPEChecker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of service-layer interface, containing business-logic and linked to {@link Area}
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AreaServiceImpl implements AreaService {
    private final AreaRepository areaRepository;

    @Transactional
    public AreaDtoFull createArea(AreaDtoNew areaDtoNew) {
        NPEChecker.checkObjNullValue(areaDtoNew);
        Area area = AreaMapper.fromAreaDtoNew(areaDtoNew);
        if (areaRepository.findByLatAndLon(area.getLat(), area.getLon()) != null) {
            throw new ValidationException(String.format(
                    "Территория с lat %f и lon %f уже сущестует", area.getLat(), area.getLon()));
        }

        area = areaRepository.save(area);
        log.info("Создана территория {}", area);
        return AreaMapper.toAreaDtoFull(area);
    }

    @Transactional
    public AreaDtoFull updateArea(AreaDtoUpdate areaDtoUpdate) {
        NPEChecker.checkObjNullValue(areaDtoUpdate);
        long id = areaDtoUpdate.getId();
        Area areaFromDb = areaRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Территория с id %d не существует", id)));
        Area area = AreaMapper.fromAreaDtoUpdate(
                areaDtoUpdate, areaFromDb.getLat(), areaFromDb.getLon());

        if (area.getName() == null) area.setName(areaFromDb.getName());
        if (area.getRadius() == null) area.setRadius(areaFromDb.getRadius());
        if (area.getType() == null) area.setType(areaFromDb.getType());
        area = areaRepository.save(area);
        log.info("Обновлена территория {}", area);
        return AreaMapper.toAreaDtoFull(area);
    }

    public AreaDtoFull getAreaById(long id) {
        Area area = areaRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Территория с id %d не существует", id)));
        log.info("Запрошена территория {}", area);
        return AreaMapper.toAreaDtoFull(area);
    }

    public List<AreaDtoFull> getAllAreas(int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        List<AreaDtoFull> areas = areaRepository.findAll(pageable).stream()
                .map(AreaMapper::toAreaDtoFull)
                .collect(Collectors.toList());
        log.info("Запрошены все территории {}", areas);
        return areas;
    }
}
