package mass.Ranger.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.example.travinavi.R;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class SensorAdapter extends ArrayAdapter<SensorSettingItem> {

    Context context;
    int layoutResourceId;
    SensorSettingItem data[] = null;

    public SensorAdapter(Context context, int layoutResourceId, SensorSettingItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SensorHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SensorHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.switcher = (Switch)row.findViewById(R.id.sensorSwitch);
            row.setTag(holder);
        }
        else
        {
            holder = (SensorHolder)row.getTag();
        }

        SensorSettingItem sensor = data[position];
        holder.txtTitle.setText(sensor.title);
        holder.imgIcon.setImageResource(sensor.icon);
        holder.switcher.setChecked(true);
        return row;
    }

    static class SensorHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        Switch switcher;
    }

}
