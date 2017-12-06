package shiftac.github.com.upartier;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
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


/**
 * Created by shirley on 12/11/2017.
 */

public class NicknameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
    }
    public void nicknameNext(View view){
        EditText txtNickname = (EditText) findViewById(R.id.txtNickname);
        String nickname = txtNickname.getText().toString();
        if(nickname.length() != 0){
            Bundle bundle = this.getIntent().getExtras();
            String RegisterEmail = bundle.getString("RegisterEmail");
            String RegisterPassword = bundle.getString("RegisterPassword");
            bundle.putString("RegisterEmail", RegisterEmail);
            bundle.putString("RegisterPassword", RegisterPassword);
            bundle.putString("RegisterNickname",nickname);
            Intent intent = new Intent();
            intent.setClass(NicknameActivity.this,RegisterProfileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(NicknameActivity.this);
            //    设置Title的内容
            builder.setTitle("昵称不可以为空哦！");
            //    设置Content来显示一个信息
            //btn.setText(phoneNum);
            builder.show();
            return;
        }
    }
}
