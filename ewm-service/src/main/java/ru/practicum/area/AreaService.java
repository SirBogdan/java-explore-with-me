package ru.practicum.area;

import ru.practicum.area.dto.AreaDtoFull;
import ru.practicum.area.dto.AreaDtoNew;
import ru.practicum.area.dto.AreaDtoUpdate;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Area}
 */
public interface AreaService {
    AreaDtoFull createArea(AreaDtoNew areaDtoNew);

    AreaDtoFull updateArea(AreaDtoUpdate areaDtoUpdate);

    AreaDtoFull getAreaById(long id);

    List<AreaDtoFull> getAllAreas(int from, int size);
}