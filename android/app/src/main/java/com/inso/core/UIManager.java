package com.inso.core;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseDialogFragment;

public class UIManager {

    private static UIManager instance;

    private UIManager() { }

    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }


    public static void showLogoutConfirmFrg(AppCompatActivity context,final IConfirm listener){
        new BaseDialogFragment.Builder(context)
                .setContentView(R.layout.dialog_content_logout)
                .setShowTitle(false)
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onConfirm(null);
                    }
                })
                .setWidthMaxDp(600)
                .setShowButtons(true)
                .create()
                .show(context.getSupportFragmentManager(),"logout");
    }

    public static void showChangeUserIconFrg(AppCompatActivity context, final IChangeUserIcon listener){
        String[] content = {"拍照","本地照片"};
        new BaseDialogFragment.Builder(context)
                .setMessages(content)
                .setTitle("修改头像")
                .setTopDivider(false)
                .setOnItemClickListener(new BaseDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClicked(int index) {
                        if(index == 0){
                            listener.takePhoto();
                        }else if(index == 1) {
                            listener.pickPic();
                        }
                    }
                })
                .setWidthMaxDp(600)
                .setShowButtons(false)
                .create()
                .show(context.getSupportFragmentManager(),"changeUserIcon");
    }



    public interface IChangeUserIcon{
        void takePhoto();
        void pickPic();
    }

    public interface IConfirm<T>{
        void onConfirm(T t);
    }


}
