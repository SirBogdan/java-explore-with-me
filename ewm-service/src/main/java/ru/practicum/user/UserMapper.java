package ru.practicum.user;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoShort;

public class UserMapper {

    public static User fromUserDtoShort(UserDtoShort userDtoShort) {
        return new User(userDtoShort.getName(), userDtoShort.getEmail());
    }

    public static UserDtoShort toUserDtoShort(User user) {
        return new UserDtoShort(user.getName(), user.getEmail());
    }

    public static User fromUserDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
