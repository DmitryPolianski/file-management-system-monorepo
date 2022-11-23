package com.file.management.system.user.api.nats;

import com.file.management.system.nats.transport.NatsHandler;
import com.file.management.system.nats.transport.NatsHandlerMapping;
import com.file.management.system.user.domain.request.CreateUserRequest;
import com.file.management.system.user.domain.view.UserView;
import com.file.management.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NatsHandler
@RequiredArgsConstructor
public class UserHandler {

    private final UserService userService;

    @NatsHandlerMapping(natsTopic = "${users.endpoint.nats.create-user}")
    public UserView createUser(CreateUserRequest request) {
        log.debug("Receive createUserRequest: {}", request);
        return userService.create(request);
    }

}
