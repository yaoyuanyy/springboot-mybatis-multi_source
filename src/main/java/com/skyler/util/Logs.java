package com.skyler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Logs {

    private final static Logger infolog;
    private final static Logger slowlog;
    private final static Logger errorlog;
    private final static Logger dblog;

    static {
        infolog = LoggerFactory.getLogger("infoLog");
        slowlog = LoggerFactory.getLogger("slowLog");
        errorlog = LoggerFactory.getLogger("errorLog");
        dblog = LoggerFactory.getLogger("dbLog");
    }

    public static Logger getinfoLogger() {
        return infolog;
    }

    public static Logger getslowLogger() {
        return slowlog;
    }

    public static Logger geterrorLogger() {
        return errorlog;
    }

    public static Logger getDblogger() {
        return dblog;
    }

    public static boolean ifLog(int n) {
        return System.currentTimeMillis() % n == 1;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}
