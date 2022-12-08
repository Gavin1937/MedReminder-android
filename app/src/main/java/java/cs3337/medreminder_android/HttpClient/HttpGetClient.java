package java.cs3337.medreminder_android.HttpClient;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.Util.GlobVariables;
import java.cs3337.medreminder_android.Util.Utilities;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpGetClient extends AsyncTask {

    private final OkHttpClient client = new OkHttpClient();
    public boolean ready = false;
    public boolean ok = false;
    public int status = -1;
    public String payload = null;
    public JSONObject obj = null;

    @Override
    protected String doInBackground(Object[] params) {

        Request.Builder builder = new Request.Builder();
        builder
            .url((String)params[0])
            .addHeader("Content-Type", "application/json")
            .addHeader("username", Utilities.getUsername())
            .addHeader("secret", Utilities.getSecret())
            .get()
        ;
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            if (body != null && !body.trim().isEmpty()) {
                JSONObject obj = new JSONObject(body);
                payload = obj.getString("payload");
                ok = obj.getBoolean("ok");
                status = obj.getInt("status");
            }
            else {
                ok = false;
                status = -1;
            }
            ready = true;
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
        if (this.payload != null && this.obj != null)
            return this.obj;
        else if (this.payload != null && this.obj == null)
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
