package ru.practicum.area;

import ru.practicum.area.dto.AreaDtoFull;
import ru.practicum.area.dto.AreaDtoNew;
import ru.practicum.area.dto.AreaDtoUpdate;

/**
 * Mapper to/from DTO, linked to {@link Area}
 */
public class AreaMapper {

    public static AreaDtoFull toAreaDtoFull(Area area) {
        return AreaDtoFull.builder()
                .id(area.getId())
                .lat(area.getLat())
                .lon(area.getLon())
                .name(area.getName())
                .radius(area.getRadius())
                .type(area.getType())
                .build();
    }

    public static Area fromAreaDtoFull(AreaDtoFull areaDtoFull) {
        return Area.builder()
                .id(areaDtoFull.getId())
                .lat(areaDtoFull.getLat())
                .lon(areaDtoFull.getLon())
                .name(areaDtoFull.getName())
                .radius(areaDtoFull.getRadius())
                .type(areaDtoFull.getType())
                .build();
    }

    public static Area fromAreaDtoNew(AreaDtoNew areaDtoNew) {
        return Area.builder()
                .lat(areaDtoNew.getLat())
                .lon(areaDtoNew.getLon())
                .name(areaDtoNew.getName())
                .radius(areaDtoNew.getRadius())
                .type(areaDtoNew.getType())
                .build();
    }

    public static Area fromAreaDtoUpdate(AreaDtoUpdate areaDtoUpdate, float lat, float lon) {
        return Area.builder()
                .id(areaDtoUpdate.getId())
                .lat(lat)
                .lon(lon)
                .name(areaDtoUpdate.getName())
                .radius(areaDtoUpdate.getRadius())
                .type(areaDtoUpdate.getType())
                .build();
    }
}
