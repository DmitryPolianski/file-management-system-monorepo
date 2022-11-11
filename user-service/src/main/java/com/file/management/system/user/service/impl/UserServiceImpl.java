package com.file.management.system.user.service.impl;

import com.file.management.system.user.domain.mapper.CreateUserRequestToUserMapper;
import com.file.management.system.user.domain.mapper.UserToViewMapper;
import com.file.management.system.user.domain.request.CreateUserRequest;
import com.file.management.system.user.domain.view.UserView;
import com.file.management.system.user.exception.NotValidUserDataException;
import com.file.management.system.user.model.User;
import com.file.management.system.user.repository.UserRepository;
import com.file.management.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final CreateUserRequestToUserMapper createUserRequestToUserMapper;
    private final UserToViewMapper userToViewMapper;

    @Override
    @Transactional
    public UserView create(CreateUserRequest request) {
        User newUser = createUserRequestToUserMapper.convert(request);
        if (newUser == null) {
            throw new NotValidUserDataException("User must not be null!");
        }
        newUser = repository.save(newUser);
        return userToViewMapper.convert(newUser);
    }

}
