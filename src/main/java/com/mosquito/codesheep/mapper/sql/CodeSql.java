package com.mosquito.codesheep.mapper.sql;

import org.apache.ibatis.jdbc.SQL;

public class CodeSql {
    @Deprecated
    public String getCodesByConditionsSql(Integer uid, String language, String fileName){
        String where = "uid = " + uid;
        if (fileName != null && !fileName.equals(""))
            where += " AND file_name like concat('%',#{fileName},'%')";
        if (language != null && !language.equals(""))
            where += " AND name = #{language}";
        String finalWhere = where;
        return new SQL(){{
            SELECT("name language");
            SELECT("file_name");
            SELECT("time");
            FROM("code c");
            LEFT_OUTER_JOIN("language l on c.lang_id = l.lang_id");
            WHERE(finalWhere);
            ORDER_BY("time DESC");
        }}.toString();
    }
}
