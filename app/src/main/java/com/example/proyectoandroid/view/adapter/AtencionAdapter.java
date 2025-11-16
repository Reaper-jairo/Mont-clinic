package com.example.proyectoandroid.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Atencion;
import com.example.proyectoandroid.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AtencionAdapter extends RecyclerView.Adapter<AtencionAdapter.AtencionViewHolder> {
    private List<Atencion> atenciones = new ArrayList<>();

    @NonNull
    @Override
    public AtencionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atencion, parent, false);
        return new AtencionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AtencionViewHolder holder, int position) {
        Atencion atencion = atenciones.get(position);
        holder.bind(atencion);
    }

    @Override
    public int getItemCount() {
        return atenciones.size();
    }

    public void setAtenciones(List<Atencion> atenciones) {
        this.atenciones = atenciones != null ? atenciones : new ArrayList<>();
        notifyDataSetChanged();
    }

    class AtencionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFechaAtencion;
        private final TextView tvMedicoAtencion;
        private final TextView tvMotivoAtencion;
        private final TextView tvDiagnosticoAtencion;

        public AtencionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaAtencion = itemView.findViewById(R.id.tvFechaAtencion);
            tvMedicoAtencion = itemView.findViewById(R.id.tvMedicoAtencion);
            tvMotivoAtencion = itemView.findViewById(R.id.tvMotivoAtencion);
            tvDiagnosticoAtencion = itemView.findViewById(R.id.tvDiagnosticoAtencion);
        }

        public void bind(Atencion atencion) {
            String na = itemView.getContext().getString(R.string.na);
            
            if (atencion.getFecha() != null) {
                tvFechaAtencion.setText(DateUtils.formatDateForDisplay(atencion.getFecha()));
            } else {
                tvFechaAtencion.setText(na);
            }
            
            tvMedicoAtencion.setText(atencion.getMedico() != null ? atencion.getMedico() : na);
            tvMotivoAtencion.setText(atencion.getMotivo() != null ? atencion.getMotivo() : na);
            tvDiagnosticoAtencion.setText(atencion.getDiagnostico() != null ? atencion.getDiagnostico() : na);
        }
    }
}

