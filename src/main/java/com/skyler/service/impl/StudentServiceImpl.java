package com.skyler.service.impl;

import com.skyler.data.school.StudentMapper;
import com.skyler.domain.Student;
import com.skyler.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by yaoliang on 2017/11/19 at 下午4:29
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Override
    public List<Student> list() {
        return studentMapper.list();
    }

    @Override
    @Transactional(value = "schoolTransactionManager", rollbackFor = Throwable.class)
    public Student getById(long id) {
        return studentMapper.getById(id);
    }

    @Override
    @Transactional(value = "schoolTransactionManager", rollbackFor = Throwable.class)
    public int updateNameById(String name, long id) {
        // 这行为了模拟复杂逻辑
        Student s = studentMapper.getById(id);

        return studentMapper.updateNameById(name, id);
    }
}