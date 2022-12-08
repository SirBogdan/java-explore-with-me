package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LocationDto {
    private Float lat;
    private Float lon;
}
