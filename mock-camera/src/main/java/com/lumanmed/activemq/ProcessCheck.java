package com.lumanmed.activemq;

import org.apache.log4j.Logger;

public class ProcessCheck {

    private final static Logger logger = Logger.getLogger(ProcessCheck.class);

    /**
     * 阻塞当前线程，一直到process执行完或者超时
     * 
     * @param process
     *            需要检查是否执行完的线程
     * @param timeOut
     *            超时时间，秒
     */
    public static void blockUntilProcessEnd(Process process, int timeOut) {
        int maxCount = timeOut * 5;
        for (int i = 0; i < maxCount && checkProcessEnd(process); i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    /**
     * 检查一个Process是否已经结束
     * 
     * @param process
     *            需要检查的Process
     * @return 获取exitValue, 如果没有结束，则会抛异常
     */
    public static boolean checkProcessEnd(Process process) {
        try {
            logger.info("Process ends with exit value: " + process.exitValue());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
