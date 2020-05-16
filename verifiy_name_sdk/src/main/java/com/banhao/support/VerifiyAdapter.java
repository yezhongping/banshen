package com.banhao.support;

import android.app.Activity;

import com.banhao.support.util.SharedPreferencesUtil;

/**
 * Created by zhongpingye on 2020/3/16.
 */

public class VerifiyAdapter {
    private static VerifiyAdapter _instance;
    public static VerifiyAdapter instance(){
        if (_instance == null){
            _instance = new VerifiyAdapter();
        }
        return _instance;
    }
    private VerifiyAdapter(){

    }

    public void showLogin(final Activity activity, final boolean isVerifiy, final ICertificationDialogDelegate iCertificationDialogDelegate)
    {
        LoginDialog loginDialog = new LoginDialog(activity, new ILoginDialogDelegate() {
            @Override
            public void onLoginButtonClicked(String account) {
                if (account != null){
                    if (account.equals(SharedPreferencesUtil.getRecord(activity,"cerAccount"))){
                        iCertificationDialogDelegate.onSuccess(account ,SharedPreferencesUtil.getRecord(activity,"age"));
                    }else{
                        showCertification(activity, isVerifiy, iCertificationDialogDelegate);
                    }
                }

            }
        });

        loginDialog.setCancelable(false);
        loginDialog.show();
    }

    public void showCertification(Activity activity, final boolean isVerifiy, final ICertificationDialogDelegate iCertificationDialogDelegate)
    {
        final CertificationDialog certificationDialog = new CertificationDialog(activity, isVerifiy, new ICertificationDialogDelegate() {
            @Override
            public void onSuccess(String cAccount,String age) {
                iCertificationDialogDelegate.onSuccess(cAccount,age);
            }

            @Override
            public void onFailed(String cAccount, String age) {
                iCertificationDialogDelegate.onFailed(cAccount,age);
            }
        });
        certificationDialog.setCancelable(false);
        certificationDialog.show();
    }

}
