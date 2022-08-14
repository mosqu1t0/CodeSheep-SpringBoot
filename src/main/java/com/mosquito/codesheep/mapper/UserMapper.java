package com.mosquito.codesheep.mapper;

import org.apache.ibatis.annotations.*;
import com.mosquito.codesheep.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(email, password, salt, activation_time, is_valid, confirm_code)" +
            " VALUE(#{email}, #{password}, #{salt}, #{activationTime}, #{isValid}, #{confirmCode})")
    int InsertUser(User user);

    @Select("SELECT email, activation_time FROM user WHERE confirm_code = #{confirmCode} AND is_valid = 0")
    User SelectUserByConfirmCode(@Param("confirmCode") String confirmCode);

    @Update("UPDATE user SET is_valid = 1 WHERE confirm_code = #{confirmCode}")
    int UpdateUserByConfirmCode(String confirmCode);

    @Update("UPDATE user SET config = #{config} WHERE email = #{email}")
    int UpdateUserConfigByEmail(String config, String email);


    @Select("SELECT email, password, salt, config FROM user WHERE email = #{email} AND is_valid = 1")
    List<User> SelectUserByEmailAndValid(String email);

    @Select("SELECT email, password, salt FROM user WHERE email = #{email}")
    List<User> SelectUserByEmail(String email);

    @Select("SELECT uid FROM user WHERE email = #{email}")
    Integer SelectUidByEmail(String  email);

}
