package com.oppo.usercenter.ojt.netty.rpc.simple_rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokerProtocol implements Serializable {

    private static final long serialVersionUID = 4645660461088574095L;

    private String serviceName;

    private String methodName;

    private Class<?>[] paramTypes;

    private Object[] paramValues;
}
