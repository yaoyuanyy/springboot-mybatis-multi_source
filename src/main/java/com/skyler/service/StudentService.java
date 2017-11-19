package com.skyler.service;

import com.skyler.domain.Student;

import java.util.List;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/19 at 下午4:29
 */
public interface StudentService {

    List<Student> list();

    Student getById(long id);

    int updateNameById(String name, long id);
}

