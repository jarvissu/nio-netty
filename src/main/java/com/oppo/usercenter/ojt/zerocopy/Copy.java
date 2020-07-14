package com.oppo.usercenter.ojt.zerocopy;

import java.io.IOException;

public interface Copy {
    void copy(String src, String dest) throws IOException;
}
