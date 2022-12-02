package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Shot version of data transfer object for {@link ru.practicum.compilation.Compilation}
 * <p>Contains fields, necessary to create new entity</p>
 *
 * @see ru.practicum.compilation.CompilationServiceImpl#createCompilation(CompilationDtoNew)
 */
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
