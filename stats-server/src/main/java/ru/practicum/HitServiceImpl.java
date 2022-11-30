package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitDtoCreate;
import ru.practicum.dto.ViewStats;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.net.URLDecoder.decode;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Transactional
    public HitDto createHit(HitDtoCreate hitDtoCreate) {
        Hit hit = HitMapper.fromHitDtoCreate(hitDtoCreate);
        hit.setTimestamp(LocalDateTime.now());
        hit = hitRepository.save(hit);

        return HitMapper.toHitDto(hit);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        start = decode(start, StandardCharsets.UTF_8);
        end = decode(end, StandardCharsets.UTF_8);

        List<ViewStats> viewStatsList;
        if (uris == null || uris.size() == 0) {
            if (unique) {
                viewStatsList = hitRepository.getStatsDistinctIpAll(start, end);
            } else {
                viewStatsList = hitRepository.getStatsAll(start, end);
            }
        } else {
            if (unique) {
                viewStatsList = hitRepository.getStatsDistinctIp(start, end, uris);
            } else {
                viewStatsList = hitRepository.getStats(start, end, uris);
            }
        }

        return viewStatsList;
    }
}
