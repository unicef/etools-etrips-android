package org.unicef.etools.etrips.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.etools.etrips.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.ui.activity.ActionPointActivity;
import org.unicef.etools.etrips.ui.adapter.ActionPointAdapter;
import org.unicef.etools.etrips.util.Constant;
import org.unicef.etools.etrips.util.widget.EmptyState;

import java.util.ArrayList;

import io.realm.Realm;

public abstract class BaseActionPointFragment extends BaseFragment implements
        ActionPointAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    protected static final int REQUEST_CODE_EDIT_ACTION_POINT = 1000;
    protected static final int REQUEST_CODE_ADD_ACTION_POINT = 1001;

    // ===========================================================
    // Fields
    // ===========================================================

    private int mClickedPosition;

    protected ActionPointAdapter mActionPointAdapter;
    protected EmptyState mEmptyState;
    protected RecyclerView mRvActionPoints;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_ACTION_POINT) {
            if (resultCode == Activity.RESULT_OK) {
                final long id = data.getLongExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, -1L);
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final ActionPoint actionPoint = realm.where(ActionPoint.class)
                                .equalTo("id", id)
                                .findFirst();
                        mActionPointAdapter.add(actionPoint);

                        if (mEmptyState.getVisibility() == View.VISIBLE) {
                            mEmptyState.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } else if (requestCode == REQUEST_CODE_EDIT_ACTION_POINT) {
            if (resultCode == Activity.RESULT_OK) {
                mActionPointAdapter.notifyItemChanged(mClickedPosition);
            }
        }
    }

    @Override
    public void onItemClick(ActionPoint actionPoint, int position) {
        // Don't forget to handle case when action point is not valid as Realm object.
        mClickedPosition = position;
        final Intent intent = new Intent(getActivity(), ActionPointActivity.class);
        intent.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_OPERATION, ActionPointActivity.EDIT);
        intent.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, actionPoint.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_ACTION_POINT);
    }

    @Override
    public void onItemLongClick(ActionPoint actionPoint) {
        // Don't forget to handle case when action point is not valid as Realm object.
    }

    // ===========================================================
    // Methods
    // ===========================================================

    protected void initListComponents() {
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvActionPoints.setLayoutManager(layoutManager);

        mActionPointAdapter = new ActionPointAdapter(new ArrayList<ActionPoint>(), this);
        mRvActionPoints.setAdapter(mActionPointAdapter);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
