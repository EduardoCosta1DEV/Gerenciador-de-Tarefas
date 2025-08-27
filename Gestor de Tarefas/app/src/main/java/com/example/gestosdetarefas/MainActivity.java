package com.example.gestosdetarefas;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TarefaDBHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private TarefaAdapter adapter;
    private ArrayList<Tarefa> listaTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TarefaDBHelper(this);
        db = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerViewTarefas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaTarefas = carregarTarefas();
        adapter = new TarefaAdapter(listaTarefas);
        recyclerView.setAdapter(adapter);


        adapter.setOnCheckBoxClickListener(new TarefaAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClick(int position, boolean isChecked) {
                Tarefa tarefa = listaTarefas.get(position);
                atualizarTarefaConcluida(tarefa.getId(), isChecked);
                tarefa.setConcluida(isChecked);
            }
        });

        
        adapter.setOnItemLongClickListener(new TarefaAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                mostrarDialogEditarExcluir(position);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> mostrarDialogCriarTarefa());
    }

    private ArrayList<Tarefa> carregarTarefas() {
        ArrayList<Tarefa> lista = new ArrayList<>();
        Cursor cursor = db.query("tarefas", null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            int concluida = cursor.getInt(cursor.getColumnIndexOrThrow("concluida"));
            lista.add(new Tarefa(id, titulo, concluida == 1));
        }
        cursor.close();
        return lista;
    }

    private void inserirTarefa(String titulo) {
        ContentValues values = new ContentValues();
        values.put("titulo", titulo);
        values.put("concluida", 0);
        long id = db.insert("tarefas", null, values);
        if (id != -1) {
            listaTarefas.add(0, new Tarefa((int) id, titulo, false));
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            Toast.makeText(this, "Tarefa adicionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarTarefaConcluida(int id, boolean concluida) {
        ContentValues values = new ContentValues();
        values.put("concluida", concluida ? 1 : 0);
        db.update("tarefas", values, "id=?", new String[]{String.valueOf(id)});
    }

    private void atualizarTarefaTitulo(int id, String titulo, int position) {
        ContentValues values = new ContentValues();
        values.put("titulo", titulo);
        db.update("tarefas", values, "id=?", new String[]{String.valueOf(id)});
        listaTarefas.get(position).setConcluida(listaTarefas.get(position).isConcluida());
        listaTarefas.get(position).setTitulo(titulo);
        adapter.notifyItemChanged(position);
    }

    private void excluirTarefa(int id, int position) {
        db.delete("tarefas", "id=?", new String[]{String.valueOf(id)});
        listaTarefas.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Tarefa excluída", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogCriarTarefa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nova tarefa");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String titulo = input.getText().toString().trim();
            if (!titulo.isEmpty()) {
                inserirTarefa(titulo);
            } else {
                Toast.makeText(this, "Título não pode ser vazio", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void mostrarDialogEditarExcluir(int position) {
        Tarefa tarefa = listaTarefas.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar ou excluir tarefa");

        final EditText input = new EditText(this);
        input.setText(tarefa.getTitulo());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoTitulo = input.getText().toString().trim();
            if (!novoTitulo.isEmpty()) {
                atualizarTarefaTitulo(tarefa.getId(), novoTitulo, position);
            } else {
                Toast.makeText(this, "Título não pode ser vazio", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Excluir", (dialog, which) -> {
            excluirTarefa(tarefa.getId(), position);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
