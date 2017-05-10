package com.example.spiros.savetheteacher.Realm;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
/**
 * Created by Spiros on 9/5/2017.
 */

public interface RegionService {

    @GET("Region/(id)")

    Call<Region> getRegion(@Path("id") String id);

}
