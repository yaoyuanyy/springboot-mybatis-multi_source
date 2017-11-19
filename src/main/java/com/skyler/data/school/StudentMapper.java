package com.skyler.data.school;

import com.skyler.domain.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午10:42
 */
@Mapper
public interface StudentMapper {

    List<Student> list();

    Student getById(@Param("id") long id);

    int updateNameById(@Param("name") String name, @Param("id") long id);
}
