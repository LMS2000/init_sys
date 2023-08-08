package com.lms.init.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lms.init.entity.dao.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
