package org.unicef.etools.etrips.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.etools.etrips.R;
import org.unicef.etools.etrips.util.Constant;

public class ReportPhotoPreviewActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ReportPhotoPreviewActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ImageView mIvPhoto;
    //    private Uri mPhotoUri;
    private String mPhotoUrl;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        getData();
        loadPhoto();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_photo_preview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void getData() {
//        mPhotoUri = getIntent().getParcelableExtra(Constant.Extra.REPORT_PHOTO_URI);
        mPhotoUrl = getIntent().getStringExtra(Constant.Extra.REPORT_PHOTO_URL);
    }

    private void findViews() {
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo_preview);
    }

    private void loadPhoto() {
        Glide.with(this)
                .load(mPhotoUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvPhoto);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}