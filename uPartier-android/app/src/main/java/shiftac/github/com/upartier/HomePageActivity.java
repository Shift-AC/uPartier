package shiftac.github.com.upartier;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

//import com.github.shiftac.upartier.data.Block;
import com.hjm.bottomtabbar.BottomTabBar;

import java.io.IOException;


/**
 * Created by eric on 15/11/2017.
 */

public class HomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        BottomTabBar mBottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);
        mBottomTabBar.init(getSupportFragmentManager())
            .setImgSize(70,70)
            .setFontSize(16)
            .setTabPadding(4,6,10)
            .setChangeColor(Color.parseColor("#FF4081"),Color.LTGRAY)
            .addTabItem("首页", R.mipmap.homepage, FragmentOne.class)
            .addTabItem("联系人", R.mipmap.social_relations, FragmentTwo.class)
            .addTabItem("我", R.mipmap.profile, FragmentThree.class)
            //.addTabItem("第四项", R.mipmap.ic_launcher, FragmentFour.class)
            .setTabBarBackgroundColor(Color.WHITE)
            .isShowDivider(false);

    }
}
