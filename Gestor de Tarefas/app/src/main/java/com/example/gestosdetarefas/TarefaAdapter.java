package com.example.gestosdetarefas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.ViewHolder> {

    private ArrayList<Tarefa> lista;

    private OnCheckBoxClickListener checkBoxClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public TarefaAdapter(ArrayList<Tarefa> lista) {
        this.lista = lista;
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(int position, boolean isChecked);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    @NonNull
    @Override
    public TarefaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarefa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarefaAdapter.ViewHolder holder, int position) {
        Tarefa tarefa = lista.get(position);
        holder.textTitulo.setText(tarefa.getTitulo());
        holder.checkBoxConcluida.setChecked(tarefa.isConcluida());

        holder.checkBoxConcluida.setOnClickListener(v -> {
            if (checkBoxClickListener != null) {
                checkBoxClickListener.onCheckBoxClick(position, holder.checkBoxConcluida.isChecked());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (itemLongClickListener != null) {
                itemLongClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxConcluida;
        TextView textTitulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxConcluida = itemView.findViewById(R.id.checkBoxConcluida);
            textTitulo = itemView.findViewById(R.id.textTitulo);
        }
    }
}
