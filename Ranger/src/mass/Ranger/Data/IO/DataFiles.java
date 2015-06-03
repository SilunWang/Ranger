package mass.Ranger.Data.IO;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import mass.Ranger.Activity.NavigationActivity;
import mass.Ranger.Device.AccessPointReading;
import mass.Ranger.Device.BackgroundPositioning;
import mass.Ranger.Util.WifiBriefInfo;
import mass.Ranger.View.PathView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 */
public class DataFiles {

	public static File root;
	public static File path;
	public static File stepFile;
	static XmlSerializer serializer = Xml.newSerializer();
	static XmlPullParser parser = Xml.newPullParser();
	public static FileOutputStream fileOutputStream;
	public static BufferedWriter out;
	Context context;
	SharedPreferences traceList;
	SharedPreferences.Editor traceListEditor;

	public DataFiles(Context context) {
		this.context = context;
		traceList = this.context.getSharedPreferences("traceList", Context.MODE_PRIVATE);
		traceListEditor = traceList.edit();
	}

	
	public enum FileType{
		wifiAP, bestAP, step, turn, mag, mag2
	}


	private static void clearTraces() {
		BackgroundPositioning.initial_radian = 0;
		BackgroundPositioning.stepTrace.clear();
		BackgroundPositioning.magnetTrace.clear();
		BackgroundPositioning.WiFis.clear();
		BackgroundPositioning.dispXArray.clear();
		BackgroundPositioning.traceRad.clear();
		BackgroundPositioning.WiFiIndex.clear();
		BackgroundPositioning.dispYArray.clear();
		BackgroundPositioning.realtimeX = 0;
		BackgroundPositioning.realtimeY = 0;
	}

	public static int init()
	{
		clearTraces();
		try {
			root = Environment.getExternalStorageDirectory();
	        root = new File(root, "/Travi-Navi");
			path = new File(root, BackgroundPositioning.patrollingID);
			
	        if(!path.exists())
	        	path.mkdirs();
	        
	        stepFile = new File(path, "step.txt");
			fileOutputStream = new FileOutputStream(stepFile, true);
			//out = new BufferedWriter(new FileWriter(stepFile));
			serializer.setOutput(fileOutputStream, "UTF-8");
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startDocument("UTF-8", true);
			return 0;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

    public static void readFile(String url) throws IOException, XmlPullParserException {
		// init
		clearTraces();
        File file = new File(url);
        if (!file.exists())
            return;

        FileInputStream fis = new FileInputStream(file);
        parser.setInput(fis, "UTF-8");
        int eventType = parser.getEventType();
		List<AccessPointReading> list = new ArrayList<AccessPointReading>(){};
		ArrayList<Double> maglist = new ArrayList<Double>();
        while (eventType != parser.END_DOCUMENT) {

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("step".equals(parser.getName())) {
						if (list.size() > 0) {
							BackgroundPositioning.WiFiIndex.add(list.size());
							BackgroundPositioning.WiFis.add(list);
							list = new ArrayList<AccessPointReading>();
						}
						if (maglist.size() > 0) {
							BackgroundPositioning.magnetTrace.add(maglist);
							maglist = new ArrayList<Double>();
						}
						float radian = Float.parseFloat(parser.nextText());
						if (BackgroundPositioning.stepTrace.size() == 0) {
							BackgroundPositioning.initial_radian = radian;
							BackgroundPositioning.radianInitialized = false;
						}
                        BackgroundPositioning.stepTrace.add(radian);
						NavigationActivity.terminalXCoordinate += Math.cos(radian) * PathView.stepLength;
						NavigationActivity.terminalYCoordinate -= Math.sin(radian) * PathView.stepLength;
						BackgroundPositioning.dispXArray.add(NavigationActivity.terminalXCoordinate);
						BackgroundPositioning.dispYArray.add(NavigationActivity.terminalYCoordinate);
						BackgroundPositioning.traceRad.add(radian);
                    }
					if ("mag".equals(parser.getName())) {
						if (maglist.size() < 5)
							maglist.add(Double.parseDouble(parser.nextText()));
					}
					if ("SSID".equals(parser.getName())) {
						try {
							AccessPointReading ap = new AccessPointReading();
							ap.RSSI = (Integer.parseInt(parser.getAttributeValue(null, "rssi")));
							ap.bssid = (parser.nextText());
							list.add(ap);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }

    }
	
	public void closeFiles() throws IOException {
		//out.flush();
		//out.close();

		try {
			serializer.endDocument();
            fileOutputStream.flush();
			fileOutputStream.close();
			if (traceListEditor != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
				int size;
				if (traceList.getString("trace_size", null) == null)
					size = 0;
				else
					size = Integer.parseInt(traceList.getString("trace_size", null));
				traceListEditor.putString("trace_size", String.valueOf(size+1));
				String timeStr = sdf.format(new Date());
				traceListEditor.putString("trace_item_" + String.valueOf(size + 1), timeStr + "#" + BackgroundPositioning.patrollingID);
				traceListEditor.commit();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void record(double x, double y) throws IOException {
		out.write(String.valueOf(x) + " " + String.valueOf(y) + "\r\n");
	}

	static ArrayList<String> aplist = new ArrayList<String>();
	static HashMap<String, Integer> apmap = new HashMap<String, Integer>();

	public static void recordAP(List<AccessPointReading> infoList) {
		apmap.clear();
		for (AccessPointReading anInfoList : infoList) {
			boolean inside = false;
			for (String ssid : aplist) {
				if (ssid.equals(anInfoList.getBssid()))
					inside = true;
			}
			if (!inside) {
				aplist.add(anInfoList.getBssid());
			}
			apmap.put(anInfoList.getBssid(), anInfoList.getRSSI());
		}

		try {
			for (int i = 0; i < 50; i++) {
				if (i < aplist.size())
					out.write(String.valueOf(apmap.get(aplist.get(i))) + " ");
				else
					out.write("0 ");
			}
			out.write("0\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void record(Object rec, FileType type) {
		
		try{
			switch (type) {
			
				case wifiAP:
					if (rec == null){
						Log.i("wifi", "no wifi??");
						break;
					}
					List<AccessPointReading> infoList = (List<AccessPointReading>)rec;
					for (AccessPointReading anInfoList : infoList) {
						serializer.startTag(null, "SSID");
						serializer.attribute(null, "rssi", String.valueOf(anInfoList.getRSSI()));
						serializer.text(anInfoList.getBssid());
						serializer.endTag(null, "SSID");
					}
					break;
					
				case bestAP:
					WifiBriefInfo bestAP = (WifiBriefInfo)rec;
					serializer.startTag(null, "bestAP");
					serializer.startTag(null, "SSID");
					serializer.attribute(null, "rssi", String.valueOf(bestAP.rssi));
					serializer.text(bestAP.bssid);
					serializer.endTag(null, "SSID");
					serializer.endTag(null, "bestAP");
					break;
					
				case step:
					String step = (String)rec;
					serializer.startTag(null, "step");
					serializer.text(step);
					serializer.endTag(null, "step");
					break;
					
				case turn:
					String turn = (String)rec;
					serializer.startTag(null, "turn");
					serializer.text(turn);
					serializer.endTag(null, "turn");
					break;
					
				case mag:
					String mag = (String)rec;
					serializer.startTag(null, "mag");
					serializer.text(mag);
					serializer.endTag(null, "mag");
					break;
				case mag2:
					String mag2 = (String)rec;
					serializer.startTag(null, "mag2");
					serializer.text(mag2);
					serializer.endTag(null, "mag2");
				default:
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

