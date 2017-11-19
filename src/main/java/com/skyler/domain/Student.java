package com.skyler.domain;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 * <p></p>
 * <pre></pre>
 * NB.
 * Created by skyler on 2017/11/18 at 下午10:14
 */
@Data
public class Student {

    private long id;
    private String name;
    private int age;
    private Date ctime;

}
