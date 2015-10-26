package com.medakk.alternote.uihelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeToDismissListViewListener implements View.OnTouchListener {

    private final ListView listView;
    private final DismissCallback dismissCallback;

    private final int slop, minFlingVelocty, maxFlingVelocity;
    private final long animationTime;

    private boolean paused = false;
    private int viewWidth = 1;
    private View downView;
    private VelocityTracker velocityTracker;
    private float downX, downY;
    private int downPosition;
    private boolean swiping;
    private int swipingSlop;
    private int dismissAnimationRefCount;
    private List<PendingDismissData> pendingDismisses = new ArrayList<>();

    public interface DismissCallback {
        boolean canDismiss(int position);
        void onDismiss(ListView lv, int[] reverseSortedPositions);
    }

    public SwipeToDismissListViewListener(ListView listView, DismissCallback dismissCallback) {
        this.listView = listView;
        this.dismissCallback = dismissCallback;

        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());

        slop = vc.getScaledTouchSlop();
        minFlingVelocty = vc.getScaledMinimumFlingVelocity();
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        animationTime = listView.getContext().getResources().
                getInteger(android.R.integer.config_shortAnimTime);
    }

    public void setEnabled(boolean enabled) {
        paused = !enabled;
    }

    public AbsListView.OnScrollListener makeOnScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(viewWidth < 2) {
            viewWidth = listView.getWidth();
        }

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (paused)
                    return false;

                Rect rect = new Rect();
                int childCount = listView.getChildCount();
                int[] listViewCoords = new int[2];

                listView.getLocationOnScreen(listViewCoords);
                final int x = (int) event.getRawX() - listViewCoords[0];
                final int y = (int) event.getRawY() - listViewCoords[1];

                for (int i = 0; i < childCount; i++) {
                    View child = listView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        downView = child;
                        break;
                    }
                }

                if (downView != null) {
                    downX = event.getRawX();
                    downY = event.getRawY();
                    downPosition = listView.getPositionForView(downView);

                    if (dismissCallback.canDismiss(downPosition)) {
                        velocityTracker = VelocityTracker.obtain();
                        velocityTracker.addMovement(event);
                    } else {
                        downView = null;
                    }
                }
                return false;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (velocityTracker == null)
                    return false;
                if (downView != null && swiping) {
                    //cancel
                    downView.animate()
                            .alpha(1)
                            .translationX(0)
                            .setDuration(animationTime)
                            .setListener(null);
                }
                velocityTracker.recycle();
                velocityTracker = null;

                downX = downY = 0;
                downPosition = ListView.INVALID_POSITION;
                downView = null;
                swiping = false;
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (velocityTracker == null) {
                    return false;
                }

                float deltaX = event.getRawX() - downX;
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);

                float velocityX = velocityTracker.getXVelocity();
                float velocityY = velocityTracker.getYVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(velocityY);
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > viewWidth / 2 && swiping) {
                    dismiss = true;
                    dismissRight = deltaX > 0f;
                } else if (minFlingVelocty <= absVelocityX && absVelocityX <= maxFlingVelocity &&
                        absVelocityX > absVelocityY && swiping) {
                    dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = velocityX > 0;
                }

                if (dismiss && downPosition != ListView.INVALID_POSITION) {
                    final View downViewCpy = downView;
                    final int downPositionCpy = downPosition;
                    ++dismissAnimationRefCount;

                    downViewCpy.animate()
                            .translationX(dismissRight ? viewWidth : -viewWidth)
                            .alpha(0)
                            .setDuration(animationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss(downViewCpy, downPositionCpy);
                                }
                            });
                } else {
                    //cancel
                    downView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(animationTime)
                            .setListener(null);
                }
                velocityTracker.recycle();
                velocityTracker = null;

                downX = downY = 0;
                downPosition = ListView.INVALID_POSITION;
                downView = null;
                swiping = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (velocityTracker == null || paused) {
                    return false;
                }
                velocityTracker.addMovement(event);
                float deltaX = event.getRawX() - downX;
                float deltaY = event.getRawY() - downY;
                if(Math.abs(deltaX) > slop && Math.abs(deltaY) < Math.abs(deltaX) / 2f) {
                    swiping = true;
                    swipingSlop = deltaX > 0 ? slop : -slop;
                    listView.requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    listView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if(swiping) {
                    downView.setTranslationX(deltaX - swipingSlop);
                    downView.setAlpha(Math.max(0f,
                            Math.min(1f, 1f - 2f*Math.abs(deltaX)/viewWidth)));
                    return true;
                }
                break;
            }
        }

        return false;
    }

    class PendingDismissData implements Comparable<PendingDismissData>{
        public int position;
        public View view;

        public PendingDismissData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingDismissData another) {
            return another.position - position;
        }
    }

    private void performDismiss(final View dismissView, final int dismissPosition) {
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();
        final ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(animationTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                --dismissAnimationRefCount;
                if(dismissAnimationRefCount == 0) {
                    Collections.sort(pendingDismisses);
                    int[] dismissPositions = new int[pendingDismisses.size()];
                    for(int i = pendingDismisses.size()-1; i>=0; i--) {
                        dismissPositions[i] = pendingDismisses.get(i).position;
                    }
                    dismissCallback.onDismiss(listView, dismissPositions);

                    downPosition = ListView.INVALID_POSITION;
                    for(PendingDismissData pendingDismiss : pendingDismisses) {
                        pendingDismiss.view.setAlpha(1);
                        ViewGroup.LayoutParams lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingDismiss.view.setLayoutParams(lp);
                    }
                    long time = SystemClock.uptimeMillis();
                    MotionEvent cancelEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL,
                            0, 0, 0);
                    listView.dispatchTouchEvent(cancelEvent);
                    pendingDismisses.clear();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (int) animation.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });

        pendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
        animator.start();
    }
}
