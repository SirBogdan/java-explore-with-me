package ru.practicum;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitDtoCreate;

public class HitMapper {
    public static Hit fromHitDtoCreate(HitDtoCreate hitDtoCreate) {
        return Hit.builder()
                .app(hitDtoCreate.getApp())
                .uri(hitDtoCreate.getUri())
                .ip(hitDtoCreate.getIp())
                .build();
    }

    public static HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}
