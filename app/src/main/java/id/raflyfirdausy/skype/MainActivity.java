package id.raflyfirdausy.skype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    ShimmerRecyclerView rvRiwayat;
    private Context context = MainActivity.this;
    private List<RiwayatModel> list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvRiwayat = findViewById(R.id.rvRiwayat);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRiwayat();
    }

    private void getRiwayat() {
        swipeRefreshLayout.setRefreshing(true);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Config.HOST + "/sKYPE-PKM/read_riwayat.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(a);
                                RiwayatModel riwayatModel = new RiwayatModel();
                                riwayatModel.setId_riwayat(jsonObject.getString("id_riwayat"));
                                riwayatModel.setId_data(jsonObject.getString("id_data"));
                                riwayatModel.setMasuk_keluar(jsonObject.getString("masuk_keluar"));
                                riwayatModel.setWaktu(jsonObject.getString("waktu"));
                                riwayatModel.setPenduduk_tamu(jsonObject.getString("penduduk_tamu"));
                                riwayatModel.setNama(jsonObject.getString("nama"));
                                riwayatModel.setAlamat(jsonObject.getString("alamat"));
                                riwayatModel.setKtp(jsonObject.getString("ktp"));
                                list.add(riwayatModel);
                            }
                            RiwayatAdapter riwayatAdapter = new RiwayatAdapter(context, list);
                            @SuppressLint("WrongConstant")
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL, false);
                            rvRiwayat.setLayoutManager(mLayoutManager);
                            rvRiwayat.setAdapter(riwayatAdapter);
                            riwayatAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {
        getRiwayat();
    }
}
