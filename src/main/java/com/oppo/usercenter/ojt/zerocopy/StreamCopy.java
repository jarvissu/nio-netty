package com.oppo.usercenter.ojt.zerocopy;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StreamCopy implements Copy{

    private static final int DEFAULT_SIZE = 1024;

    @Override
    public void copy(String src, String dest) throws IOException {

        if (StringUtils.isAnyBlank(src, dest)) {
            System.out.println("参数不正确：无法实现文件复制操作！！！");
            return;
        }
        File srcFile = new File(src);
        File destFile = new File(dest);
        if (!srcFile.exists()) {
            System.out.println("源文件不存在！！");
            return;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileInputStream inputStream = new FileInputStream(srcFile);
        FileOutputStream outputStream = new FileOutputStream(destFile);
        byte[] buffer = new byte[DEFAULT_SIZE];

        long start = System.currentTimeMillis();
        while (true) {
            int readLength = inputStream.read(buffer);
            if (readLength <= 0) {
                break;
            }

            outputStream.write(buffer);
        }

        outputStream.flush();
        System.out.println("Copy time=[" + (System.currentTimeMillis() - start) + "ms]");
    }
}
