package ru.practicum;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitDtoCreate;
import ru.practicum.dto.ViewStats;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link Hit}
 */
public interface HitService {
    HitDto createHit(HitDtoCreate hitDtoCreate);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
