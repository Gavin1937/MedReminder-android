package java.cs3337.medreminder_android.HttpClient;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpPutClient extends AsyncTask {

    private final OkHttpClient client = new OkHttpClient();
    public boolean ready = false;
    public boolean ok = false;
    public String payload = null;

    @Override
    protected String doInBackground(Object[] params) {

        Request.Builder builder = new Request.Builder();
        builder
                .url((String)params[0])
                .addHeader("Content-Type", "application/json")
                .put(RequestBody.create(
                    (String)params[1],
                    MediaType.parse("application/json")
                ))
        ;
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            payload = body;

            ready = true;
            ok = true;
            return body;
        } catch (Exception e) {
            ready = true;
            ok = false;
            payload = null;
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasPayload()
    {
        return (this.payload != null);
    }

    public JSONObject jsonObject() throws JSONException
    {
        if (this.payload != null)
            return (new JSONObject(this.payload));
        throw new JSONException("Payload is null.");
    }

    public JSONArray jsonArray() throws JSONException
    {
        if (this.payload != null)
            return (new JSONArray(this.payload));
        throw new JSONException("Payload is null.");
    }

}
