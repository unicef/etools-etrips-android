package org.unicef.etools.etrips.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.etools.etrips.R;
import org.unicef.etools.etrips.util.Constant;

public class ReportPhotoActivity extends BaseActivity implements View.OnClickListener,
        TextWatcher {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ReportPhotoActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    //    private Uri mPhotoUri;
    private ImageView mIvReportPhoto;
    private TextInputLayout mTilReportPhotoDescription;
    private EditText mEdtReportPhotoDescription;
    private Button mBtnReportPhotoSave;
    private String mPhotoDescription;
    private String mPhotoUrl;
//    private long mAttachmentId;
//    private Attachment mAttachment;

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
        setListeners();
        getData();
        customizeActionBar();
        loadData();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_report_photo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            case R.id.btn_report_photo_save:
                grabDataAndProceed(
                        mEdtReportPhotoDescription.getText().toString(),
                        mPhotoUrl
                );
                break;

            case R.id.iv_report_photo:
                Intent intent = new Intent(this, ReportPhotoPreviewActivity.class);
//                intent.putExtra(Constant.Extra.REPORT_PHOTO_URI, mPhotoUri);
                intent.putExtra(Constant.Extra.REPORT_PHOTO_URL, mPhotoUrl);
                startActivity(intent);
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (getCurrentFocus() != null) {
            mTilReportPhotoDescription.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void getData() {
//        mAttachmentId = getIntent().getLongExtra(Constant.Extra.REPORT_ATTACHMENT_ID, 0);
//        if (mAttachmentId != 0) {
//            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    mAttachment = realm.where(Attachment.class).equalTo("id", mAttachmentId).findFirst();
//                }
//            });
//        }
//        if (mAttachment != null) {
//            mPhotoUrl = mAttachment.getFile();
//            mPhotoDescription = mAttachment.getName();
//        }
        mPhotoUrl = getIntent().getStringExtra(Constant.Extra.REPORT_PHOTO_URL);
        mPhotoDescription = getIntent().getStringExtra(Constant.Extra.REPORT_PHOTO_DESCRIPTION);
    }

    private void findViews() {
        mIvReportPhoto = (ImageView) findViewById(R.id.iv_report_photo);
        mTilReportPhotoDescription = (TextInputLayout) findViewById(R.id.til_report_photo_description);
        mEdtReportPhotoDescription = (EditText) findViewById(R.id.edt_report_photo_description);
        mBtnReportPhotoSave = (Button) findViewById(R.id.btn_report_photo_save);
    }

    private void setListeners() {
        mBtnReportPhotoSave.setOnClickListener(this);
        mIvReportPhoto.setOnClickListener(this);
        mEdtReportPhotoDescription.addTextChangedListener(this);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.screen_report_photo));
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void loadData() {
        if (mPhotoUrl != null && mPhotoDescription != null) {

            // set description
            mEdtReportPhotoDescription.setText(mPhotoDescription);

            // hide SAVE button
            mBtnReportPhotoSave.setVisibility(View.GONE);

            // disable edt
            mEdtReportPhotoDescription.setFocusable(false);
            mEdtReportPhotoDescription.setCursorVisible(false);
            mEdtReportPhotoDescription.setKeyListener(null);
            mEdtReportPhotoDescription.setBackgroundColor(Color.TRANSPARENT);

            // load image
            Glide.with(this)
                    .load(mPhotoUrl)
                    .asBitmap()
                    .placeholder(R.color.color_bdbdbd)
                    .error(R.color.color_bdbdbd)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvReportPhoto);

        } else {
            Glide.with(this)
                    .load(mPhotoUrl)
                    .asBitmap()
                    .placeholder(R.color.color_bdbdbd)
                    .error(R.color.color_bdbdbd)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvReportPhoto);
        }
    }

    private void grabDataAndProceed(String description, String photoPath) {

        boolean isValidationSucceeded = true;

        if (description.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilReportPhotoDescription.setError(getString(R.string.msg_edt_description_error));
        }

        if (isValidationSucceeded) {
            Intent intent = new Intent();
            intent.putExtra(Constant.Extra.REPORT_PHOTO_URL, photoPath);
            intent.putExtra(Constant.Extra.REPORT_PHOTO_DESCRIPTION, description);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}