package com.lumanmed.activemq;

import org.apache.log4j.Logger;

public class ProgramSetupThread extends Thread {
    private static final Logger logger = Logger
            .getLogger(ProgramSetupThread.class);

    private static Process programProcess = null;
    private String pname;

    public ProgramSetupThread(String pname) {
        this.pname = pname;
    }

    @Override
    public void run() {
        try {
            if (programProcess == null
                    || ProcessCheck.checkProcessEnd(programProcess))
                programProcess = Runtime.getRuntime().exec(pname);
        } catch (Exception e) {
            // windows下面, 执行的命令如果出现异常，异常信息会乱码，尝试用了GBK等编码，还是不行
            logger.error(e);
        }
    }
}