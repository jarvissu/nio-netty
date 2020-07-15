package com.oppo.usercenter.ojt.zerocopy;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static void main(String[] args) throws IOException {
        String src = "D:\\OPPO_WORK\\oppo-ojt\\zerocopy\\DingTalk_v5.0.6.114.exe";
        String dest = "D:\\OPPO_WORK\\oppo-ojt\\zerocopy\\DingTalk_v5.0.6.114.exe.bak";

//        new StreamCopy().copy(src, dest);          // Copy time=[1933ms]
        new ChannelZeroCopy().copy(src, dest);       //Copyaaa time=[455ms]
    }
}
