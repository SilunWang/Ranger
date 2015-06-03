package mass.Ranger.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.travinavi.R;
import mass.Ranger.Adapter.SensorAdapter;
import mass.Ranger.Adapter.SensorSettingItem;


public class SettingsFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View setting = inflater.inflate(R.layout.settings, container, false);

        SensorSettingItem sensorSettingItem[] = new SensorSettingItem[]
                {
                        new SensorSettingItem(R.drawable.ic_drawer, "Barometer"),
                        new SensorSettingItem(R.drawable.ic_drawer, "Acc"),
                        new SensorSettingItem(R.drawable.ic_drawer, "Gyro"),
                        new SensorSettingItem(R.drawable.ic_drawer, "Camera")
                };

        SensorAdapter adapter = new SensorAdapter(getActivity(), R.layout.settings_item, sensorSettingItem);

        listView = (ListView)setting.findViewById(R.id.settinglist);
        View header = getLayoutInflater(savedInstanceState).inflate(R.layout.settings_header, null);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);

//        setting.findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MainActivity.resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//            }
//        });
//
//        setting.findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MainActivity.resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });

        return setting;
    }

}
