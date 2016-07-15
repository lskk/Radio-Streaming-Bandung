package startup.pptik.itb.ac.id.streamingradiobandung.models;

/**
 * Created by hynra on 5/10/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RadioModel {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("sitea_dd")
    @Expose
    public String siteaDd;
    @SerializedName("url_stream_stereo")
    @Expose
    public String urlStreamStereo;
    @SerializedName("url_stream_mono")
    @Expose
    public String urlStreamMono;
    @SerializedName("stat_mono")
    @Expose
    public Integer statMono;
    @SerializedName("stat_stereo")
    @Expose
    public Integer statStereo;
    @SerializedName("pathlogo")
    @Expose
    public String pathlogo;
    @SerializedName("kota")
    @Expose
    public String kota;
    @SerializedName("frekuensi")
    @Expose
    public String frekuensi;
    @SerializedName("band")
    @Expose
    public String band;
    @SerializedName("kategori")
    @Expose
    public String kategori;
    @SerializedName("kodepropinsi")
    @Expose
    public String kodepropinsi;
    @SerializedName("namapropinsi")
    @Expose
    public String namapropinsi;
    @SerializedName("favorited")
    @Expose
    public Integer favorited;

}
