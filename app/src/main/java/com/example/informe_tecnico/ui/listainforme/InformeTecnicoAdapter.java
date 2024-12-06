package com.example.informe_tecnico.ui.listainforme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informe_tecnico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class InformeTecnicoAdapter extends RecyclerView.Adapter<InformeTecnicoAdapter.InformeTecnicoViewHolder> {
    private List<InformeTecnico> listaInformesTecnicos;
    private OnItemClickListener listener;

    // Constructor actualizado para recibir el listener
    public InformeTecnicoAdapter(List<InformeTecnico> listaInformesTecnicos, OnItemClickListener listener) {
        this.listaInformesTecnicos = listaInformesTecnicos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InformeTecnicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.informetecnico_item, parent, false);
        return new InformeTecnicoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InformeTecnicoViewHolder holder, int position) {
        InformeTecnico informeTecnico = listaInformesTecnicos.get(position);

        // Asignando valores a los TextViews
        holder.textViewTituloInforme.setText(informeTecnico.getTitulo());
        holder.textViewNombreEquipo.setText(informeTecnico.getNombre_Equipo());
        holder.textViewDiagnosticoEquipo.setText(informeTecnico.getDiagnostico());
    }

    @Override
    public int getItemCount() {
        return listaInformesTecnicos.size();
    }

    // ViewHolder para el RecyclerView
    public class InformeTecnicoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTituloInforme, textViewNombreEquipo, textViewDiagnosticoEquipo;

        public InformeTecnicoViewHolder(View itemView) {
            super(itemView);
            // Enlazando los TextViews con los IDs de la vista
            textViewTituloInforme = itemView.findViewById(R.id.textViewTituloInforme);
            textViewNombreEquipo = itemView.findViewById(R.id.textViewNombreEquipo);
            textViewDiagnosticoEquipo = itemView.findViewById(R.id.textViewDiagnosticoEquipo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(listaInformesTecnicos.get(position));
                    }
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(InformeTecnico informeTecnico);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar un informe técnico de la lista y Firebase
    public void eliminarInformeTecnico(int position) {
        InformeTecnico informeTecnico = listaInformesTecnicos.get(position);
        FirebaseFirestore.getInstance().collection("Informe_Tecnico")
                .document(informeTecnico.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaInformesTecnicos.remove(position);
                    notifyItemRemoved(position);
                });
    }
}

