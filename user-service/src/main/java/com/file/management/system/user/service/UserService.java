package com.file.management.system.user.service;

import com.file.management.system.user.domain.request.CreateUserRequest;
import com.file.management.system.user.domain.view.UserView;

public interface UserService {

    UserView create(CreateUserRequest request);

}
