package com.novahub.voipcall.apiendpoint;

import com.novahub.voipcall.model.Response;
import com.novahub.voipcall.model.Status;
import com.novahub.voipcall.model.Token;
import com.novahub.voipcall.model.WrapperRate;

import retrofit.http.Body;
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
    @POST("/voip/generatetoken/")
    Token getVoipToken(@Field("from_token") String from_token);

    @FormUrlEncoded
    @POST("/conference/")
    Status getStatusAfterCall(@Field("People") String people);

    @FormUrlEncoded
    @POST("/voip/requestcode/")
    Response requestActivateCode(@Field("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("/voip/verifycode/")
    Response verifyActivateCode(@Field("phone_number") String phone_number,
                                @Field("activate_code") String activate_code);

    @FormUrlEncoded
    @POST("/voip/update/")
    Response updateInfo(
            @Field("phone_number") String phone_number,
            @Field("email") String email,
            @Field("address") String address,
            @Field("name") String name,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("/voip/changeinfo/")
    Response changeInfo(
            @Field("token") String token,
            @Field("email") String email,
            @Field("address") String address,
            @Field("name") String name,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("/voip/updatelocation/")
    Response updateLocation(@Field("latitude") float latitude,
                            @Field("longitude") float longitude,
                            @Field("token") String token);

    @FormUrlEncoded
    @POST("/voip/turnoff/")
    Response turnOffSamaritan(@Field("token") String token);

    @FormUrlEncoded
    @POST("/voip/turnon/")
    Response turnOnSamaritan(@Field("token") String token);

    @FormUrlEncoded
    @POST("/voip/getinstanceid/")
    Response updateInstanceId(@Field("token") String token, @Field("instance_id") String instance_id);

    @FormUrlEncoded
    @POST("/voip/makeconferencecall/")
    Response makeConferenceCall(@Field("token") String token, @Field("name_room") String nameRoom);

    @FormUrlEncoded
    @POST("/voip/updatelocationservice/")
    Response updateLocationService(@Field("latitude") float latitude,
                                   @Field("longitude") float longitude, @Field("token") String token);


    @POST("/voip/rating")
    Response rate(@Body WrapperRate wrapperRate);

}
