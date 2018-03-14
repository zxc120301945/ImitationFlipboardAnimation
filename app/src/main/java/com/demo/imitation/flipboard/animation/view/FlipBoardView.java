package com.demo.imitation.flipboard.animation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.demo.imitation.flipboard.animation.R;

public class FlipBoardView extends ImageView {
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Bitmap bitmap;
  private Camera camera = new Camera();
  int degreeZ;
  int degreeY;
  int degreeY2;
  //中间旋转动画
  private ObjectAnimator animator = ObjectAnimator.ofInt(this, "degreeZ", 0, 270);
  //第一段折起动画
  private ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "degreeY", 0, -45);
  //最后一段折起动画
  private ObjectAnimator animator2 = ObjectAnimator.ofInt(this, "degreeY2", 0, -45);
  AnimatorSet animatorSet;

  public FlipBoardView(Context context) {
    super(context);
  }

  public FlipBoardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FlipBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  {
    //糊脸修正，给camera 做z轴距离适配，避免绘制糊脸
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    float newZ = -displayMetrics.density * 6;
    camera.setLocation(0, 0, newZ);

    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.youcai_icon);
    animator.setDuration(1000);
    animator.setStartDelay(500);
    animator.setInterpolator(new LinearOutSlowInInterpolator());
    animator1.setDuration(800);
    animator1.setStartDelay(500);
    animator1.setInterpolator(new LinearInterpolator());
    animator2.setDuration(500);
    animator2.setStartDelay(500);
    animator2.setInterpolator(new LinearInterpolator());
    animatorSet = new AnimatorSet();
    animatorSet.playSequentially(animator1, animator, animator2);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        degreeZ = 0;
        degreeY = 0;
        degreeY2 = 0;
        animatorSet.start();
      }
    });
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    animatorSet.start();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    animatorSet.end();
  }

  public void startAnimator() {
    if (animatorSet != null) {
      animatorSet.start();
    }
  }

  public void stioAnimator() {
    if (animatorSet != null) {
      animatorSet.end();
    }
  }


  @SuppressWarnings("unused")
  public void setDegreeZ(int degreeZ) {
    this.degreeZ = degreeZ;
    invalidate();
  }

  @SuppressWarnings("unused")
  public void setDegreeY(int degreeY) {
    this.degreeY = degreeY;
    invalidate();
  }

  public void setDegreeY2(int degreeY2) {
    this.degreeY2 = degreeY2;
    invalidate();
  }

  /**
   * 原理：从折 线分为两部分绘制，其实是绘制了两个bitmap 一个动一个不动 然后截取拼凑
   *
   * @param canvas ca
   */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();
    int centerX = getWidth() / 2;
    int centerY = getHeight() / 2;
    int x = centerX - bitmapWidth / 2;
    int y = centerY - bitmapHeight / 2;

    canvas.save();
    camera.save();
    canvas.translate(centerX, centerY);
    canvas.rotate(-degreeZ);
    camera.rotateY(degreeY);
    camera.applyToCanvas(canvas);
    canvas.clipRect(0, -centerY, centerX, centerY);
    canvas.rotate(degreeZ);
    canvas.translate(-centerX, -centerY);
    canvas.drawBitmap(bitmap, x, y, paint);
    camera.restore();
    canvas.restore();


//       不动的另一部分
    canvas.save();
    camera.save();
    canvas.translate(centerX, centerY);
    canvas.rotate(-degreeZ);
    canvas.clipRect(-centerX, -centerY, 0, centerY);
    canvas.rotate(degreeZ);
    camera.rotateX(degreeY2);
    camera.applyToCanvas(canvas);
    canvas.translate(-centerX, -centerY);
    canvas.drawBitmap(bitmap, x, y, paint);
    camera.restore();
    canvas.restore();

  }

  /**
   * onMeasure() 日常重写
   *
   * @param widthMeasureSpec  w
   * @param heightMeasureSpec h
   */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width;
    int height;
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    if (widthMode == MeasureSpec.EXACTLY) {
      width = widthSize;
    } else {
      width = getPaddingLeft() + bitmap.getWidth() + getPaddingRight();
    }
    if (heightMode == MeasureSpec.EXACTLY) {
      height = heightSize;
    } else {
      height = getPaddingTop() + bitmap.getHeight() + getPaddingBottom();
    }
    setMeasuredDimension(width, height);
  }
}
