package org.unicef.etools.etrips.prod.ui.fragment.trip;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Attachment;
import org.unicef.etools.etrips.prod.db.entity.trip.LocalTrip;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.activity.ReportPhotoActivity;
import org.unicef.etools.etrips.prod.ui.adapter.ReportPhotoAdapter;
import org.unicef.etools.etrips.prod.ui.fragment.BaseFragment;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.FileUtil;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

public class ReportFragment extends BaseFragment implements View.OnClickListener,
        ReportPhotoAdapter.OnItemClickListener, TextWatcher {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ReportFragment.class.getSimpleName();

    private static final int REQUEST_CAMERA_CODE = 101;
    private static final int REQUEST_GALLERY_CODE = 102;
    private static final int REQUEST_REPORT_PHOTO_CODE = 103;

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private TextInputLayout mTilReportText;
    private EditText mEdtReportText;
    private TextView mTvReportPhotosDescription;
    private Button mBtnAddPhoto;
    private Button mBtnSubmit;
    private Uri mCameraPhotoPath;
    private ArrayList<Attachment> mAttachmentArrayList;
    private RecyclerView mRvReportPhotos;
    private ReportPhotoAdapter mReportPhotoAdapter;
    private Trip mTrip;
    private String mReportDescription;
    private CardView mCvPhotosContainer;
    //    private boolean canBeSavedAsDraft;
    private TextView mTvDraftLabel;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    public static ReportFragment newInstance(long tripId) {
        Bundle args = new Bundle();
        args.putLong(Constant.Argument.ARGUMENT_TRIP_ID, tripId);
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ReportFragment newInstance(Bundle args) {
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        BusProvider.register(this);
        findViews(view);
        setListeners();
        init();
        getData();
        retrieveTripFromDb();
        setData();
        restoreData(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveDraftTrip();
        BusProvider.unregister(this);
        DialogManager.getInstance().dismissPreloader(getClass());
        Realm.getDefaultInstance().close();
    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mTrip = realm.where(Trip.class).equalTo("id", mTripId).findFirst();
                if (mTrip == null || !mTrip.isValid()) {
                    return;
                }
                mTrip = realm.copyFromRealm(mTrip);
                if (mTrip.isNotSynced()) {
                    if (!mAttachmentArrayList.isEmpty()) {
                        mTrip.getAttachments().clear();
                        mTrip.getAttachments().addAll(mAttachmentArrayList);
                        realm.copyToRealmOrUpdate(mTrip);
                    }
                }
            }
        });
        if (!mEdtReportText.getText().toString().isEmpty()) {
            bundle.putString(Constant.Extra.REPORT_PHOTO_DESCRIPTION, mEdtReportText.getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_CODE:
                    if (mCameraPhotoPath != null) {
                        // start ReportPhotoActivity and pass mCameraPhotoPath, in ReportPhotoActivity
                        // we compose ReportPhoto object with two fields - description and url
                        // then store it in mAttachmentArrayList (get ReportPhoto object
                        // in REQUEST_REPORT_PHOTO_CODE)

                        String cameraPhotoUrl = FileUtil.getFileFromUri(
                                getActivity(),
                                mCameraPhotoPath
                        ).toString();


                        Intent intent = new Intent(getActivity(), ReportPhotoActivity.class);
                        intent.putExtra(Constant.Extra.REPORT_PHOTO_URL, cameraPhotoUrl);
                        startActivityForResult(intent, REQUEST_REPORT_PHOTO_CODE);
                    }
                    break;
                case REQUEST_GALLERY_CODE:

                    String galleryPhotoUrl = FileUtil.getFileFromUri(
                            getActivity(),
                            data.getData()
                    ).toString();

                    Intent intent = new Intent(getActivity(), ReportPhotoActivity.class);
                    intent.putExtra(Constant.Extra.REPORT_PHOTO_URL, galleryPhotoUrl);
                    startActivityForResult(intent, REQUEST_REPORT_PHOTO_CODE);
                    break;

                case REQUEST_REPORT_PHOTO_CODE:
                    String reportPhotoName = data.getStringExtra(Constant.Extra.REPORT_PHOTO_DESCRIPTION);
                    String reportPhotoUrl = data.getStringExtra(Constant.Extra.REPORT_PHOTO_URL);


                    final Attachment reportPhoto = new Attachment();
                    reportPhoto.setId(-System.currentTimeMillis());
                    reportPhoto.setName(reportPhotoName);
                    reportPhoto.setFile(reportPhotoUrl);

                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            List<Attachment> attachmentList = mTrip.getAttachments();
                            attachmentList.add(reportPhoto);
                            mAttachmentArrayList.clear();
                            mAttachmentArrayList.addAll(attachmentList);
                            mReportPhotoAdapter.notifyItemInserted(mAttachmentArrayList.size() - 1);
                            realm.copyToRealmOrUpdate(mTrip);
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.Code.ASK_MEDIA_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaPickerDialog();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report_submit:
                if (NetworkUtil.getInstance().isConnected(getActivity())) {
                    // crete trip report and send in server
                    mReportDescription = mEdtReportText.getText().toString();
                    grabDataAndSubmitReport();
                } else {
                    SnackBarManager.show(getActivity(), getString(R.string.msg_network_connection_error),
                            SnackBarManager.Duration.LONG);
                }
                break;

            case R.id.btn_report_add_photo:
                // first check on necessary permissions for camera and gallery
                AppUtil.closeKeyboard(getActivity());
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            Constant.Code.ASK_MEDIA_PERMISSIONS
                    );
                } else {
                    mediaPickerDialog();
                }
                break;
        }
    }

    // ===========================================================
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleApiEvents(ApiEvent event) {

        switch (event.getEventType()) {
            case Event.EventType.Api.REPORT_SUBMITTED:
                // send report photos on server
                if (!mAttachmentArrayList.isEmpty()) {
                    for (final Attachment attachment : mAttachmentArrayList) {
                        RetrofitUtil.uploadTripReportFileRequest(
                                getActivity(),
                                attachment,
                                ReportFragment.class.getSimpleName(),
                                mTrip.getId()
                        );
                    }
                } else {
                    retrieveTripFromDb();
                    mTrip.setNotSynced(false);
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            LocalTrip localTrip = realm.where(LocalTrip.class).equalTo("id", mTripId).findFirst();
                            if (localTrip != null && localTrip.isValid()) {
                                localTrip.deleteFromRealm();
                            }
                            realm.copyToRealmOrUpdate(mTrip);
                        }
                    });
                    setData();
                    DialogManager.getInstance().dismissPreloader(getClass());
                }
                break;

            case Event.EventType.Api.REPORT_FILES_UPLOADED:
                long sentAttachmentId = (long) event.getEventData();
                Log.d(LOG_TAG, "File " + sentAttachmentId + " are sent on server");
                if (!mAttachmentArrayList.isEmpty()) {
                    Iterator<Attachment> attachmentIterator = mAttachmentArrayList.iterator();
                    while (attachmentIterator.hasNext()) {
                        Attachment attachment = attachmentIterator.next();
                        if ((attachment.getId() == sentAttachmentId)) {
                            attachmentIterator.remove();
                        }
                    }
                }

                if (mAttachmentArrayList.isEmpty()) {

                    mTrip.setNotSynced(false);
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            LocalTrip localTrip = realm.where(LocalTrip.class).equalTo("id", mTripId).findFirst();
                            if (localTrip != null && localTrip.isValid()) {
                                localTrip.deleteFromRealm();
                            }
                            realm.copyToRealmOrUpdate(mTrip);
                        }
                    });

                    retrieveTripFromDb();
                    setData();
                    DialogManager.getInstance().dismissPreloader(getClass());
                }
                break;

            case Event.EventType.Api.TRIP_SYNCED:
                retrieveTripFromDb();
                setData();
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                SnackBarManager.show(getActivity(), getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(getActivity(), body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                SnackBarManager.show(getActivity(), getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                DialogManager.getInstance().dismissPreloader(getClass());
                mBtnSubmit.setEnabled(true);
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onItemClick(long attachmentId, int position) {
        Intent intent = new Intent(getActivity(), ReportPhotoActivity.class);
        intent.putExtra(Constant.Extra.REPORT_PHOTO_URL, mAttachmentArrayList.get(position).getFile());
        intent.putExtra(Constant.Extra.REPORT_PHOTO_DESCRIPTION, mAttachmentArrayList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(long attachmentId, final int position) {
        if (mTrip.isNotSynced()) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mTrip.getAttachments().remove(position);
                    mAttachmentArrayList.remove(position);
                    mReportPhotoAdapter.notifyItemRemoved(position);
                    realm.copyToRealmOrUpdate(mTrip);
                }
            });
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTilReportText.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void dispatchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType(Constant.Util.IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GALLERY_CODE);
    }

    private void dispatchCameraIntent() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            mCameraPhotoPath = FileUtil.getTempPhotoUri(
                    getActivity(),
                    String.valueOf(System.currentTimeMillis())
            );
            if (mCameraPhotoPath != null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPhotoPath);
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
            }
        }
    }

    private void mediaPickerDialog() {
        final CharSequence[] items = {
                getString(R.string.dialog_text_take_from_camera),
                getString(R.string.dialog_text_take_from_gallery),
                getString(R.string.dialog_text_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.dialog_text_take_from_camera))) {
                    dispatchCameraIntent();

                } else if (items[item].equals(getString(R.string.dialog_text_take_from_gallery))) {
                    dispatchGalleryIntent();

                } else if (items[item].equals(getString(R.string.dialog_text_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setListeners() {
        mBtnAddPhoto.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mEdtReportText.addTextChangedListener(this);
    }

    private void findViews(View view) {
        mTvDraftLabel = (TextView) view.findViewById(R.id.tv_report_draft_label);
        mTilReportText = (TextInputLayout) view.findViewById(R.id.til_report_text);
        mEdtReportText = (EditText) view.findViewById(R.id.edt_report_text);
        mBtnAddPhoto = (Button) view.findViewById(R.id.btn_report_add_photo);
        mCvPhotosContainer = (CardView) view.findViewById(R.id.cv_report_photos);
        mBtnSubmit = (Button) view.findViewById(R.id.btn_report_submit);
        mRvReportPhotos = (RecyclerView) view.findViewById(R.id.rv_report_photos);
        mTvReportPhotosDescription = (TextView) view.findViewById(R.id.tv_report_photos_description);
    }

    public void getData() {
        if (getArguments() != null) {
            mTripId = getArguments().getLong(Constant.Argument.ARGUMENT_TRIP_ID);
        }
    }

    private void retrieveTripFromDb() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mTrip = realm.where(Trip.class).equalTo("id", mTripId).findFirst();
                if (mTrip == null || !mTrip.isValid()) {
                    return;
                }
                mTrip = realm.copyFromRealm(mTrip);
            }
        });
    }

    public void setData() {
        if (mTrip == null || !mTrip.isValid() || mTrip.getReport() == null) {
            return;
        }

        if (!mTrip.isNotSynced()) {

            mEdtReportText.setText(mTrip.getReport());
            List<Attachment> reportPhotos = mTrip.getAttachments();
            mAttachmentArrayList.clear();
            mAttachmentArrayList.addAll(reportPhotos);
            mReportPhotoAdapter.notifyDataSetChanged();

            if (mTrip.getAttachments() != null && !mTrip.getAttachments().isEmpty()
                    && mTrip.getReport() != null && !mTrip.getReport().isEmpty()) {

                // disable edt
                mEdtReportText.setFocusable(false);
                mEdtReportText.setCursorVisible(false);
                mEdtReportText.setKeyListener(null);
                mEdtReportText.setBackgroundColor(Color.TRANSPARENT);

                // hide 'Submit button' and 'Add photo' button
                mBtnSubmit.setVisibility(View.GONE);
                mBtnAddPhoto.setVisibility(View.GONE);
                mTvReportPhotosDescription.setVisibility(View.GONE);

            } else if (mTrip.getReport() != null && !mTrip.getReport().isEmpty()) {
                // disable edt
                mEdtReportText.setFocusable(false);
                mEdtReportText.setCursorVisible(false);
                mEdtReportText.setKeyListener(null);
                mEdtReportText.setBackgroundColor(Color.TRANSPARENT);

                if (mTrip.getAttachments().isEmpty()) {
                    mCvPhotosContainer.setVisibility(View.GONE);
                    mBtnSubmit.setVisibility(View.GONE);
                    mTvReportPhotosDescription.setVisibility(View.GONE);
                }
            }
        } else {
            mTvDraftLabel.setVisibility(View.VISIBLE);
            mEdtReportText.setText(mTrip.getReport());
            List<Attachment> reportPhotos = mTrip.getAttachments();
            mAttachmentArrayList.clear();
            mAttachmentArrayList.addAll(reportPhotos);
            mReportPhotoAdapter.notifyDataSetChanged();
        }

        // check supervised state
        if (mTrip.getSupervisor() == Preference.getInstance(getActivity()).getUserId() || !mTrip.isMyTrip()) {
            mBtnSubmit.setVisibility(View.GONE);
            if (mTrip.getReport() == null || mTrip.getReport().isEmpty()) {
                mEdtReportText.setFocusable(false);
                mEdtReportText.setCursorVisible(false);
                mEdtReportText.setKeyListener(null);
                mEdtReportText.setBackgroundColor(Color.TRANSPARENT);
                mEdtReportText.setText(R.string.text_no_report);
                mCvPhotosContainer.setVisibility(View.GONE);
                return;
            }

            if (mTrip.getAttachments() == null || mTrip.getAttachments().isEmpty()) {
                mCvPhotosContainer.setVisibility(View.GONE);
            }
        }

    }

    private void init() {
        // init RecyclerView with adapter and list of ReportPhoto objects,
        // later we will store all selected photos in it
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvReportPhotos.setNestedScrollingEnabled(false);
        mRvReportPhotos.setLayoutManager(linearLayoutManager);
        mAttachmentArrayList = new ArrayList<>();
        mReportPhotoAdapter = new ReportPhotoAdapter(mAttachmentArrayList, this);
        mRvReportPhotos.setAdapter(mReportPhotoAdapter);
    }

    private void grabDataAndSubmitReport() {
        boolean isValidationSucceeded = true;

        // validate report (empty or not)
        if (mReportDescription.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilReportText.setError(getString(R.string.msg_edt_report_error));
        }

        // if required fields are filled up then proceed
        if (isValidationSucceeded) {
            mBtnSubmit.setEnabled(false);
            mTvDraftLabel.setVisibility(View.GONE);
            DialogManager.getInstance().showPreloader(getActivity(), getClass().getSimpleName());
            mTilReportText.setErrorEnabled(false);
            AppUtil.closeKeyboard(getActivity());

            // save trip in db as draft before sending on server
            saveDraftTrip();

            RetrofitUtil.updateTripReportDescriptionRequest(
                    getActivity(),
                    ReportFragment.class.getSimpleName(),
                    mTrip
            );
        }
    }

    private void saveDraftTrip() {
        retrieveTripFromDb();
        if (mTrip == null || !mTrip.isValid()) {
            return;
        }
        if (mTrip.isMyTrip()) {
            if (mTrip.isNotSynced()) {
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mTrip.setReport(mEdtReportText.getText().toString());
                        if (!mEdtReportText.getText().toString().isEmpty() ||
                                !mTrip.getAttachments().isEmpty()) {
                            mTrip.setNotSynced(true);
                            realm.copyToRealmOrUpdate(mTrip);
                            realm.copyToRealmOrUpdate(LocalTrip.buildFromTrip(mTrip));
                        } else {
                            mTrip.setNotSynced(false);
                            LocalTrip localTrip = realm.where(LocalTrip.class).equalTo("id", mTripId).findFirst();
                            if (localTrip != null && localTrip.isValid()) {
                                localTrip.deleteFromRealm();
                            }
                            realm.copyToRealmOrUpdate(mTrip);
                        }
                    }
                });

            } else {
                if (mTrip.getReport() == null || mTrip.getReport().isEmpty()) {
                    if (!mEdtReportText.getText().toString().isEmpty() ||
                            !mTrip.getAttachments().isEmpty()) {
                        mTrip.setReport(mEdtReportText.getText().toString());
                        mTrip.setNotSynced(true);
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(LocalTrip.buildFromTrip(mTrip));
                                realm.copyToRealmOrUpdate(mTrip);
                            }
                        });
                    }
                }
            }
        }
    }

    private void restoreData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mReportDescription = savedInstanceState.getString(Constant.Extra.REPORT_PHOTO_DESCRIPTION);
            mEdtReportText.setText(mReportDescription);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}