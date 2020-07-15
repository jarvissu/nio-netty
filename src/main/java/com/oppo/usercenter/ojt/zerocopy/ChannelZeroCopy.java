package com.oppo.usercenter.ojt.zerocopy;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ChannelZeroCopy implements Copy{

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
        FileChannel inChannel = inputStream.getChannel();
        FileChannel outChannel = outputStream.getChannel();

        long start = System.currentTimeMillis();
        // trasferFrom 实现零拷贝
//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());
//        outChannel.force(true);
        System.out.println("Copyaaa time=[" + (System.currentTimeMillis() - start) + "ms]");
    }
}
