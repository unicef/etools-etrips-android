package org.unicef.etools.etrips.prod.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.ui.activity.ActionPointActivity;
import org.unicef.etools.etrips.prod.ui.adapter.ActionPointAdapter;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.manager.CustomLinearLayoutManager;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import java.util.ArrayList;

public abstract class BaseActionPointFragment extends BaseFragment implements
        ActionPointAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

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
    public void onItemClick(ActionPoint actionPoint, int position) {
        // Don't forget to handle case when action point is not valid as Realm object.
        final Intent intent = new Intent(getActivity(), ActionPointActivity.class);
        intent.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_OPERATION, ActionPointActivity.EDIT);
        intent.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, actionPoint.getId());
        startActivity(intent);
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
                = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvActionPoints.setLayoutManager(layoutManager);

        mActionPointAdapter = new ActionPointAdapter(new ArrayList<ActionPoint>(), this);
        mRvActionPoints.setAdapter(mActionPointAdapter);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
