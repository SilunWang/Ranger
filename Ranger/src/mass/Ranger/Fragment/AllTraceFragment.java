package mass.Ranger.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.travinavi.R;
import mass.Ranger.Activity.NavigationActivity;
import mass.Ranger.Activity.NavigationMapActivity;
import mass.Ranger.Activity.TrackingActivity;
import mass.Ranger.Activity.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mengyl on 12/19/2014.
 */
public class AllTraceFragment extends Fragment {
    private Button btn;
    private View parentView;
    private ListView listView;
    public Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    //toolBar
    private ImageButton toolRouteButton, toolUserButton, toolGuiderButton;
    private Spinner titleSpinner;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AllTraceFragment newInstance(int sectionNumber) {
        AllTraceFragment fragment = new AllTraceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.all_trace_home, container, false);
        initView();
        initData();
        initEvent();
        return parentView;
    }

    private void initEvent() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), NavigationActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Title change to!" + position, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Clicked item!", Toast.LENGTH_SHORT).show();
                toNaviMap(view);

                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        toolRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        toolGuiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getActivity().getApplication(), TrackingActivity.class);
                startActivity(i1);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        toolUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getActivity().getApplication(), UserProfileActivity.class);
                startActivity(i2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    private void initData() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                getTraceData());
        listView.setAdapter(arrayAdapter);
    }

    private void initView() {

        //下拉菜单相关
        String[] values = {"全部路线", "收藏路线", "已缓存"};
        titleSpinner = (Spinner) parentView.findViewById(R.id.id_title_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.self_spinner_item, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView textView = (TextView) v.findViewById(R.id.id_spinner_text);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return v;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        titleSpinner.setAdapter(spinnerArrayAdapter);

        //trace列表
        listView = (ListView) parentView.findViewById(R.id.all_trace_listView_id);
        //toolbar控件
        toolRouteButton = (ImageButton) parentView.findViewById(R.id.id_toolbar_route_button);
        toolGuiderButton = (ImageButton) parentView.findViewById(R.id.id_toolbar_guider_button);
        toolUserButton = (ImageButton) parentView.findViewById(R.id.id_toolbar_user_button);

        btn = (Button) parentView.findViewById(R.id.id_navi_test);
    }

    private List<String> getTraceData() {
        ArrayList<String> traceList = new ArrayList<String>();
        traceList.add("欧美汇 - 云海肴");
        traceList.add("芳草地 - 签证中心");
        traceList.add("新中关 - 麻辣诱惑");
        traceList.add("大悦城地下停车场 - 金逸影院");
        traceList.add("巴沟地铁站 - 海底捞");
        return traceList;
    }

    public void toNaviMap(View view) {
        Intent i = new Intent(getActivity(), NavigationMapActivity.class);
        startActivity(i);
    }

}
