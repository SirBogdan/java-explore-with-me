package ru.practicum.user;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoShort;

import java.util.List;

/**
 * Interface of service-layer, containing business-logic and linked to {@link User}
 */
public interface UserService {
    UserDto createUser(UserDtoShort userDtoShort);

    UserDto updateUser(UserDto userDto);

    List<UserDto> getUsersByIds(List<Long> ids, int from, int size);

    UserDto getUserById(long userId);

    void deleteUser(long userId);
}
