package com.banhao.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.banhao.support.util.Md5Util;
import com.banhao.support.util.SharedPreferencesUtil;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhongpingye on 2020/3/16.
 */

public class CertificationDialog extends Dialog {
    private ICertificationDialogDelegate _delegate;
    Activity activity;
    EditText number,name;
    Button certificationBtn;
    private OkHttpClient mOkHttpClient;
    private boolean _isVerifiy;

    public CertificationDialog(Context context, boolean isVerifiy, ICertificationDialogDelegate delegate) {
        super(context, R.style.custom_dialog_style);
        activity = (Activity)context;
        _delegate = delegate;
        _isVerifiy = isVerifiy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.certification);
        number = findViewById(R.id.number);
        name = findViewById(R.id.name);
        certificationBtn= findViewById(R.id.certificationButton);
        certificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isIDCode(number.getText().toString())){
                    Toast.makeText(activity,"请输入正确的身份证号码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isChinese(name.getText().toString()))
                {
                    Toast.makeText(activity,"请输入正确的身份证姓名", Toast.LENGTH_LONG).show();
                    return;
                }

                final String cerAccount = SharedPreferencesUtil.getRecord(activity,"account");

                if(_isVerifiy) {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                mOkHttpClient = new OkHttpClient();
                                FormBody.Builder builder = new FormBody.Builder();
                                builder.add("realName", name.getText().toString());
                                builder.add("cardNo", number.getText().toString());
                                String md5 = name.getText().toString() + number.getText().toString() +  "_" +"f419763dfed3a4bd";
                                builder.add("sign", Md5Util.encode(md5));
                                RequestBody formBody = builder.build();
                                String requestUrl = "https://wxm.syzygame.com/dc1/realName/check";
                                final Request request = new okhttp3.Request.Builder().url(requestUrl).post(formBody).build();
                                final Call call = mOkHttpClient.newCall(request);
                                Response response = call.execute();
                                if (response.isSuccessful()) {
                                    String _response  = response.body().string();
                                    Log.e("yyyyyy", "response ----->" + _response);
                                    try {
                                        JSONObject responseJson = new JSONObject(_response);
                                        JSONObject dataJson = responseJson.getJSONObject("data");
                                        boolean check = dataJson.getBoolean("check");
                                        if(check)
                                        {
                                            String age = dataJson.getString("age");
                                            if (Integer.parseInt(age) >= 18){
                                                _delegate.onSuccess(cerAccount , age);
                                            }else
                                            {
                                                _delegate.onFailed(cerAccount , age);
                                            }
                                            CertificationDialog.this.dismiss();
                                            SharedPreferencesUtil.setRecord(activity,"cerAccount",cerAccount);
                                            SharedPreferencesUtil.setRecord(activity,"age", age);
                                        }else{
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity,"验证失败: 请检查身份证号码和姓名是否匹配。", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("yyyyyy", e.toString());
                            }
                        }
                    }).start();
                }else {
                    _delegate.onSuccess(cerAccount , "20");
                    CertificationDialog.this.dismiss();
                    SharedPreferencesUtil.setRecord(activity,"cerAccount",cerAccount);
                    SharedPreferencesUtil.setRecord(activity,"age", "20");
                }
            }
        });
    }

    private boolean isIDCode(String text) {
        String regx1 = "[0-9]{17}X";
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx1)|| text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }

    private boolean isChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }



}
