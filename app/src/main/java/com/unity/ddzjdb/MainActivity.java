package com.unity.ddzjdb;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.banhao.support.ICertificationDialogDelegate;
import com.banhao.support.IOnlineTimeDelegate;
import com.banhao.support.OnlineTime;
import com.banhao.support.VerifiyAdapter;
import com.banhao.support.util.ToastUtil;

import java.util.List;


public class MainActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        getDataFromBrowser();

        OnlineTime.instance().initOnlineTime(this, false, new IOnlineTimeDelegate() {
            @Override
            public void onTimeOver() {
                Log.w("yyyyyy", "onTimeOver: "+ "时间到了");
            }
        });


     }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineTime.instance().stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OnlineTime.instance().startTimer();
      //  OnlineTime.instance().cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getDataFromBrowser() {
        Log.e("yyyyyy'", "jinru");
        Uri data = getIntent().getData();
        if (data != null){
            try {
//                String scheme = data.getScheme();
//                String host = data.getHost();
                List<String> params = data.getPathSegments();
                // 从网页传过来的数据
                String[] all=params.get(0).split("&");
                String[] part = all[0].split("=");
                String testId = part[1];
                Log.e("yyyyyy: ", testId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onShowLogin(View view) {
        VerifiyAdapter.instance().showLogin(this, true, new ICertificationDialogDelegate() {
            @Override
            public void onSuccess(final String cAccount ,final String age) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"验证成功: "+ age + " ,账号ID: " + cAccount, Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onFailed(final String cAccount ,final String age) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"验证失败: "+ age + " ,账号ID: " + cAccount, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    public void onStartTime(View view) {
        //OnlineTime.instance().startTimer();
        ToastUtil.show(this,"你好", 10000);
        Log.d("yyyyyyy", "onStartTime: " + OnlineTime.instance().getTimeCount());
    }

    public void onStoptime(View view) {
        OnlineTime.instance().stopTimer();
    }

    public void onShowShare(View view) {
       // ShareAdapter.instance().shareWx(this,"wx58938acad693164f","http://ll.zongyiforum.com/web/share/?id=1979193","斗地主", "好玩的游戏",0);
    }

    public void onShowQQShare(View view) {

    }

}
