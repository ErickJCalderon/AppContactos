package com.example.appcontacto;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Clase controladora del RecyclerView establecido en el activity_main
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    /**
     * The Activity.
     */
    Activity activity;
    /**
     * The Array list.
     */
    ArrayList <Contacto> arrayList;
    private ItemClickListener itemClickListener;


    /**
     * Contructor del MainAdapter
     *
     * @param activity          Activity en la que se encuentra
     * @param arrayList         ArrayList que implementa
     * @param itemClickListener ItemClickListener el cual va a referenciar para su uso
     */
    public MainAdapter(Activity activity, ArrayList<Contacto> arrayList, ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener= itemClickListener;
        notifyDataSetChanged();
    }


    /**
     * ViewHolder al cual se va a referenciar, en este caso pertenece al layout item_contact
     * @param parent ViewGroup al que pertenece
     * @param viewType ViewType que recibe
     * @return Devuelve una view nueva con los parametros que se le han establecido
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent,false);
        return new ViewHolder(view);
    }

    /**
     * Metodo que controla union del ViewHolder al cual va a estar vinculador
     * @param holder ViewHolder al que hace referencia
     * @param position Indica la posicion seleccionada
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contacto contacto = arrayList.get(position);

        holder.tvNombre.setText(contacto.getNombre());
        holder.tvNumero.setText(contacto.getNumero());

        //Esto devolvera la posicion del item en el RecyclerView
        holder.itemView.setOnClickListener(view -> itemClickListener.onItemClick(arrayList.get(position)));
    }


    /**
     * Metodoo que devuelve la cantidad de items que hay
     * @return Devuleve el tamaño del arrayList
     */
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    /**
     * Interfaz que implementa el clickListener de cada item
     */
    public interface ItemClickListener {
        /**
         * On item click.
         *
         * @param contacto the contacto
         */
        void onItemClick(Contacto contacto);
    }

    /**
     * Clase ViewHolder que extiende de RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        /**
         * The Tv nombre.
         */
        TextView tvNombre, /**
         * The Tv numero.
         */
        tvNumero;

        /**
         * Constructor del ViewHolder
         *
         * @param itemView View a la que se refiere
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvNumero = itemView.findViewById(R.id.tv_numero);
        }

    }

}
