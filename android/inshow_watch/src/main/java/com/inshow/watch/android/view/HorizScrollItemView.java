package com.inshow.watch.android.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.inshow.watch.android.R;
import java.util.ArrayList;

/**
 */
public class HorizScrollItemView extends LinearLayout{

    private View rootView;
    private RecyclerView mRecyclerView;
    private int recyclerviewWidth;
    private Context context;
    private int xDown, xMove;
    private boolean isIntercept;
    private DataAdapter dataAdapter;
    private ArrayList<String> datas = new ArrayList<>();
    public static final int ITEM_NUM = 13; // 每行拥有的Item数, 必须是奇数

    public void setScrollXListener(IScrollX scrollXListener) {
        this.scrollXListener = scrollXListener;
    }

    private IScrollX scrollXListener;
    public HorizScrollItemView(Context context) {
        super(context);
    }


    public HorizScrollItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.watch_step_layout, this, true);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
    }

    public void setDatas(ArrayList<String> datas) {
        this.datas.addAll(datas);
    }

    public void setRecyclerviewWidth(int width) {
        this.recyclerviewWidth = width;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        isIntercept = true;
        if(mRecyclerView.getScrollState()== RecyclerView.SCROLL_STATE_IDLE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isIntercept = false;
                    xDown = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    xMove = x;
                    if (xMove - xDown < 0) {
                        isIntercept = false;
                    } else if (xMove - xDown > 0) {
                        isIntercept = true;
                        mRecyclerView.stopScroll();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return isIntercept;
    }

    public void initList() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        dataAdapter = new DataAdapter(context);
        mRecyclerView.setAdapter(dataAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dx;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断是否是底部
                if(!recyclerView.canScrollHorizontally(1)){
                    for (int i = 0; i < 5 * ITEM_NUM; i++) {
                        datas.add("");
                    }
                    dataAdapter.notifyDataSetChanged();
                }
                if(newState==RecyclerView.SCROLL_STATE_IDLE)
                {
                    if(scrollXListener!=null){
                        scrollXListener.scrollX(Math.abs(dx));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                this.dx = Math.abs(dx);
            }
        });
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.AgeItemViewHolder> {

        private Context context;

        public DataAdapter(Context context) {
            this.context = context;
        }


        @Override
        public AgeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_step_item, parent, false);
            ViewGroup.LayoutParams lp = item.getLayoutParams();
            lp.width = getItemStdWidth();
            return new AgeItemViewHolder(item);
        }

        @Override
        public void onBindViewHolder(AgeItemViewHolder holder, int position) {
        }
        @Override
        public int getItemCount() {
            return datas.size();
        }

        public int getItemStdWidth() {
            return recyclerviewWidth / ITEM_NUM;
        }

        public class AgeItemViewHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;

            public AgeItemViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.tvName);
                mTextView.setTag(this);
            }
        }
    }

    public interface  IScrollX{
        void scrollX(int dx);
    }
}
