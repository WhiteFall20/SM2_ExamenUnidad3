package com.example.informe_tecnico.ui.area;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informe_tecnico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {
    private List<Area> listaAreas;
    private OnAreaClickListener listener;

    // Constructor para inicializar la lista de áreas y el listener
    public AreaAdapter(List<Area> listaAreas, OnAreaClickListener listener) {
        this.listaAreas = listaAreas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout del ítem para cada área
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, parent, false);
        return new AreaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        // Obtener el área actual
        Area area = listaAreas.get(position);

        // Asignar los valores a los TextViews
        holder.nombreArea.setText(area.getNombre_Area());
        holder.descripcionArea.setText(area.getDescripcion_Area());
        holder.sedeTextView.setText(area.getSedeNombre()); // Ahora simplemente usamos el nombre de la sede almacenado

        // Manejar clic en el área
        holder.bind(area, listener);
    }

    @Override
    public int getItemCount() {
        return listaAreas.size(); // Retorna el tamaño de la lista de áreas
    }

    // ViewHolder para el RecyclerView que contiene los TextView
    public class AreaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreArea, descripcionArea, sedeTextView;

        public AreaViewHolder(View itemView) {
            super(itemView);
            // Asignar los TextView definidos en el archivo XML
            nombreArea = itemView.findViewById(R.id.textViewNombreArea);
            descripcionArea = itemView.findViewById(R.id.textViewDescripcionArea);
            sedeTextView = itemView.findViewById(R.id.textSede);
        }

        // Método para manejar la selección de un área
        public void bind(final Area area, final OnAreaClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAreaClick(area); // Llama el método de clic en el área seleccionada
                }
            });
        }
    }

    // Interface para manejar los clics en los ítems
    public interface OnAreaClickListener {
        void onAreaClick(Area area);
    }

    // Método para eliminar un área de la lista y de Firebase
    public void eliminarArea(int position) {
        Area area = listaAreas.get(position);
        FirebaseFirestore.getInstance().collection("Area") // Asegúrate de que el nombre de la colección esté correcto
                .document(area.getId()) // Usamos el nombre del área para obtener el documento (o el ID si lo tienes almacenado en otra parte)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaAreas.remove(position); // Eliminar de la lista local
                    notifyItemRemoved(position); // Notificar al RecyclerView del cambio
                })
                .addOnFailureListener(e -> {
                    // Opcionalmente maneja el fallo de la operación de eliminación
                    Log.e("AreaAdapter", "Error eliminando el área", e);
                });
    }
}



