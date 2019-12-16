package com.alipay.api;


/**
 * Created by bruce on 2017/12/25.
 */
public interface AlipayClient {

    <T extends AlipayResponse> void execute(AlipayRequest<T> request, AlipayCallBack callBack);

}
