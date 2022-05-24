package com.example.appcontacto;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    Activity activity;
    ArrayList <Contacto> arrayList;
    private ItemClickListener itemClickListener;




    public MainAdapter(Activity activity, ArrayList<Contacto> arrayList, ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener= itemClickListener;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contacto contacto = arrayList.get(position);

        holder.tvNombre.setText(contacto.getNombre());
        holder.tvNumero.setText(contacto.getNumero());

        //Esto devolvera la posicion del item en el RecyclerView
        holder.itemView.setOnClickListener(view -> {
           itemClickListener.onItemClick(arrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface ItemClickListener {
        void onItemClick(Contacto contacto);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvNombre, tvNumero;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvNumero = itemView.findViewById(R.id.tv_numero);
        }

    }

}
