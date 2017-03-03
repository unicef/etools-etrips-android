package org.unicef.etools.etrips.util.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.etools.etrips.R;


public class EmptyState extends FrameLayout {

    public EmptyState(Context context) {
        this(context, null);
    }

    public EmptyState(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyState(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.widget_empty_state, this);

        final ImageView ivIcon = (ImageView) findViewById(R.id.iv_empty_state_icon);
        final TextView tvTitle = (TextView) findViewById(R.id.tv_empty_state_title);
        final TextView tvDescription = (TextView) findViewById(R.id.tv_empty_state_description);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyState);
        ivIcon.setImageResource(a.getResourceId(R.styleable.EmptyState_es_icon, R.color.color_a1a1a1));
        tvTitle.setText(a.getString(R.styleable.EmptyState_es_title));
        tvDescription.setText(a.getString(R.styleable.EmptyState_es_description));
        a.recycle();
    }
}
