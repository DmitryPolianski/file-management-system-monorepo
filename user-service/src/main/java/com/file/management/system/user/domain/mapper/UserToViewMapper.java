package com.file.management.system.user.domain.mapper;

import com.file.management.system.user.domain.view.UserView;
import com.file.management.system.user.model.User;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public interface UserToViewMapper extends Converter<User, UserView> {
}
