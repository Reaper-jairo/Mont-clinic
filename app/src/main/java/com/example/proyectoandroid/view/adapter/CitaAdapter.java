package com.example.proyectoandroid.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Cita;
import com.example.proyectoandroid.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {
    private List<Cita> citas = new ArrayList<>();

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citas.get(position);
        holder.bind(cita);
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas != null ? citas : new ArrayList<>();
        notifyDataSetChanged();
    }

    class CitaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFechaCita;
        private final TextView tvHoraCita;
        private final TextView tvMotivoCita;
        private final TextView tvMedicoCita;
        private final TextView tvEstadoCita;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvHoraCita = itemView.findViewById(R.id.tvHoraCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
            tvMedicoCita = itemView.findViewById(R.id.tvMedicoCita);
            tvEstadoCita = itemView.findViewById(R.id.tvEstadoCita);
        }

        public void bind(Cita cita) {
            String na = itemView.getContext().getString(R.string.na);
            
            if (cita.getFecha() != null) {
                tvFechaCita.setText(DateUtils.formatDateForDisplay(cita.getFecha()));
            } else {
                tvFechaCita.setText(na);
            }
            
            tvHoraCita.setText(cita.getHora() != null ? cita.getHora() : "--:--");
            tvMotivoCita.setText(cita.getMotivo() != null ? cita.getMotivo() : na);
            tvMedicoCita.setText(cita.getMedico() != null ? cita.getMedico() : na);
            
            String estado = cita.getEstado() != null ? cita.getEstado() : "pendiente";
            String estadoTexto;
            int colorRes;
            
            if ("confirmada".equalsIgnoreCase(estado)) {
                estadoTexto = itemView.getContext().getString(R.string.estado_confirmada);
                colorRes = R.color.success;
            } else if ("pendiente".equalsIgnoreCase(estado)) {
                estadoTexto = itemView.getContext().getString(R.string.estado_pendiente_cita);
                colorRes = R.color.warning;
            } else {
                estadoTexto = itemView.getContext().getString(R.string.estado_cancelada);
                colorRes = R.color.error;
            }
            
            tvEstadoCita.setText(estadoTexto);
            tvEstadoCita.setTextColor(itemView.getContext().getColor(colorRes));
        }
    }
}

