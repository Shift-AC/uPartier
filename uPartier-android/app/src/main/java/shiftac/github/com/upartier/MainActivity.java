package shiftac.github.com.upartier;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        final Button btn = (Button) findViewById(R.id.btnRegister);
        final EditText txtPhone = (EditText) findViewById(R.id.txtPhoneNum);
        final EditText txtPwd = (EditText) findViewById(R.id.txtPassword);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String phoneNum = txtPhone.getText().toString();
                String password = txtPwd.getText().toString();
                if (phoneNum.length() != 11){
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    //    设置Title的内容
                    builder.setTitle("请输入手机号正确格式");
                    //    设置Content来显示一个信息
                    //btn.setText(phoneNum);
                    builder.show();
                }
            }
        );
        */
        Context mContext = this;

    }
    public void mainRegister(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,CheckMailActivity.class);
        startActivity(intent);
    }
    public void mainLogIn(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,LogInActivity.class);
        startActivity(intent);
    }
}
