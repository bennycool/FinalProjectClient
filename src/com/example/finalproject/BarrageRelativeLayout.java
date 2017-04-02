package com.example.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

public class BarrageRelativeLayout extends RelativeLayout {

    private Context mContext;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==RANDOM_SHOW) {
                String text = texts.get(random.nextInt(texts.size()));
                BarrageTextItem item = new BarrageTextItem(text);
                showBarrageItem(item);

                //ÿ����Ļ�����ļ��ʱ�����
                int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
                this.sendEmptyMessageDelayed(RANDOM_SHOW, duration);
            }


        }
    };

    public static int RANDOM_SHOW=0x0a1;
    public static int SEQ_SHOW=0x0a2;

    private Random random = new Random(System.currentTimeMillis());
    private static final long BARRAGE_GAP_MIN_DURATION = 1000;//������Ļ����С���ʱ��
    private static final long BARRAGE_GAP_MAX_DURATION = 2000;//������Ļ�������ʱ��
    private int maxSpeed = 10000;//�ٶȣ�ms
    private int minSpeed = 5000;//�ٶȣ�ms
    private int maxSize = 30;//���ִ�С��dp
    private int minSize = 15;//���ִ�С��dp

    private int totalHeight = 0;
    private int lineHeight = 0;//ÿһ�е�Ļ�ĸ߶�
    private int totalLine = 0;//��Ļ������

    private LinkedList<String> texts = null;

    public BarrageRelativeLayout(Context context) {
        this(context, null);
    }

    public BarrageRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();
    }

    private void init() {
        texts = new LinkedList<String>();
    }

    public void show(int type){
        int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
        mHandler.sendEmptyMessageDelayed(type, duration);
    }

    //��ʾһ����Ļ�ı�
    //�൱�ڸ���Ļ��������Դ
    public void setBarrageTexts(LinkedList<String> texts) {
        this.texts = texts;
    }

    //ͷ����һ��λ��׷�ӣ����µġ�
    public void addBarrageText(String text) {
        this.texts.add(0,text);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        totalHeight = getMeasuredHeight();
        lineHeight = getLineHeight();
        totalLine = totalHeight / lineHeight;
    }

    private void showBarrageItem(final BarrageTextItem item) {
        int leftMargin = this.getRight() - this.getLeft() - this.getPaddingLeft();
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = item.verticalPos;
        this.addView(item.textView, params);
        Animation anim = generateTranslateAnim(item, leftMargin);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.textView.clearAnimation();
                BarrageRelativeLayout.this.removeView(item.textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        item.textView.startAnimation(anim);
    }

    private TranslateAnimation generateTranslateAnim(BarrageTextItem item, int leftMargin) {
        TranslateAnimation anim = new TranslateAnimation(leftMargin, -item.textMeasuredWidth, 0, 0);
        anim.setDuration(item.moveSpeed);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * ����TextView���ַ����ĳ���
     *
     * @param text Ҫ������ַ���
     * @param Size �����С
     * @return TextView���ַ����ĳ���
     */
    public float getTextWidth(BarrageTextItem item, String text, float Size) {
        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * ���ÿһ�е�Ļ�����߶�
     *
     * @return
     */
    private int getLineHeight() {
        BarrageTextItem item = new BarrageTextItem();

        //���ݽ�ȥһ���ǿ��ַ�����Ŀ����Ϊ�˻��һ������ֵ
        String tx = "no null data";

        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(maxSize);

        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(tx, 0, tx.length(), bounds);
        return bounds.height();
    }

    //��Ļ��һ���ı�item
    public class BarrageTextItem {
        public TextView textView;
        public int textColor;
        public String text;
        public int textSize;
        public int moveSpeed;//�ƶ��ٶ�
        public int verticalPos;//��ֱ������ʾ��λ��
        public int textMeasuredWidth;//������ʾռ�ݵĿ��

        public BarrageTextItem() {

        }

        public BarrageTextItem(String text) {
            this.textSize = (int) (minSize + (maxSize - minSize) * Math.random());
            this.text = text;
            this.textColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            this.textView = new TextView(mContext);
            textView.setText(text);
            textView.setTextSize(textSize);
            textView.setTextColor(textColor);
            textMeasuredWidth = (int) getTextWidth(this, text, textSize);
            moveSpeed = (int) (minSpeed + (maxSpeed - minSpeed) * Math.random());
            if (totalLine == 0) {
                totalHeight = getMeasuredHeight();
                lineHeight = getLineHeight();
                totalLine = totalHeight / lineHeight;
            }
            verticalPos = random.nextInt(totalLine) * lineHeight;
        }
    }
}
