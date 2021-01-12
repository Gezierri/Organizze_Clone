package com.gezierri.organizze.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gezierri.organizze.R;
import com.gezierri.organizze.config.ConfiguracaoFirebase;
import com.gezierri.organizze.helper.Base64Custom;
import com.gezierri.organizze.helper.DateCustom;
import com.gezierri.organizze.model.Movimentacao;
import com.gezierri.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitaActivity extends AppCompatActivity {

    private EditText valor;
    private TextInputEditText data, categoria, descricao;
    private FloatingActionButton fab;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference database  = ConfiguracaoFirebase.getDatabase();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        valor = findViewById(R.id.editValor);
        data = findViewById(R.id.txtData);
        categoria = findViewById(R.id.txtCategoria);
        descricao = findViewById(R.id.txtDescricao);
        fab = findViewById(R.id.fabSalvar);

        data.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }

    public void salvar(View view) {

        if (validarCampos()) {

            String dataString = data.getText().toString();

            Movimentacao movimentacao = new Movimentacao();
            Double valorRecuperado = Double.parseDouble(valor.getText().toString());
            movimentacao.setData(dataString);
            movimentacao.setValor(Double.parseDouble(valor.getText().toString()));
            movimentacao.setCategoria(categoria.getText().toString());
            movimentacao.setDescricao(descricao.getText().toString());
            movimentacao.setTipo("r");
            movimentacao.salvar(dataString);
            Double receitaAtualizada = valorRecuperado + receitaTotal;
            atualizarReceita(receitaAtualizada);

            Toast.makeText(ReceitaActivity.this, "Receita salva com sucesso!", Toast.LENGTH_SHORT).show();
            limparCampos();
        }
    }

    private Boolean validarCampos() {


        String campoValor = valor.getText().toString();
        String campoData = valor.getText().toString();
        String campoCategoria = valor.getText().toString();
        String campoDescricao = valor.getText().toString();

        if (!campoValor.isEmpty()) {
            if (!campoData.isEmpty()) {
                if (!campoCategoria.isEmpty()) {
                    if (!campoDescricao.isEmpty()) {

                    } else {
                        Toast.makeText(ReceitaActivity.this, "Preencha o campo descrição", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(ReceitaActivity.this, "Preencha o campo categoria", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(ReceitaActivity.this, "Preencha o campo data", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(ReceitaActivity.this, "Preencha o campo valor", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limparCampos() {
        valor.setText("");
        categoria.setText("");
        descricao.setText("");
    }

    private void atualizarReceita(Double receita){

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = database.child("usuarios").child(idUsuario);
        usuarioRef.child("receitaTotal").setValue(receita);
    }

    public void recuperarReceitaTotal(){

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = database.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}