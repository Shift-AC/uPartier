package shiftac.github.com.upartier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.github.shiftac.upartier.data.NoSuchPostException;
import com.github.shiftac.upartier.data.Post;

import java.io.IOException;

/**
 * Created by shirley on 26/11/2017.
 */

public class PostOneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_one);
        //Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
        //Post posts = (Post)bundle.getSerializable("post");
        //Button btn = (Button) findViewById(R.id.btnPost_1);
        //btn.setText(posts.name.toString());

    }
    public void getPosts() throws IOException, NoSuchPostException {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Post posts = (Post)bundle.getSerializable("post");
        Button btn = (Button) findViewById(R.id.btnPost_1);
        btn.setText(posts.name.toString());
    }

    public void creatPost_1(View view){
        Intent intent = new Intent();
        intent.setClass(PostOneActivity.this,SendPostActivity.class);
        startActivity(intent);
    }
    public void joinPost(View view){
        Intent intent = new Intent();
        intent.setClass(PostOneActivity.this,JoinPostActivity.class);
        startActivity(intent);
    }
}
