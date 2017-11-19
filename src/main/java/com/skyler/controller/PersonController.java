package com.skyler.controller;

import com.skyler.data.home.PersonMapper;
import com.skyler.domain.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/person")
public class PersonController {

    @Resource
    private PersonMapper personMapper;

    @GetMapping("/list")
    public List<Person> list() {
        List<Person> list = personMapper.list();
        return list;
    }
}
