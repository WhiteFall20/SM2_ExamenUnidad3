package com.example.informe_tecnico.ui.sede;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.informe_tecnico.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SedeAdapter extends RecyclerView.Adapter<SedeAdapter.SedeViewHolder> {
    private List<Sede> listaSedes;
    private OnItemClickListener listener;

    // Constructor actualizado para recibir el listener
    public SedeAdapter(List<Sede> listaSedes, OnItemClickListener listener) {
        this.listaSedes = listaSedes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SedeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sede_item, parent, false);
        return new SedeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SedeViewHolder holder, int position) {
        Sede sede = listaSedes.get(position);
        holder.nombreSede.setText(sede.getNombre_Sede());
        holder.descripcionSede.setText(sede.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaSedes.size();
    }

    // ViewHolder para el RecyclerView
    public class SedeViewHolder extends RecyclerView.ViewHolder {
        TextView nombreSede, descripcionSede;

        public SedeViewHolder(View itemView) {
            super(itemView);
            nombreSede = itemView.findViewById(R.id.textViewNombreSede);
            descripcionSede = itemView.findViewById(R.id.textViewDescripcionSede);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(listaSedes.get(position));
                    }
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(Sede sede);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar una sede de la lista y Firebase
    public void eliminarSede(int position) {
        Sede sede = listaSedes.get(position);
        FirebaseFirestore.getInstance().collection("Sede")
                .document(sede.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaSedes.remove(position);
                    notifyItemRemoved(position);
                });
    }
}
