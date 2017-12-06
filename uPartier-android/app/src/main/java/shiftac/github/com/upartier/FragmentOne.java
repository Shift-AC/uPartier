package shiftac.github.com.upartier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import com.github.shiftac.upartier.data.BString;
import com.github.shiftac.upartier.data.Block;
import com.github.shiftac.upartier.data.NoSuchBlockException;
import com.github.shiftac.upartier.data.NoSuchPostException;
import com.github.shiftac.upartier.data.Post;


/**
 * Created by eric on 15/11/2017.
 */

public class FragmentOne extends Fragment{

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            switch (msg.what) {
                case 0:
                    Button btn = (Button) getView().findViewById(R.id.btnBlock1);
                    btn.setText((String) msg.obj);
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Block []blocks = Block.fetchBlocks();
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = blocks[0].name.toString();
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "没有网络，请检查";
                    handler.sendMessage(msg);
                }
            }
        }).start();


        Button btn1 = (Button) view.findViewById(R.id.btnBlock1);
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Block blocks = new Block();
                blocks.id = 0;
                blocks.name = new BString("自习专栏");


                @SuppressLint("HandlerLeak")
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("post", (Serializable) blocks.posts.get(0));
                                Intent intent = new Intent(getActivity(),PostOneActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            case 1:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle((String)msg.obj);
                                builder.show();
                                break;

                        }
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            int count = 0;
                            blocks.fetchPosts(count);
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                        catch(IOException e){
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = "没有网络，请检查";
                            handler.sendMessage(msg);
                        }
                        catch (NoSuchBlockException e){
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = "没有相关信息";
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
                Intent intent = new Intent(getActivity(),PostOneActivity.class);
                startActivity(intent);
            }
        });

        Button btn2 = (Button) view.findViewById(R.id.btnBlock2);
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PostTwoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}

