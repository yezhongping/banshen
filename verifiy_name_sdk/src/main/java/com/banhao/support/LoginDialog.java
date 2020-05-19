package com.banhao.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.banhao.support.util.DBHelper;
import com.banhao.support.util.SharedPreferencesUtil;

import static com.banhao.support.util.DBHelper.PASSWORD;
import static com.banhao.support.util.DBHelper.USERNAME;
import static com.banhao.support.util.DBHelper.USER_TABLE;

/**
 * Created by zhongpingye on 2020/3/16.
 */

public class LoginDialog extends Dialog {
    EditText account, password;
    Button loginBtn;
    private ILoginDialogDelegate _delegate;
    Activity activity;
    TextView Register;
    private DBHelper dbHelper;

    public LoginDialog(Context context, ILoginDialogDelegate delegate) {
        super(context, R.style.custom_dialog_style);
        activity = (Activity)context;
        _delegate = delegate;
        dbHelper = new DBHelper(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.login);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        Register = findViewById(R.id.registerButton);
        Register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        Register.getPaint().setAntiAlias(true);//抗锯齿

        if(!CheckIsDataAlreadyInDBOrNot("zongyi")){
            RegisterUserInfo("zongyi", "123456");
        }
        if(SharedPreferencesUtil.getRecord(activity,"account")!=null)
        {
            account.setText(SharedPreferencesUtil.getRecord(activity,"account"));
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(account.getText()))
                {
                    Toast.makeText(activity,"请输入账号", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText()))
                {
                    Toast.makeText(activity,"请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                String accountText = account.getText().toString();
                String passWordText = password.getText().toString();

                if (!isRegister(accountText)){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "账号不存在，请注册账号后登陆。", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                if (isLogin(accountText, passWordText)) {
                    SharedPreferencesUtil.setRecord(activity,"account", accountText);
                    LoginDialog.this.dismiss();
                    _delegate.onLoginButtonClicked(accountText);
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RegisterDialog registerDialog = new RegisterDialog(activity);
                registerDialog.show();
            }
        });
    }

    private boolean isLogin(String account, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from " + USER_TABLE + " where " + USERNAME + "=? and " + PASSWORD + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{account, password});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    private boolean isRegister(String account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from " + USER_TABLE + " where " + USERNAME + "=? ";
        Cursor cursor = db.rawQuery(sql, new String[]{account});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    private void RegisterUserInfo(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        db.insert(USER_TABLE, null, values);
        db.close();
    }

    private boolean CheckIsDataAlreadyInDBOrNot(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from "+USER_TABLE+" where "+USERNAME+"=?";
        Cursor c = db.rawQuery(sql, new String[]{username});
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }
}
