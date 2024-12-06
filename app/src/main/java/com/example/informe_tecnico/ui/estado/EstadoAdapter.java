package com.example.informe_tecnico.ui.estado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informe_tecnico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EstadoAdapter extends RecyclerView.Adapter<EstadoAdapter.EstadoViewHolder> {
    private List<Estado> listaEstados;
    private OnItemClickListener listener;

    // Constructor para recibir la lista de estados y el listener
    public EstadoAdapter(List<Estado> listaEstados, OnItemClickListener listener) {
        this.listaEstados = listaEstados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EstadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout estado_item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.estado_item, parent, false);
        return new EstadoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadoViewHolder holder, int position) {
        // Obtener el objeto Estado desde la lista y asignar los valores a los TextViews
        Estado estado = listaEstados.get(position);
        holder.nombreEstado.setText(estado.getNombre_Estado());
        holder.descripcionEstado.setText(estado.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaEstados.size();
    }

    // ViewHolder para el RecyclerView, que maneja las vistas del estado_item.xml
    public class EstadoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreEstado, descripcionEstado;

        public EstadoViewHolder(View itemView) {
            super(itemView);
            nombreEstado = itemView.findViewById(R.id.textViewNombreEstado); // Vincular con el TextView en estado_item.xml
            descripcionEstado = itemView.findViewById(R.id.textViewDescripcionEstado); // Vincular con el TextView en estado_item.xml

            // Manejar el clic en cada ítem
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(listaEstados.get(position)); // Notificar al listener cuando se hace clic
                    }
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(Estado estado);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar un estado de la lista y Firebase
    public void eliminarEstado(int position) {
        Estado estado = listaEstados.get(position);
        FirebaseFirestore.getInstance().collection("Estado")
                .document(estado.getId()) // Eliminar el documento de Firebase por ID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaEstados.remove(position); // Remover el estado de la lista local
                    notifyItemRemoved(position); // Notificar al adaptador para actualizar la vista
                });
    }
}

