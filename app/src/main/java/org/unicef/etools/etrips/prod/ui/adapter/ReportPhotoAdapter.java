package org.unicef.etools.etrips.prod.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Attachment;

import java.util.ArrayList;

public class ReportPhotoAdapter extends RecyclerView.Adapter<ReportPhotoAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ReportPhotoAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<Attachment> mReportPhotos;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ReportPhotoAdapter(ArrayList<Attachment> reportPhotos,
                              OnItemClickListener onItemClickListener) {
        mReportPhotos = reportPhotos;
        mOnItemClickListener = onItemClickListener;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item_report_photos, viewGroup, false);
        return new ViewHolder(view, mReportPhotos, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mReportPhotos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView tvReportPhotoDescription;
        ImageView ivReportPhoto;
        LinearLayout llReportPhotoContainer;
        OnItemClickListener onItemClickListener;
        ArrayList<Attachment> attachmentList;

        ViewHolder(View itemView, ArrayList<Attachment> mAttachments,
                   OnItemClickListener onItemClickListener) {
            super(itemView);
            this.attachmentList = mAttachments;
            this.onItemClickListener = onItemClickListener;
            this.context = itemView.getContext();
            findViews(itemView);
        }

        void findViews(View view) {
            tvReportPhotoDescription = (TextView) view.findViewById(R.id.tv_item_report_photo_description);
            ivReportPhoto = (ImageView) view.findViewById(R.id.iv_item_report_photo);
            llReportPhotoContainer = (LinearLayout) view.findViewById(R.id.ll_rp_container);
        }

        void bindData() {
            tvReportPhotoDescription.setText(
                    attachmentList.get(getAdapterPosition()).getName());

            Glide.with(context)
                    .load(attachmentList.get(getAdapterPosition()).getFile())
                    .asBitmap()
                    .placeholder(R.color.color_bdbdbd)
                    .error(R.color.color_bdbdbd)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivReportPhoto);

            llReportPhotoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onClick with position == -1.");
                        return;
                    }
                    onItemClickListener.onItemClick(attachmentList.get(getAdapterPosition()).getId(),
                            getAdapterPosition());
                }
            });

            llReportPhotoContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onLongClick with position == -1.");
                        return false;
                    }
                    onItemClickListener.onItemLongClick(attachmentList.get(getAdapterPosition()).getId(),
                            getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(long attachmentId, int position);

        void onItemLongClick(long attachmentId, int position);
    }
}
