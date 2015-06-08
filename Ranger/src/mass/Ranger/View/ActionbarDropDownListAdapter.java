package mass.Ranger.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.travinavi.R;

/**
 * Created by v-mengyl on 2015/1/19.
 */
public class ActionbarDropDownListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;


    /**
     * View displayed inside the Action Bar
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView = convertView;
        if (resultView == null) {
            resultView = mLayoutInflater.inflate(R.layout.self_spinner_item, null);
        }

        return resultView;
    }

    /**
     * View Displayed inside list of possible options
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View resultView = convertView;

        if (resultView == null) {
            resultView = mLayoutInflater.inflate(R.layout.spinner_drop_down, null);
        }

        return resultView;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
