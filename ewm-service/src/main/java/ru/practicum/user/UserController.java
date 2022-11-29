package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoShort;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated UserDtoShort userDtoShort) {
        UserDto userDto = userService.createUser(userDtoShort);
        log.info("Создан пользователь: {}", userDto);
        return userDto;
    }

    @PatchMapping
    public UserDto updateUser(@RequestBody @Validated UserDto userDto) {
        UserDto updatedUserDto = userService.updateUser(userDto);
        log.info("Обновлен пользователь: {}", updatedUserDto);
        return updatedUserDto;
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam(name = "ids") List<Long> ids,
                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<UserDto> userDtoList = userService.getUsersByIds(ids, from, size);
        log.info("Запрошен список пользователей: {}", userDtoList);
        return userDtoList;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Запрошена информация о пользователе: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        userService.deleteUser(userId);
    }
}
