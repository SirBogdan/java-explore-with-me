package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Shot version of data transfer object for {@link ru.practicum.user.User}
 *
 * @see ru.practicum.user.UserServiceImpl#createUser(UserDtoShort)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDtoShort {
    @NotBlank
    private String name;
    @Email
    private String email;
}
