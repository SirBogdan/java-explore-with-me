package ru.practicum.area;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity of created by Admin Areas. Using to search {@link ru.practicum.event.Event} in area
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "areas", schema = "public")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "lat", nullable = false)
    private Float lat;
    @Column(name = "lon", nullable = false)
    private Float lon;
    @Column(name = "name")
    private String name;
    @Column(name = "radius")
    private Float radius;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AreaType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return id == area.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
