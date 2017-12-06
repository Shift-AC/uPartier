package shiftac.github.com.upartier;

/**
 * Created by shirley on 12/11/2017.
 */

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
import android.app.Dialog;


public class CheckMailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmail);


    }
    public void checkMailNext(View view){
        EditText txtMail = (EditText) findViewById(R.id.txtMail);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        String mailbox = txtMail.getText().toString();
        String password = txtPassword.getText().toString();
        boolean mailboxNotValid = isNotMailBox(mailbox);
        if(!mailboxNotValid && password.length()!=0){
            /*
            * 此处需要将mailbox和password传递到数据库
            *
            * dbHelper = MyDatabaseHelper.getInstance(mContext);
            * db = dbHelper.getWritableDatabase();
            * db.execSQL("INSERT INTO userProfile VALUES('"+mailbox+"', '"+password+"', '"+null+"', '"+null+"' );");
            *
            */

            Bundle bundle = new Bundle();
            bundle.putString("RegisterEmail", mailbox);
            bundle.putString("RegisterPassword", password);
            Intent intent = new Intent();
            intent.setClass(CheckMailActivity.this,NicknameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }
    boolean isNotMailBox(String mailbox){
        String mailSuffix = "@pku.edu.cn";
        if (mailbox.indexOf(mailSuffix) == -1 || mailbox.length() < 12){
            AlertDialog.Builder builder = new AlertDialog.Builder(CheckMailActivity.this);
            //    设置Title的内容
            builder.setTitle("请填写正确的P大邮箱地址！");
            //    设置Content来显示一个信息
            //btn.setText(phoneNum);
            builder.show();
            return true;
        }
        return false;
    }
}
