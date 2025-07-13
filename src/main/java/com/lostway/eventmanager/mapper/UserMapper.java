package com.lostway.eventmanager.mapper;

import com.lostway.eventmanager.controller.dto.UserRegistryDto;
import com.lostway.eventmanager.controller.dto.UserToShowDto;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.service.model.UserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserModel toModel(UserEntity userEntity);

    UserModel toModel(UserRegistryDto userRegistryDto);

    UserToShowDto toUserToShowDto(UserModel registeredUserModel);
}
