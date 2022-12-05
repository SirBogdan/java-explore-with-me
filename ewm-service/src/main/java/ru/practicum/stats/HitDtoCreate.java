package ru.practicum.stats;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class HitDtoCreate {
    @NotBlank
    @NotNull
    private String app;
    @NotBlank
    @NotNull
    private String uri;
    @NotBlank
    @NotNull
    private String ip;
}
