package com.banhao.support;

/**
 * Created by zhongpingye on 2020/3/16.
 */

public interface ICertificationDialogDelegate {
    void onSuccess(String cAccount ,String age);
    void onFailed(String cAccount ,String age);
}
