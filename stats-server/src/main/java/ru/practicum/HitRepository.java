package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(h.ip))" +
            "from Hit as h " +
            "where (h.timestamp between cast(:start as timestamp) and cast(:end as timestamp)) " +
            "and h.uri in :uris " +
            "group by h.app, h.uri")
    List<ViewStats> getStats(@Param("start") String start, @Param("end") String end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(distinct h.ip))" +
            "from Hit as h " +
            "where (h.timestamp between cast(:start as timestamp) and cast(:end as timestamp)) " +
            "and h.uri in :uris " +
            "group by h.app, h.uri")
    List<ViewStats> getStatsDistinctIp(@Param("start") String start, @Param("end") String end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(h.ip))" +
            "from Hit as h " +
            "where (h.timestamp between cast(:start as timestamp) and cast(:end as timestamp)) " +
            "group by h.app, h.uri")
    List<ViewStats> getStatsAll(@Param("start") String start, @Param("end") String end);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(distinct h.ip))" +
            "from Hit as h " +
            "where (h.timestamp between cast(:start as timestamp) and cast(:end as timestamp)) " +
            "group by h.app, h.uri")
    List<ViewStats> getStatsDistinctIpAll(@Param("start") String start, @Param("end") String end);
}
