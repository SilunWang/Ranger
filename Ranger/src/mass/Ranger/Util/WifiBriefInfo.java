package mass.Ranger.Util;

public class WifiBriefInfo {
	public long timestamp;
	public String bssid;
	public int rssi;
	public int freq;
	
	public WifiBriefInfo(long timestamp, String bssid, int rssi,int freq){
		this.timestamp = timestamp;
		this.bssid = bssid;
		this.rssi = rssi;
		this.freq = freq;
	}
}
