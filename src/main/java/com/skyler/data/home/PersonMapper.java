package com.skyler.data.home;

import com.skyler.domain.Person;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午10:42
 */
@Mapper
public interface PersonMapper {
    List<Person> list();

    Person getById(long id);
}
