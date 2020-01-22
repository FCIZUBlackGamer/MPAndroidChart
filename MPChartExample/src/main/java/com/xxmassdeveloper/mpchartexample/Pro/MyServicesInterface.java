package com.xxmassdeveloper.mpchartexample.Pro;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyServicesInterface {

    @GET("/ords/fluid/api/getProviderTiming?")
    Call<Model> getProviderTiming(@Query("fromDate") String fromDate, @Query("toDate") String toDate, @Query("specialityCode") String specialityCode);

    @GET("/ords/fluid/api/getSpecialities")
    Call<SpecialitiesResponse> getSpecialities();
}
