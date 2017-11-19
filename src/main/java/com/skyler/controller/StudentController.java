package com.skyler.controller;

import com.skyler.domain.Student;
import com.skyler.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午11:13
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService service;

    @GetMapping("/list")
    public List<Student> list() {
        List<Student> list = service.list();
        return list;
    }

    @GetMapping("/update_name")
    // @PostMapping("/update_name") 实际上修改要用post方式，这里为了浏览器能直接访问，用了get
    public void updateName(@RequestParam("name") String name, @RequestParam("id") Long id){
        service.updateNameById(name,id);
    }
}
