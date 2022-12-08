package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Shot version of data transfer object for {@link ru.practicum.event.Event}
 * <p>Contains fields, necessary to create new entity</p>
 *
 * @see ru.practicum.event.EventServiceImpl#createEvent(EventDtoNew, long)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EventDtoNew {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Краткое описание события должно содержать от {min} до {max} символов")
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Полное описание события должно содержать от {min} до {max} символов")
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    @NotNull
    private Boolean paid;
    private Integer participantLimit;
    @NotNull
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120, message = "Заголовок события должен содержать от {min} до {max} символов")
    private String title;
}
