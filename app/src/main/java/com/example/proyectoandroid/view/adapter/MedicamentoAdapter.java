package com.example.proyectoandroid.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Medicamento;
import com.example.proyectoandroid.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter para mostrar la lista de medicamentos
 */
public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {
    private List<Medicamento> medicamentos = new ArrayList<>();

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);
        holder.bind(medicamento);
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos != null ? medicamentos : new ArrayList<>();
        notifyDataSetChanged();
    }

    class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvDosis;
        private final TextView tvFrecuencia;
        private final TextView tvFechaInicio;
        private final TextView tvFechaFin;
        private final TextView tvMedico;
        private final TextView tvEstado;
        private final TextView tvObservaciones;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMedicamento);
            tvDosis = itemView.findViewById(R.id.tvDosis);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuencia);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);
            tvFechaFin = itemView.findViewById(R.id.tvFechaFin);
            tvMedico = itemView.findViewById(R.id.tvMedico);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
        }

        public void bind(Medicamento medicamento) {
            String na = itemView.getContext().getString(R.string.na);
            tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : na);
            tvDosis.setText(medicamento.getDosis() != null ? medicamento.getDosis() : na);
            tvFrecuencia.setText(medicamento.getFrecuencia() != null ? medicamento.getFrecuencia() : na);
            tvMedico.setText(medicamento.getMedico() != null ? medicamento.getMedico() : na);

            // Formatear fechas usando DateUtils
            if (medicamento.getFechaInicio() != null) {
                tvFechaInicio.setText(itemView.getContext().getString(R.string.label_desde, 
                        DateUtils.formatDateForDisplay(medicamento.getFechaInicio())));
            } else {
                tvFechaInicio.setText(itemView.getContext().getString(R.string.label_desde, na));
            }

            if (medicamento.getFechaFin() != null) {
                tvFechaFin.setText(itemView.getContext().getString(R.string.label_hasta, 
                        DateUtils.formatDateForDisplay(medicamento.getFechaFin())));
            } else {
                tvFechaFin.setText(itemView.getContext().getString(R.string.label_hasta, na));
            }

            // Mostrar observaciones si existen
            if (medicamento.getObservaciones() != null && !medicamento.getObservaciones().trim().isEmpty()) {
                tvObservaciones.setText(itemView.getContext().getString(R.string.label_observaciones, 
                        medicamento.getObservaciones()));
                tvObservaciones.setVisibility(View.VISIBLE);
            } else {
                tvObservaciones.setVisibility(View.GONE);
            }

            // Mostrar estado (activo/inactivo)
            Date ahora = new Date();
            Date fechaFin = medicamento.getFechaFin();
            Date fechaInicio = medicamento.getFechaInicio();
            
            if (fechaFin != null && ahora.after(fechaFin)) {
                tvEstado.setText(itemView.getContext().getString(R.string.estado_finalizado));
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            } else if (fechaInicio != null && ahora.before(fechaInicio)) {
                tvEstado.setText(itemView.getContext().getString(R.string.estado_pendiente));
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.warning));
            } else {
                tvEstado.setText(itemView.getContext().getString(R.string.estado_activo));
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.success));
            }
        }
    }
}

