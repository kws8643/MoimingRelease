package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int marginHeightDp;

    public RecyclerViewItemDecoration(Context context, int marginHeightDp) {

        this.context = context;
        this.marginHeightDp = marginHeightDp;

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.top = dpToPx(marginHeightDp);
    }

    // dp -> pixel 단위로 변경
    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
