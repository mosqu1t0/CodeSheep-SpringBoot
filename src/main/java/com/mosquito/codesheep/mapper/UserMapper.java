package com.mosquito.codesheep.mapper;

import org.apache.ibatis.annotations.*;
import com.mosquito.codesheep.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(email, password, salt, activation_time, is_vaild, confirm_code)" +
            " VALUE(#{email}, #{password}, #{salt}, #{activationTime}, #{isVaild}, #{confirmCode})")
    int InsertUser(User user);

    @Select("SELECT email, activation_time FROM user WHERE confirm_code = #{confirmCode} AND is_vaild = 0")
    User SelectUserByConfirmCode(@Param("confirmCode") String confirmCode);

    @Update("UPDATE user SET is_vaild = 1 WHERE confirm_code = #{confirmCode}")
    int UpdataUserByConfirmCode(@Param("confirmCode") String confirmCode);

    @Select("SELECT email, password, salt FROM user WHERE email = #{email} AND is_vaild = 1")
    List<User> SelectUserByEmailAndVaild(@Param("email") String email);

    @Select("SELECT email, password, salt FROM user WHERE email = #{email}")
    List<User> SelectUserByEmail(@Param("email") String email);


}
