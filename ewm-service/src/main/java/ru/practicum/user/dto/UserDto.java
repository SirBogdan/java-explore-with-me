package ru.practicum.user.dto;

import lombok.*;

/**
 * Data transfer object for {@link ru.practicum.user.User}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private long id;
    private String name;
    private String email;
}
