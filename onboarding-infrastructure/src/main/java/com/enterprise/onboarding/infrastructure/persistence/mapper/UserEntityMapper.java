package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.User;
import com.enterprise.onboarding.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}
