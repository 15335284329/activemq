package com.lumanmed.microscope;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lumanmed.activemq.api.Client;
import com.lumanmed.activemq.impl.FileServerClient;

public class MicroScope {
    private static final Logger logger = Logger.getLogger(MicroScope.class);
    private Client client;
    private MicroRequestHandler handler;
    private Vector<File> imageQueue;

    private static String programPath = "C:\\Program Files (x86)\\Luman\\lumanScanner\\lumanScanner.exe";

    public MicroScope() {
        imageQueue = new Vector<File>();
        DriverThread driver = new DriverThread(imageQueue);
        System.out.println("Ready to start NIS program ...");
        driver.start();
        client = new FileServerClient("Micro-Response", "Micro-Request");
        handler = new MicroRequestHandler(imageQueue);
        client.waitAndResponse(handler);
    }

    // 在新线程里启动显微照相程序
    public static void setupMicroProgram() {
        new ProgramSetupThread(programPath).start();
    }

    private static class ProgramSetupThread extends Thread {
        private static Process programProcess = null;
        private String pname;

        public ProgramSetupThread(String pname) {
            this.pname = pname;
        }

        @Override
        public void run() {
            try {

                programProcess = Runtime.getRuntime().exec(pname);
            } catch (Exception e) {
                // windows下面, 执行的命令如果出现异常，异常信息会乱码，尝试用了GBK等编码，还是不行
                logger.error(e);
            }
        }
    }

    private static class DriverThread extends Thread {
        private Vector<File> imageQueue;
        private Vector<File> totalQueue = new Vector<File>();

        public DriverThread(Vector<File> imageQueue) {
            this.imageQueue = imageQueue;
        }

        @Override
        public void run() {
            File currentDir = new File(
                    "C:\\Users\\Public\\LumanData\\images\\scannedImages");
            int count = 0;
            while (true) {
                File[] images = currentDir.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        if (file.isFile()
                                && (file.getName().endsWith(".jpg")
                                        || file.getName().endsWith(".JPG")
                                        || file.getName().endsWith(".JPEG")
                                        || file.getName().endsWith(".jpeg")
                                        || file.getName().endsWith(".png") || file
                                        .getName().endsWith(".PNG"))) {
                            return true;
                        }
                        return false;
                    }
                });
                for (File i : images) {
                    boolean found = false;
                    for (File j : totalQueue) {
                        if (i.getAbsolutePath().equals(j.getAbsolutePath())) {
                            found = true;
                        }
                    }

                    if (!found) {
                        System.out.println(String.format(
                                "Found image %s, added to queue.",
                                i.getAbsolutePath()));
                        if (count != 0) {
                            imageQueue.add(i);
                        }
                        totalQueue.add(i);
                    } else {
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
                count++;
            }

        }
    }

    public static void main(String[] args) {
        new MicroScope();
    }
}
