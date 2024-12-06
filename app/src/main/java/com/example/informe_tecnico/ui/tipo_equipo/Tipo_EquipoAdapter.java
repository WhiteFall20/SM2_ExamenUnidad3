package com.example.informe_tecnico.ui.tipo_equipo;

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

public class Tipo_EquipoAdapter extends RecyclerView.Adapter<Tipo_EquipoAdapter.TipoEquipoViewHolder> {
    private List<Tipo_Equipo> listaTiposEquipo;
    private OnItemClickListener listener;

    // Constructor actualizado para recibir el listener
    public Tipo_EquipoAdapter(List<Tipo_Equipo> listaTiposEquipo, OnItemClickListener listener) {
        this.listaTiposEquipo = listaTiposEquipo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TipoEquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tipo_equipo_item, parent, false);
        return new TipoEquipoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TipoEquipoViewHolder holder, int position) {
        Tipo_Equipo tipoEquipo = listaTiposEquipo.get(position);
        holder.nombreTipoEquipo.setText(tipoEquipo.getNombre_Tipo_Equipo());
        holder.descripcionTipoEquipo.setText(tipoEquipo.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaTiposEquipo.size();
    }

    // ViewHolder para el RecyclerView
    public class TipoEquipoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTipoEquipo, descripcionTipoEquipo;

        public TipoEquipoViewHolder(View itemView) {
            super(itemView);
            nombreTipoEquipo = itemView.findViewById(R.id.textViewNombreTipoEquipo);
            descripcionTipoEquipo = itemView.findViewById(R.id.textViewDescripcionTipoEquipo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(listaTiposEquipo.get(position));
                    }
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(Tipo_Equipo tipoEquipo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar un tipo de equipo de la lista y Firebase
    public void eliminarTipoEquipo(int position) {
        Tipo_Equipo tipoEquipo = listaTiposEquipo.get(position);
        FirebaseFirestore.getInstance().collection("Tipo_Equipo")
                .document(tipoEquipo.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaTiposEquipo.remove(position);
                    notifyItemRemoved(position);
                });
    }
}
