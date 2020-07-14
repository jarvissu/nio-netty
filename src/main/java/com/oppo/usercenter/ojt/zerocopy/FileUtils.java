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
        String src = "D:\\OPPO Work\\OJT\\zerocopy\\jdk-8u251-windows-x64.exe";
        String dest = "D:\\OPPO Work\\OJT\\zerocopy\\jdk-8u251-windows-x64.exe.bak";

        // normal copy
//        new StreamCopy().copy(src, dest);          // Copy time=[3475ms]
        new ChannelZeroCopy().copy(src, dest);       //Copy time=[343ms]
    }
}
