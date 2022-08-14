package com.mosquito.codesheep.mapper;

import com.github.pagehelper.Page;
import com.mosquito.codesheep.mapper.sql.CodeSql;
import com.mosquito.codesheep.pojo.Code;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CodeMapper {
    @Insert("INSERT INTO code(uid, lang_id, file_name, time) VALUE(#{uid}, "+
            "(SELECT lang_id FROM language WHERE name = #{code.language}), #{code.fileName}, #{time})")
    int InsertCode(Code code, Integer uid, LocalDateTime time);

    @Delete("DELETE FROM code WHERE uid = #{uid} AND file_name = #{code.fileName} " +
            "AND lang_id = (SELECT lang_id FROM language WHERE name = #{code.language})")
    int DeleteCode(Code code, Integer uid);

    @Update("UPDATE code SET time = #{code.time} WHERE uid = #{uid} AND file_name = #{code.fileName}" +
            "AND lang_id = (SELECT lang_id FROM language WHERE name = #{code.language})")
    int UpdateCodeTime(Code code, Integer uid);

    @Select("SELECT id FROM code WHERE lang_id = "+
            "(SELECT lang_id FROM language WHERE name = #{code.language}) AND file_name = #{code.fileName} AND uid = #{uid}")
    List<Code> SelectUniqueCode(Code code, Integer uid);

//    @Select("SELECT name language, file_name, time FROM code c "
//            +"LEFT JOIN language l on c.lang_id = l.lang_id WHERE uid = #{uid} ORDER BY time DESC")
    @SelectProvider(type = CodeSql.class, method = "getCodesByConditionsSql")
    Page<Code> SelectCodesPageByCondition(Integer uid, String language, String fileName);

}
