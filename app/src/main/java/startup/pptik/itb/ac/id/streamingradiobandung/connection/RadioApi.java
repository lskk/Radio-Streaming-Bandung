package startup.pptik.itb.ac.id.streamingradiobandung.connection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import startup.pptik.itb.ac.id.streamingradiobandung.models.RadioModel;

/**
 * Created by hynra on 5/10/2016.
 */
public interface RadioApi {

    @GET("getalldata")
    Call<List<RadioModel>> groupList();
}
