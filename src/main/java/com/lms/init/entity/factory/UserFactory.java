package com.lms.init.entity.factory;


import com.lms.init.entity.dao.User;
import com.lms.init.entity.dto.UserDto;
import com.lms.init.entity.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class UserFactory {
    public static final UserConverter USER_CONVERTER= Mappers.getMapper(UserConverter.class);
    @Mapper
   public interface UserConverter {
        @Mappings({
                @Mapping(target = "uid",ignore = true),
        }
        )
        User toUser(UserDto userDto);
        UserVo toUserVo(User user);
        List<UserVo> toListUserVo(List<User> user);
    }
}
