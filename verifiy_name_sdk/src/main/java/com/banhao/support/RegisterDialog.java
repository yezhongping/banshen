package com.banhao.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.banhao.support.util.DBHelper;

import static com.banhao.support.util.DBHelper.PASSWORD;
import static com.banhao.support.util.DBHelper.USERNAME;
import static com.banhao.support.util.DBHelper.USER_TABLE;

/**
 * Created by zhongpingye on 2020/4/24.
 */

public class RegisterDialog extends Dialog implements View.OnClickListener{
    Activity activity;
    private EditText edit_setUserName, edit_setPassword, edit_confirmPassword;
    private DBHelper dbHelper;
    private String userName, password, passwordAgain;
    private Button btn_confirm, btn_close;

    public RegisterDialog(Context context) {
        super(context, R.style.custom_dialog_style);
        activity = (Activity)context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.register);
        btn_confirm = (Button) findViewById(R.id.confirmButton);
        btn_confirm.setOnClickListener(this);
        btn_close = (Button) findViewById(R.id.close_button);
        btn_close.setOnClickListener(this);

        edit_setUserName = (EditText) findViewById(R.id.accountRegister);
        edit_setPassword = (EditText) findViewById(R.id.passwordRegister);
        edit_confirmPassword = (EditText) findViewById(R.id.passwordAgain);
        dbHelper = new DBHelper(activity);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close_button) {
            RegisterDialog.this.dismiss();
        } else if (id == R.id.confirmButton) {
            userName = edit_setUserName.getText().toString();
            password = edit_setPassword.getText().toString().trim();
            passwordAgain = edit_confirmPassword.getText().toString().trim();
            Log.d("yyyyyy+",userName);
            Log.d("yyyyyy++",password);
            Log.d("yyyyyy++",passwordAgain);

            if (TextUtils.isEmpty(edit_setUserName.getText()) || TextUtils.isEmpty(edit_setPassword.getText()) || TextUtils.isEmpty(edit_confirmPassword.getText())) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "不能为空！", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (CheckIsDataAlreadyInDBOrNot(userName)) {
                    Toast.makeText(activity, "该用户已被注册，注册失败！", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(passwordAgain)) {
                        RegisterUserInfo(userName, password);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "注册成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        RegisterDialog.this.dismiss();
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

        }
    }

    private void RegisterUserInfo(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        db.insert(USER_TABLE, null, values);
        db.close();
    }

    private boolean CheckIsDataAlreadyInDBOrNot(String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from "+USER_TABLE+" where "+USERNAME+"=?";
        Cursor c = db.rawQuery(sql, new String[]{phone});
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }


}
