package roomies.com.roomies.mainactivityviews.managecoloc.joincoloc;

import android.content.Context;
import android.support.v7.widget.SearchView;

/**
 * Created by xila on 30/01/2017.
 */

public class MySearchView extends SearchView {
    private boolean expanded;


    public MySearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        expanded = true;
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
