package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
public class StopTime {
    String tripId;
    String stopId;
    int stopSequence;

    static StopTime fromJson(JSONObject json) {
        StopTime st = new StopTime();
        st.tripId = json.optString("trip_id");
        st.stopId = json.optString("stop_id");
        st.stopSequence = json.optInt("stop_sequence");
        return st;
    }
}