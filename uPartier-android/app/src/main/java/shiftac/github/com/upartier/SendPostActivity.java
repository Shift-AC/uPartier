package shiftac.github.com.upartier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by shirley on 28/11/2017.
 */

public class SendPostActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendpost);
    }
    public void sendPostNext(View view){
        Intent intent = new Intent();
        intent.setClass(SendPostActivity.this,PostOneActivity.class);
        startActivity(intent);
    }
}
