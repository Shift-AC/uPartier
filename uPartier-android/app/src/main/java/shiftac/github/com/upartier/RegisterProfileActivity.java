package shiftac.github.com.upartier;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import android.graphics.Bitmap;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Message;
import android.os.Handler;

import java.io.IOException;
import java.lang.InterruptedException.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;

import com.github.shiftac.upartier.data.BString;
import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.data.NoSuchUserException;
import com.github.shiftac.upartier.data.PermissionException;
import com.github.shiftac.upartier.data.User;


/**
 * Created by shirley on 12/11/2017.
 */

public class RegisterProfileActivity extends AppCompatActivity {
    private static final String TAG = "RegisterProfileActivity";
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView iv_personal_icon;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerprofile);
        iv_personal_icon = (ImageView) findViewById(R.id.iv_personal_icon);
        //selectDate = (EditText) findViewById(R.id.txtBirthday);
        //selectDate.setOnClickListener(this);
    }


    public void showChoosePicDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        takePicture();
                        break;
                }
            }
        });
        builder.create().show();
    }
    private void takePicture() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            // 需要申请动态权限
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment
                .getExternalStorageDirectory(), "image.jpg");
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= 24) {
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tempUri = FileProvider.getUriForFile(RegisterProfileActivity.this, "shiftac.github.com.upartier", file);
        } else {
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "image.jpg"));
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Log.d(TAG,"setImageToView:"+photo);
            photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            iv_personal_icon.setImageBitmap(photo);
            uploadPic(photo);
        }
    }
    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
            Log.d(TAG,"imagePath:"+imagePath);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void AlertU(String a) {
        /*
        * 弹出对话框
        */

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterProfileActivity.this);
        //    设置Title的内容
        builder.setTitle(a);
        //    设置Content来显示一个信息
        //btn.setText(phoneNum);
        builder.show();
        //    return;
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
                    AlertU("注册成功");
                    intent.setClass(RegisterProfileActivity.this, LogInActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void registerProfileNext(View view){
        EditText txtStudentId = (EditText) findViewById(R.id.txtStudentId);
        String studentId = txtStudentId.getText().toString();
        if(studentId.length() == 10) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterProfileActivity.this);
            //    设置Title的内容
            builder.setTitle("注册成功！");
            //    设置Content来显示一个信息
            //btn.setText(phoneNum);
            builder.show();

            Bundle bundle = this.getIntent().getExtras();
            //接收name值

            EditText registerId = (EditText) findViewById(R.id.txtStudentId);

            final String RegisterNickname = bundle.getString("RegisterNickname");
            final String RegisterPassword = bundle.getString("RegisterPassword");
            final String RegisterEmail = bundle.getString("RegisterEmail");
            final String RegisterId = registerId.getText().toString();

            /*bundle.putString("RegisterEmail", RegisterEmail);
            bundle.putString("RegisterPassword", RegisterPassword);
            bundle.putString("RegisterNickname",RegisterNickname);*/

            new Thread(new Runnable() {
                @Override
                public void run() {
                    User registerUser = new User();
                    LoginInf registerInf = new LoginInf();
                    try {
                        registerInf.id = Integer.parseInt(RegisterId);
                        registerInf.passwd = new BString(RegisterPassword);
                        registerInf.isNewUser = true;

                        registerUser = User.login(registerInf);

                        registerUser.mailAccount = new BString(RegisterEmail);
                        registerUser.nickname = new BString(RegisterNickname);
                        registerUser.modify();

                    }catch (IOException e) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "没有网络，请检查";
                        handler.sendMessage(msg);
                    } catch (NoSuchUserException e) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "注册失败";
                        handler.sendMessage(msg);
                    } catch (PermissionException e) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "没有修改权限";
                        handler.sendMessage(msg);
                    }
                }
            }).start();

            Intent intent = new Intent();
            intent.setClass(RegisterProfileActivity.this, LogInActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}
