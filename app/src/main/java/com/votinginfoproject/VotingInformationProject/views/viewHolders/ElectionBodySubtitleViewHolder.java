package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.animation.ObjectAnimator;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by max on 4/18/16.
 */
public class ElectionBodySubtitleViewHolder extends RecyclerView.ViewHolder {
    private static final int chevronFlipAnimationDuration = 500;

    private final View mView;
    private final ImageView mImageView;
    private final ImageView mChevronImageView;
    private final TextView mTextView;
    private boolean mIsExpanded;

    private int mImageResource;

    public ElectionBodySubtitleViewHolder(View v) {
        super(v);

        mView = v;
        mView.setTag(this);
        mImageView = (ImageView) mView.findViewById(R.id.body_subtitle_image);
        mChevronImageView = (ImageView) mView.findViewById(R.id.body_subtitle_arrow);
        mTextView = (TextView) mView.findViewById(R.id.body_subtitle);
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public void setImageResource(@DrawableRes int id) {
        if (id != mImageResource) {
            mImageResource = id;
            updateImage();
        }
    }

    private void updateImage() {
        mImageView.setImageResource(mImageResource);
    }

    public void setExpanded(boolean expanded) {
        mIsExpanded = expanded;
        performFlipAnimation(mIsExpanded);
    }

    private void performFlipAnimation(boolean expanded) {
        ObjectAnimator flipY = ObjectAnimator.ofFloat(mChevronImageView, "scaleY", expanded ? -1f : 1f);
        flipY.setDuration(chevronFlipAnimationDuration);
        flipY.start();
    }

    public int getLeftDividerMargin() {
        return mTextView.getLeft();
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }
}