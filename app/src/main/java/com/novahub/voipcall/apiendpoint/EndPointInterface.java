package com.novahub.voipcall.apiendpoint;

import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.model.Status;
import com.novahub.voipcall.model.Token;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by samnguyen on 16/12/2015.
 */
public interface EndPointInterface {

    @FormUrlEncoded
    @POST("/token/generate/")
    Token getAuthToken(@Field("from_contact") String from_contact);

    @FormUrlEncoded
    @POST("/conference/")
    Status getStatusAfterCall(@Field("People") String people);

    @FormUrlEncoded
    @POST("/users/register/")
    Response registerNewPhoneNumber(@Field("phone_number") String phone_number);
}
