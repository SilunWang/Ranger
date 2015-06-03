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
    private ImageButton toolRouteButton, toolUserButton,toolGuiderButton;
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
                Toast.makeText(getActivity(), "Title change to!"+position, Toast.LENGTH_SHORT).show();

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
                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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

    private void initView(){

        //navi_test_btn = (Button) getActivity().findViewById(R.id.id_navi_test);


        //下拉菜单相关
        String [] values = {"全部路线", "收藏路线" ,"已缓存" };
        titleSpinner = (Spinner)parentView.findViewById(R.id.id_title_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.self_spinner_item, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v = super.getView(position, convertView, parent);
                TextView textView = (TextView) v.findViewById(R.id.id_spinner_text);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View allTraceView = inflater.inflate(R.layout.all_trace_home, container, false);
//
//        traceInfoList = getTraceData();
//        traceListView = (SwipeMenuListView) getActivity().findViewById(R.id.all_trace_listview);
//        mAdapter = new TraceAdapter();
//        traceListView.setAdapter(mAdapter);
//
//        // step 1. create a MenuCreator
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//
//                ////////create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getActivity().getApplicationContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//
//                ////////create "favorite" item
//                SwipeMenuItem favoriteItem = new SwipeMenuItem(
//                        getActivity().getApplicationContext());
//                // set item background
//                favoriteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//                // set item width
//                favoriteItem.setWidth(dp2px(90));
//                // set a icon
//                favoriteItem.setIcon(R.drawable.favorite_3_xxl);
//                // add to menu
//                menu.addMenuItem(favoriteItem);
//
//                ///////create "share" item
//                SwipeMenuItem shareItem = new SwipeMenuItem(
//                        getActivity().getApplicationContext());
//                // set item background
//                shareItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//                // set item width
//                shareItem.setWidth(dp2px(90));
//                // set a icon
//                favoriteItem.setIcon(R.drawable.share_5_xxl);
//                // add to menu
//                menu.addMenuItem(shareItem);
//
//            }
//        };

//        // set creator
//        traceListView.setMenuCreator(creator);
//
//        // step 2. listener item click event
//        traceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                String item = traceInfoList.get(position);
//                switch (index) {
//                    case 0:
//                        // delete
//                        traceInfoList.remove(position);
//                        break;
//                    case 1:
//                        // favorite-->collect
//
//                        break;
//                    case 2:
//                        //share
//
//                        break;
//                }
//                return false;
//            }
//        });
//
//        // set SwipeListener
//        traceListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//
//            @Override
//            public void onSwipeStart(int position) {
//                // swipe start
//            }
//
//            @Override
//            public void onSwipeEnd(int position) {
//                // swipe end
//            }
//        });
//
//        return allTraceView;
//    }
//
//
//
//    class TraceAdapter extends BaseAdapter{
//
//        @Override
//        public int getCount() {
//            return traceInfoList.size();
//        }
//
//        @Override
//        public String getItem(int position) {
//            return traceInfoList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = View.inflate(getActivity().getApplicationContext(),
//                        R.layout.trace_list_item, null);
//                new ViewHolder(convertView);
//            }
//            ViewHolder holder = (ViewHolder) convertView.getTag();
//            String item = getItem(position);
////            holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
//            holder.tv_name.setText(item);
//            return convertView;
//        }
//
//        class ViewHolder {
//            //ImageView iv_icon;
//            TextView tv_name;
//
//            public ViewHolder(View view) {
//                //iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
//                tv_name = (TextView) view.findViewById(R.id.tv_name);
//                view.setTag(this);
//            }
//        }
//    }
//
//    private int dp2px(int dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }

    private List<String> getTraceData(){
        ArrayList<String> traceList = new ArrayList<String>();
        traceList.add("欧美汇 - 云海肴");
        traceList.add("芳草地 - 签证中心");
        traceList.add("新中关 - 麻辣诱惑");
        traceList.add("大悦城地下停车场 - 金逸影院");
        traceList.add("巴沟地铁站 - 海底捞");
        return traceList;
    }

    public void toNaviMap(View view) {
        Intent i=new Intent(getActivity(), NavigationMapActivity.class);
        startActivity(i);
    }

}
