package com.example.informe_tecnico.ui.falla;

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

public class FallaAdapter extends RecyclerView.Adapter<FallaAdapter.FallaViewHolder> {
    private List<Falla> listaFallas;
    private OnItemClickListener listener;

    // Constructor actualizado para recibir el listener
    public FallaAdapter(List<Falla> listaFallas, OnItemClickListener listener) {
        this.listaFallas = listaFallas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FallaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.falla_item, parent, false);
        return new FallaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FallaViewHolder holder, int position) {
        Falla falla = listaFallas.get(position);
        holder.nombreFalla.setText(falla.getNombre_Falla());
        holder.descripcionFalla.setText(falla.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaFallas.size();
    }

    // ViewHolder para el RecyclerView
    public class FallaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreFalla, descripcionFalla;

        public FallaViewHolder(View itemView) {
            super(itemView);
            nombreFalla = itemView.findViewById(R.id.textViewNombreFalla);
            descripcionFalla = itemView.findViewById(R.id.textViewDescripcionFalla);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(listaFallas.get(position));
                    }
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(Falla falla);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar una falla de la lista y Firebase
    public void eliminarFalla(int position) {
        Falla falla = listaFallas.get(position);
        FirebaseFirestore.getInstance().collection("Falla")
                .document(falla.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaFallas.remove(position);
                    notifyItemRemoved(position);
                });
    }
}
