package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoShort;
import ru.practicum.utils.CustomPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDtoShort userDtoShort) {
        User user = UserMapper.fromUserDtoShort(userDtoShort);
        user = userRepository.save(user);

        log.info("Создан пользователь: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        long id = user.getId();
        User userFromDb = userRepository.findById(id).orElseThrow(
                () -> new ValidationException(String.format(
                        "Ошибка: пользователя с id %d не существует", id)));

        if (user.getName() == null) {
            user.setName(userFromDb.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userFromDb.getEmail());
        }
        user = userRepository.save(user);
        log.info("Обновлен пользователь: {}", user);

        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);

        List<UserDto> userDtoList = userRepository.findUsersByIdInOrderById(ids, pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Запрошены пользователи: {}", userDtoList);

        return userDtoList;
    }

    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", userId)));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
