package com.file.management.system.user.domain.mapper;

import com.file.management.system.user.domain.request.CreateUserRequest;
import com.file.management.system.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateUserRequestToUserMapper extends Converter<CreateUserRequest, User> {
}
