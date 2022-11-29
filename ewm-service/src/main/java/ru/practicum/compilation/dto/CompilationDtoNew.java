package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CompilationDtoNew {
    @NotBlank
    @NotNull
    private String title;
    @NotNull
    private Boolean pinned;
    private List<Long> events;
}
