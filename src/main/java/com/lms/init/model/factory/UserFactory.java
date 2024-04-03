package com.lms.init.model.factory;


import com.lms.init.model.entity.User;
import com.lms.init.model.dto.user.UserDto;
import com.lms.init.model.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class UserFactory {
    public static final UserConverter USER_CONVERTER= Mappers.getMapper(UserConverter.class);
    @Mapper
   public interface UserConverter {

        User toUser(UserDto userDto);
        UserVo toUserVo(User user);
        List<UserVo> toListUserVo(List<User> user);
    }
}
