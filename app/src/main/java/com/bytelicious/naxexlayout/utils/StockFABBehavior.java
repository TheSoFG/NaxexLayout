package com.bytelicious.naxexlayout.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

/**
 * Custom FloatingActionButton behavior that hides and shows the button (hides on scroll down, shows on scroll up)
 */
public class StockFABBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;


    public StockFABBehavior(Context context, AttributeSet attrs) {

        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        // regardless of the scroll existing or not, the button is hidden or shown
        if ((dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) || (dyUnconsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE)) {

            animateOut(child);

        } else if ((dyUnconsumed < 0 && child.getVisibility() != View.VISIBLE) || (dyConsumed < 0 && child.getVisibility() != View.VISIBLE)) {

            animateIn(child);

        }

    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {

        ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {

                    public void onAnimationStart(View view) {

                        StockFABBehavior.this.mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {

                        StockFABBehavior.this.mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {

                        StockFABBehavior.this.mIsAnimatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {

        button.setVisibility(View.VISIBLE);
        ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();

    }

}