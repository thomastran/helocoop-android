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
    @POST("/users/requestcode/")
    Response requestActivateCode(@Field("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("/users/verifycode/")
    Response verifyActivateCode(@Field("phone_number") String phone_number, @Field("activate_code") String activate_code,@Field("instance_id") String instance_id );

    @FormUrlEncoded
    @POST("/users/update/")
    Response updateInfo(@Field("phone_number") String phone_number, @Field("email") String email, @Field("address") String address, @Field("name") String name);

    @FormUrlEncoded
    @POST("/users/updatelocation/")
    Response updateLocation(@Field("latitude") float latitude, @Field("longitude") float longitude, @Field("token") String token);

    @FormUrlEncoded
    @POST("/users/turnoff/")
    Response turnOffSamaritan(@Field("token") String token);


}
