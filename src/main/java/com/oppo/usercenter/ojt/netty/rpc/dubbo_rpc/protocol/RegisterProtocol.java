package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterProtocol implements Serializable {

    private static final long serialVersionUID = 5513009275932925534L;

    private String host;

    private int port;

    private String serviceName;

    private RegisterTypeEnum registerType;
}
