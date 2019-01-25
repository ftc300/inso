package com.inso.notify;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.HttpAPI;
import com.inso.entity.http.Information;
import com.inso.plugin.tools.DensityUtils;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class NotifyFrg extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<Information.ItemsBean> data = new ArrayList<>();

    public static NotifyFrg getInstance(){
        return new NotifyFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_mall;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("小喇叭");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpAPI api = retrofit.create(HttpAPI.class);
        Call<Information> call = api.getInformation();
        call.enqueue(new Callback<Information>() {
            @Override
            public void onResponse(Call<Information> call, Response<Information> response) {
                data = response.body().getItems();
                mRecyclerView.setAdapter(new CommonAdapter<Information.ItemsBean>(mActivity, R.layout.item_xiaolaba, data) {
                    @Override
                    protected void convert(ViewHolder holder, final Information.ItemsBean item, int position) {
                        holder.setText(R.id.title, item.getTitle());
                        holder.setText(R.id.desc, item.getDescription());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(item.getCreated_at()*1000);
                        holder.setText(R.id.date, new SimpleDateFormat("MM月dd日 HH:mm").format(calendar.getTime()));
                        final ImageView img =  holder.getView(R.id.img);
                        Picasso.get()
                                .load(item.getCover())
                                .placeholder(R.drawable.pic_default_large)
                                .error(R.drawable.pic_error)
                                .transform(new RoundedCornersTransformation(DensityUtils.dp2px(mContext,8),0, RoundedCornersTransformation.CornerType.TOP))
                                .fit()
                                .into(img);
                        holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle args = WebFragment.configArgs(item.getTitle(), item.getLink(), null);
                                CommonAct.start(mActivity, WebFragment.class, args);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<Information> call, Throwable t) {
                L.d(t.toString());
            }
        });
    }

    static class RoundedCornersTransformation   implements Transformation {

        public enum CornerType {
            ALL,
            TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
            TOP, BOTTOM, LEFT, RIGHT,
            OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
            DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
        }

        private int mRadius;
        private int mDiameter;
        private int mMargin;
        private CornerType mCornerType;

        public RoundedCornersTransformation(int radius, int margin) {
            this(radius, margin, CornerType.ALL);
        }

        public RoundedCornersTransformation(int radius, int margin, CornerType cornerType) {
            mRadius = radius;
            mDiameter = radius * 2;
            mMargin = margin;
            mCornerType = cornerType;
        }

        @Override public Bitmap transform(Bitmap source) {

            int width = source.getWidth();
            int height = source.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            drawRoundRect(canvas, paint, width, height);
            source.recycle();

            return bitmap;
        }

        private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
            float right = width - mMargin;
            float bottom = height - mMargin;

            switch (mCornerType) {
                case ALL:
                    canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                    break;
                case TOP_LEFT:
                    drawTopLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case TOP_RIGHT:
                    drawTopRightRoundRect(canvas, paint, right, bottom);
                    break;
                case BOTTOM_LEFT:
                    drawBottomLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case BOTTOM_RIGHT:
                    drawBottomRightRoundRect(canvas, paint, right, bottom);
                    break;
                case TOP:
                    drawTopRoundRect(canvas, paint, right, bottom);
                    break;
                case BOTTOM:
                    drawBottomRoundRect(canvas, paint, right, bottom);
                    break;
                case LEFT:
                    drawLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case RIGHT:
                    drawRightRoundRect(canvas, paint, right, bottom);
                    break;
                case OTHER_TOP_LEFT:
                    drawOtherTopLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case OTHER_TOP_RIGHT:
                    drawOtherTopRightRoundRect(canvas, paint, right, bottom);
                    break;
                case OTHER_BOTTOM_LEFT:
                    drawOtherBottomLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case OTHER_BOTTOM_RIGHT:
                    drawOtherBottomRightRoundRect(canvas, paint, right, bottom);
                    break;
                case DIAGONAL_FROM_TOP_LEFT:
                    drawDiagonalFromTopLeftRoundRect(canvas, paint, right, bottom);
                    break;
                case DIAGONAL_FROM_TOP_RIGHT:
                    drawDiagonalFromTopRightRoundRect(canvas, paint, right, bottom);
                    break;
                default:
                    canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                    break;
            }
        }

        private void drawTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, mMargin + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin + mRadius, mMargin + mRadius, bottom), paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
        }

        private void drawTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, mMargin + mDiameter), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
            canvas.drawRect(new RectF(right - mRadius, mMargin + mRadius, right, bottom), paint);
        }

        private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, mMargin + mDiameter, bottom),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom - mRadius), paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
        }

        private void drawBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
            canvas.drawRect(new RectF(right - mRadius, mMargin, right, bottom - mRadius), paint);
        }

        private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right, bottom), paint);
        }

        private void drawBottomRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right, bottom - mRadius), paint);
        }

        private void drawLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
        }

        private void drawRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
        }

        private void drawOtherTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom - mRadius), paint);
        }

        private void drawOtherTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom - mRadius), paint);
        }

        private void drawOtherBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                    paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right - mRadius, bottom), paint);
        }

        private void drawOtherBottomRightRoundRect(Canvas canvas, Paint paint, float right,
                                                   float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                    paint);
            canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                    paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin + mRadius, right, bottom), paint);
        }

        private void drawDiagonalFromTopLeftRoundRect(Canvas canvas, Paint paint, float right,
                                                      float bottom) {
            canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, mMargin + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right - mDiameter, bottom), paint);
            canvas.drawRect(new RectF(mMargin + mDiameter, mMargin, right, bottom - mRadius), paint);
        }

        private void drawDiagonalFromTopRightRoundRect(Canvas canvas, Paint paint, float right,
                                                       float bottom) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, mMargin + mDiameter), mRadius,
                    mRadius, paint);
            canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, mMargin + mDiameter, bottom),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom - mRadius), paint);
            canvas.drawRect(new RectF(mMargin + mRadius, mMargin + mRadius, right, bottom), paint);
        }

        @Override public String key() {
            return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", diameter="
                    + mDiameter + ", cornerType=" + mCornerType.name() + ")";
        }
    }
}
