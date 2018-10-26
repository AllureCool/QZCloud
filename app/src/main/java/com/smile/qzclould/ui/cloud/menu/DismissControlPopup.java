package com.smile.qzclould.ui.cloud.menu;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.smile.qzclould.R;
import com.smile.qzclould.utils.ViewUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by wzg.
 * 菜单。
 */
public class DismissControlPopup extends BasePopupWindow implements View.OnClickListener {

    private OnMenuSelectListener mMenuSelectListener;

    public DismissControlPopup(Activity context) {
        super(context, (int) ViewUtils.Companion.dip2px(180f), (int) ViewUtils.Companion.dip2px(80f));
        findViewById(R.id.tv_order_by_name).setOnClickListener(this);
        findViewById(R.id.tv_order_by_time).setOnClickListener(this);
        setAlignBackground(false);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation(false));
        return set;
    }

    public void setOrderType(int orderType) {
        if(orderType == 0) {
            ((TextView)findViewById(R.id.tv_order_by_name)).setTextColor(getContext().getResources().getColor(R.color.color_green_2EC17C));
            ((TextView)findViewById(R.id.tv_order_by_time)).setTextColor(getContext().getResources().getColor(R.color.color_black_5A5A5A));
        } else  {
            ((TextView)findViewById(R.id.tv_order_by_name)).setTextColor(getContext().getResources().getColor(R.color.color_black_5A5A5A));
            ((TextView)findViewById(R.id.tv_order_by_time)).setTextColor(getContext().getResources().getColor(R.color.color_green_2EC17C));
        }
    }

    public void setMenuSelectListener(OnMenuSelectListener menuSelectListener) {
        mMenuSelectListener = menuSelectListener;
    }

    @Override
    public void showPopupWindow(View v) {
        setOffsetX(-v.getWidth() / 2);
        setOffsetY(v.getHeight() / 2);
        super.showPopupWindow(v);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_order_by_name:
                if(mMenuSelectListener != null) {
                    mMenuSelectListener.onSelect(0);
                }
                break;
            case R.id.tv_order_by_time:
                if(mMenuSelectListener != null) {
                    mMenuSelectListener.onSelect(1);
                }
                break;
            default:
                break;
        }
        dismiss();

    }

    public static interface OnMenuSelectListener {
        void onSelect(int orderType);
    }
}
