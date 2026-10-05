package net.ayman.supplychainx.user.mapper;

import net.ayman.supplychainx.user.dto.UserRequestDTO;
import net.ayman.supplychainx.user.dto.UserResponseDTO;
import net.ayman.supplychainx.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    UserResponseDTO toDTO(User user);

    @Mapping(source = "roleId", target = "role.id")
    User toEntity(UserRequestDTO userDTO);

    List<UserResponseDTO> toResponseDTOList(List<User> users);
}