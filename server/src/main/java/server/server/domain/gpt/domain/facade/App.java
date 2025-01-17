package server.server.domain.gpt.domain.facade;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String url = "https://typecast.ai/api/speak";
        String token = "Bearer __pltSb9poEjWpYZMQusfMQ7zKTjZmTmgfEd2je3xSWbr";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("actor_id", "622964d6255364be41659078");
            jsonObject.put("text", "안녕! 또 왔구나. 무슨 얘기를 나눠볼까? 함께 얘기하면 더 즐거울 텐데. \uD83D\uDE09\uD83D\uDCAC");
            jsonObject.put("lang", "auto");
            jsonObject.put("tempo", 1);
            jsonObject.put("volume", 100);
            jsonObject.put("pitch", 0);
            jsonObject.put("xapi_hd", true);
            jsonObject.put("max_seconds", 60);
            jsonObject.put("model_version", "latest");
            jsonObject.put("xapi_audio_format", "mp3");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", token)
                .build();


        Response response;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}