package com.example.informe_tecnico.ui.o_actividades;

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

public class OtrasActividadesAdapter extends RecyclerView.Adapter<OtrasActividadesAdapter.OtrasActividadesViewHolder> {
    private List<OtrasActividades> listaOtrasActividades;
    private OnItemClickListener listener;

    // Constructor actualizado para recibir el listener
    public OtrasActividadesAdapter(List<OtrasActividades> listaOtrasActividades, OnItemClickListener listener) {
        this.listaOtrasActividades = listaOtrasActividades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OtrasActividadesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.otrasactividades_item, parent, false);
        return new OtrasActividadesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OtrasActividadesViewHolder holder, int position) {
        OtrasActividades otrasActividades = listaOtrasActividades.get(position);
        holder.nombreOtrasActividades.setText(otrasActividades.getNombre_OtrasActividades());
        holder.descripcionOtrasActividades.setText(otrasActividades.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaOtrasActividades.size();
    }

    // ViewHolder para el RecyclerView
    public class OtrasActividadesViewHolder extends RecyclerView.ViewHolder {
        TextView nombreOtrasActividades, descripcionOtrasActividades;

        public OtrasActividadesViewHolder(View itemView) {
            super(itemView);
            nombreOtrasActividades = itemView.findViewById(R.id.textViewNombreOtrasActividades);
            descripcionOtrasActividades = itemView.findViewById(R.id.textViewDescripcionOtrasActividades);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(listaOtrasActividades.get(position));
                }
            });
        }
    }

    // Interface para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(OtrasActividades otrasActividades);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Método para eliminar una actividad de la lista y Firebase
    public void eliminarOtrasActividades(int position) {
        OtrasActividades otrasActividades = listaOtrasActividades.get(position);
        FirebaseFirestore.getInstance().collection("OtrasActividades")
                .document(otrasActividades.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaOtrasActividades.remove(position);
                    notifyItemRemoved(position);
                });
    }
}

