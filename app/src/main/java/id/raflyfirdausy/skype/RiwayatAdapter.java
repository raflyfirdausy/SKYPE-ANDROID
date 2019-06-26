package id.raflyfirdausy.skype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<RiwayatModel> list;
    private Context context;

    public RiwayatAdapter(Context context, List<RiwayatModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatModel riwayatModel = list.get(position);
        holder.setDataKeView(context, riwayatModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivKTP;
        TextView tvNamaOrang;
        TextView tvWaktu;
        TextView tvPendudukTamu;
        TextView tvAlamat;
        TextView tvStatus;
        LinearLayout layoutBackground;

        ViewHolder(View view) {
            super(view);
            ivKTP = view.findViewById(R.id.ivKTP);
            tvNamaOrang = view.findViewById(R.id.tvNamaOrang);
            tvWaktu = view.findViewById(R.id.tvWaktu);
            tvPendudukTamu = view.findViewById(R.id.tvPendudukTamu);
            tvAlamat = view.findViewById(R.id.tvAlamat);
            tvStatus = view.findViewById(R.id.tvStatus);
            layoutBackground = view.findViewById(R.id.layoutBackground);
        }

        @SuppressLint("SetTextI18n")
        public void setDataKeView(Context context, RiwayatModel data) {

            if (!data.getKtp().equalsIgnoreCase("gaada")) {
                Picasso.get().load(Config.HOST + data.getKtp()).into(ivKTP);
            } else {
                String duaHuruf = data.getNama().substring(0, 2);
                TextDrawable gambar = TextDrawable.builder().buildRoundRect(duaHuruf, Color.parseColor("#000000"), 8);
                ivKTP.setImageDrawable(gambar);
            }

            if (data.getMasuk_keluar().equalsIgnoreCase("masuk")) {
                layoutBackground.setBackgroundColor(Color.parseColor("#23A352"));
            } else {
                layoutBackground.setBackgroundColor(Color.parseColor("#DB3434"));
            }

            tvNamaOrang.setText(data.getNama().toUpperCase());
            tvWaktu.setText(data.getWaktu());
            tvPendudukTamu.setText(data.getPenduduk_tamu());
            tvAlamat.setText(data.getAlamat());
            tvStatus.setText(data.getMasuk_keluar().substring(0, 1).toUpperCase() + data.getMasuk_keluar().substring(1));

        }
    }
}
