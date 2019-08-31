package id.raflyfirdausy.skype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private LinearLayout layoutEmpty;
    private ImageView skype;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvRiwayat = findViewById(R.id.rvRiwayat);
        skype = findViewById(R.id.skype);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        skype.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View view) {
                dialog = new AlertDialog.Builder(MainActivity.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.layout_ip, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Setting Server");

                @SuppressLint("CutPasteId") final EditText alamatServer = dialogView.findViewById(R.id.alamatServer);
                @SuppressLint("CutPasteId") Button btnOk = dialogView.findViewById(R.id.btnOk);
                alamatServer.setText(Config.HOST);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Config.HOST = alamatServer.getText().toString();
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue;
                requestQueue = Volley.newRequestQueue(context);
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        Config.HOST + "/sKYPE-PKM/read_riwayat.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    Config.jumlah_data_terbaru = jsonArray.length();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(stringRequest);

//                Toast.makeText(MainActivity.this,
//                        "jumlah_data_sekarang : " + Config.jumlah_data_sekarang +
//                        "\njumlah_data_terbaru : " + Config.jumlah_data_terbaru,
//                        Toast.LENGTH_LONG)
//                        .show();

                handler.postDelayed(this, 1000);
                if (Config.jumlah_data_sekarang < Config.jumlah_data_terbaru) {
                    getRiwayat();
                }

            }
        }, 1000);


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
                            Config.jumlah_data_sekarang = jsonArray.length();
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
//                            if(list.size() > 0){
//                                layoutEmpty.setVisibility(View.GONE);
//                                rvRiwayat.setVisibility(View.VISIBLE);
//                            } else {
//                                layoutEmpty.setVisibility(View.VISIBLE);
//                                rvRiwayat.setVisibility(View.GONE);
//                            }
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
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        requestQueue.add(stringRequest);
//        if(list.size() > 0){
//            layoutEmpty.setVisibility(View.VISIBLE);
//            rvRiwayat.setVisibility(View.GONE);
//        } else {
//            layoutEmpty.setVisibility(View.GONE);
//            rvRiwayat.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onRefresh() {
        getRiwayat();
    }
}
