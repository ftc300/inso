package com.inso.watch.baselib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.inso.watch.baselib.R;
import com.inso.watch.baselib.wigets.TitleBar;
import org.greenrobot.eventbus.EventBus;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.inso.watch.baselib.Constants.ARGS_EVENT_BUS;
import static com.inso.watch.baselib.Constants.ARGS_HAS_TITLE;


/**
 * Fragment基类
 */
public class BaseFragment extends Fragment{
    /**
     * Fragment参数
     */
    private Bundle arguments;

    /**
     *  标题栏
     */
    protected TitleBar mTitleBar;

    /**
     *  由getContentView初始化，子类的业务View
     */
    protected View mContentView;

    protected Context mActivity;


    Unbinder unbinder;
    /**
     * 设置无标题显示
     * @return
     */
    public static Bundle configNoTitle() {
        return configTitleArgs(false);
    }
    private boolean mDestroyed;
    protected  void switchTo(Class<?> to){
        Intent intent = new Intent(mActivity,to);
        startActivity(intent);
    }

    protected  void switchToWithEventBus(Class<?> to){
        Intent intent = new Intent(mActivity,to);
        intent.putExtra(ARGS_EVENT_BUS, true);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
        arguments = getArguments();
    }

    /**
     * 设置是否包含标题栏
     * @param hasTitle true有标题栏，false无标题栏
     */
    public static Bundle configTitleArgs(boolean hasTitle) {
        Bundle args = new Bundle();
        args.putBoolean(ARGS_HAS_TITLE, hasTitle);
        return args;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestroyed = false;
        if (arguments != null && arguments.getBoolean(ARGS_EVENT_BUS, false)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean hasTitle = true;
        if (getArguments() != null) {
            hasTitle = getArguments().getBoolean(ARGS_HAS_TITLE, true);
        }
        // 根据参数加载不同的布局文件
        int layoutRes = hasTitle ? R.layout.base_fragment : R.layout.base_fragment_no_title;
        View layout = inflater.inflate(layoutRes, container, false);

        mTitleBar = new TitleBar(layout);
        ViewGroup contentContainer = (ViewGroup) layout.findViewById(R.id.contentContainer);
        mContentView = getContentView();
        if (mContentView == null) {
            final int contentRes = getContentRes();
            if(contentRes > 0) {
                mContentView = View.inflate(mActivity, contentRes, null);
            }
        }

        if (mContentView != null) {
            contentContainer.addView(mContentView, 0);
        }

        unbinder = ButterKnife.bind(this, mContentView);
        // butterKnife初始化控件

        initViewOrData();

        return layout;
    }


    protected void setTitle(int title) {
        mTitleBar.setTitleBar(title);
    }

    protected void setTitle(CharSequence title) {
        mTitleBar.setTitleBar(title);
    }

    protected void setTitle(boolean showBack, int title) {
        mTitleBar.setTitleBar(showBack, title);
    }

    protected void setTitle(boolean showBack, CharSequence title) {
        mTitleBar.setTitleBar(showBack, title);
    }

    protected void setTitleR(boolean showBack, CharSequence title,int rightIcon,View.OnClickListener rightClick) {
        mTitleBar.setTitleBarR(showBack, title,rightIcon,rightClick);
    }
    protected void setTitleL(String leftText,String titleText, View.OnClickListener l) {
        mTitleBar.setLeftTitle(leftText, titleText,l);
    }

    /**
     * 设置具有返回按钮的标题
     * @param title
     */
    protected void setTitleWithBack(CharSequence title) {
        mTitleBar.setTitleBar(true, title);
    }


    /**
     * 获取Fragment需要显示的View
     * @return
     */
    protected View getContentView() {
        return null;
    }

    /**
     * 获取Fragment需要显示的View的资源文件
     * @return
     */
    protected int getContentRes() {
        return 0;
    }

    /**
     * 处理特殊的初始化需求，无需求可不重写
     */
    protected void initViewOrData(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (arguments != null && arguments.getBoolean(ARGS_EVENT_BUS, false)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void finish(){
        ((Activity)mActivity).finish();
    }
}
