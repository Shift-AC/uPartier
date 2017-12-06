package shiftac.github.com.upartier;

/**
 * Created by shirley on 12/11/2017.
 */

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

import com.github.shiftac.upartier.data.Block;
import com.github.shiftac.upartier.data.NoSuchUserException;
import com.github.shiftac.upartier.data.User;
import com.github.shiftac.upartier.data.BString;
import com.github.shiftac.upartier.data.LoginInf;
import java.io.IOException;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            switch (msg.what) {
                case 0:
                    AlertU((String) msg.obj);
                    break;
                case 1:
                    intent.setClass(LogInActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    };

    public void AlertU(String a) {
        /*
        * 弹出对话框
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        //    设置Title的内容
        builder.setTitle(a);
        //    设置Content来显示一个信息
        //btn.setText(phoneNum);
        builder.show();
        //    return;
    }

    LoginInf logInInf = new LoginInf();

    public void LogIn(View view) {
        EditText LogInId = findViewById(R.id.txtLogInStudentId);
        EditText LogInPw = findViewById(R.id.txtLogInPassword);

        String logInAccount = LogInId.getText().toString();
        String logInPassword = LogInPw.getText().toString();


        logInInf.id = Integer.parseInt(logInAccount);
        logInInf.passwd = new BString(logInPassword);
        logInInf.isNewUser = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                User logInUser = new User();
                try {
                    logInUser = User.login(logInInf);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = "没有网络，请检查";
                    handler.sendMessage(msg);
                } catch (NoSuchUserException e) {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = "用户名、密码错误";
                    handler.sendMessage(msg);
                }
            }
        }).start();



        Intent intent = new Intent();
        intent.setClass(LogInActivity.this,HomePageActivity.class);
        startActivity(intent);

    }
}

    /*

    public void LogIn(View view) throws IOException, NoSuchUserException {
        Bundle bundle = this.getIntent().getExtras();
        //int UserID = bundle.getInt("UserID");

        Intent intent = new Intent();
        intent.setClass(LogInActivity.this,HomePageActivity.class);
        bundle.putInt("UserID",logInUser.id);
        intent.putExtras(bundle);
        startActivity(intent);*/

        //Bundle bundle = this.getIntent().getExtras();
        //接收name值
        //String RegisterNickname = bundle.getString("RegisterNickname");
        //String RegisterPassword = bundle.getString("RegisterPassword");
        //String RegisterEmail = bundle.getString("RegisterEmail");

            /*TextView txt1 = (TextView) findViewById(R.id.lblNickname2);
            EditText txt2 = (EditText) findViewById(R.id.txtLogInStudentId);
            EditText txt3 = (EditText) findViewById(R.id.txtLogInPassword);

            txt1.setText(RegisterEmail);
            txt2.setText(RegisterPassword);
            txt3.setText(RegisterNickname);*/
    
