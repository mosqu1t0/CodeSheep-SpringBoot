package com.mosquito.codesheep.mapper;

import com.github.pagehelper.Page;
import com.mosquito.codesheep.pojo.Code;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CodeMapper {
    @Insert("INSERT INTO code VALUE(null, #{email}, #{code.language}, #{code.fileName}, #{time})")
    int InsertCode(Code code, String email, LocalDateTime time);

    @Select("SELECT * FROM (SELECT language, file_name FROM code WHERE email = #{email}) as lfn"+
            " WHERE language = #{code.language} AND file_name = #{code.fileName}")
    List<Code> SelectUniqueCode(Code code, String email);

    @Select("SELECT language, file_name, time FROM code WHERE email = #{email} ORDER BY time DESC")
    Page<Code> SelectCodesPageByEmail(String email);

    @Delete("DELETE FROM code WHERE email = #{email} AND file_name = #{code.fileName} AND language = #{code.language}")
    int DeleteCode(Code code, String email);

}
