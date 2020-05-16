package com.banhao.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.banhao.support.util.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhongpingye on 2020/04/21.
 */

public class OnlineTime {
    private boolean isLive;
    private boolean isInit;
    private boolean isPop;
    private int timeCount = 0;
    private int timeOverCount ;
    private IOnlineTimeDelegate _delegate;
    private Activity _activity;

    private static OnlineTime _instance;
    public static OnlineTime instance(){
        if (_instance == null){
            _instance = new OnlineTime();
        }
        return _instance;
    }
    private OnlineTime(){

    }

    private Timer mTimer;
    // 定时任务
    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            if (isLive){
                timeCount = timeCount + 1;
                Log.d("yyyyy", "run: "+timeCount);
                if (isPop){
                    if (timeCount == 5220){
                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ToastDialog dialog = new ToastDialog(_activity,"您的累计在线时间即将到达1.5小时，请您做好下线的准备(一分钟后消失)。");
                                dialog.setCancelable(false);
                                dialog.show();
                                if (dialog.isShowing()){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    },60*1000);
                                }
                            }
                        });
                    }else if(timeCount == timeOverCount){
                        _delegate.onTimeOver();
                        mTimer.cancel();
                        showExitGame("您的累计在线时长已满1.5小时，系统将自动结束您的游戏。");
                    }
                }else{
                    if (timeCount == timeOverCount)
                    {
                        _delegate.onTimeOver();
                        mTimer.cancel();
                    }
                }
            }
        }
    };

    public void initOnlineTime(Activity activity, boolean isPopToast, IOnlineTimeDelegate delegate) {
        if(!isInit){
            _activity = activity;
            _delegate = delegate;
            isPop = isPopToast;
            timeOverCount = 5400;
            long timeStampNow = System.currentTimeMillis();
            long timeStamoBefore = SharedPreferencesUtil.getLongValue(_activity, "local_history", "timeStamp",0L);

            if(isTheSameDay(timeStampNow, timeStamoBefore)){
                timeCount = SharedPreferencesUtil.getIntValue(_activity, "local_history", "OLTimeCount",0);
            }else {
                timeCount = 0;
            }

            SharedPreferencesUtil.putLongValue(_activity,"local_history", "timeStamp",timeStampNow);

            if (isPopToast){
                if(timeCount >= timeOverCount) {
                    showExitGame("您今天的游戏时长已结束，无法再登录游戏。");
                    return;
                }else if (isCurrentInTimeScope(22,0,8,0)){
                    showExitGame("由于您是未成年人，在每日22时至次日8时无法登录游戏。");
                }
            }

            if (timeCount < timeOverCount){
                mTimer = new Timer();
                mTimer.schedule(mTask, 0, 1000);
                isInit = true;
            }
        }
    }


    public void startTimer(){
        isLive = true;
    }

    public void stopTimer() {
        isLive = false;
        if(_activity != null){
            SharedPreferencesUtil.putIntValue(_activity, "local_history","OLTimeCount", timeCount);
        }
    }

    public void cancelTimer() {
        if (mTimer != null){
            mTimer.cancel();
        }
    }

    public int getTimeCount() {
        return timeCount;
    }

    private void showExitGame(final String content){
        if (_activity != null){
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog exitDialog = new AlertDialog.Builder(_activity).create();
                    exitDialog.setTitle("注意");
                    exitDialog.setMessage(content);
                    exitDialog.setButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _activity.finish();
                        }
                    });
                    exitDialog.show();
                    exitDialog.setCancelable(false);
                }
            });

        }
    }


    private boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();
        Time now = new Time();
        now.set(currentTimeMillis);
        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;
        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;
        // 跨天的特殊情况(比如22:00-8:00)
        if (!startTime.before(endTime)) {
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            //普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;
    }

    private boolean isTheSameDay(long l1, long l2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(l1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(l2);
        return calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE);
    }

}
