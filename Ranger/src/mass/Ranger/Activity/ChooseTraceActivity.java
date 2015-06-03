package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.travinavi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class ChooseTraceActivity extends Activity {

    private ListView listView;
    SharedPreferences traceList;
    ArrayList<String> traceIDList = new ArrayList<String>();

    private void initView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTraceData());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String traceID = traceIDList.get(i);
                Uri data = Uri.parse(traceID);
                Toast.makeText(getApplicationContext(), traceID, Toast.LENGTH_SHORT).show();
                Intent result = new Intent(null, data);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }


    private List<String> getTraceData(){
        ArrayList<String> list = new ArrayList<String>();
        try {
            int size = Integer.parseInt(traceList.getString("trace_size", null));
            while (size > 0) {
                String str = traceList.getString("trace_item_" + String.valueOf(size), null);
                if (str != null) {
                    list.add(str.split("#")[0]);
                    traceIDList.add(str.split("#")[1]);
                }
                size--;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traceList = getSharedPreferences("traceList", Context.MODE_PRIVATE);
        setContentView(R.layout.all_trace_home);
        listView = (ListView) findViewById(R.id.all_trace_listView_id);
        initView();
    }
}
