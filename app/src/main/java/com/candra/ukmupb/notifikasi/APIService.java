package com.candra.ukmupb.notifikasi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvyFBjaU:APA91bGcBb8b0mwr9SeCzQQmgfpWqqBetbCMxJFnyVBtIeEtW8sHeKI56ryzrYkn2d1_BVcr4GH3x63RgUk1eUwVKhPxTc2s_9YvyeD9ZwMiq6NLE0qL7fg2VSN5Q-VnF5odVrmMNbz8"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
