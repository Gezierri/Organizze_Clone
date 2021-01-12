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


public class DespesaActivity extends AppCompatActivity {

    private EditText editValor;
    private Movimentacao movimentacao;
    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private FloatingActionButton fb;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference database = ConfiguracaoFirebase.getDatabase();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        txtData = findViewById(R.id.txtData);
        txtCategoria = findViewById(R.id.txtCategoria);
        txtDescricao = findViewById(R.id.txtDescricao);
        editValor = findViewById(R.id.editValor);
        fb = findViewById(R.id.fb);

        String data = DateCustom.dataAtual();
        txtData.setText(data);
        recuperarDespesaTotal();
    }

    public void salvar(View view) {

        if (validarCampos()) {

            movimentacao = new Movimentacao();
            String data = txtData.getText().toString();
            Double valorRecuperado = Double.parseDouble(editValor.getText().toString());

            movimentacao.setValor(Double.parseDouble(editValor.getText().toString()));
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("d");

            Double despesaAtualizada = valorRecuperado + despesaTotal;
            movimentacao.salvar(data);
            atualizarDespesa(despesaAtualizada);
            limparCampos();
            Toast.makeText(DespesaActivity.this, "Despesa salva com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validarCampos() {

        String valor = editValor.getText().toString();
        String data = txtData.getText().toString();
        String categoria = txtCategoria.getText().toString();
        String descricao = txtDescricao.getText().toString();

        if (!valor.isEmpty()) {
            if (!data.isEmpty()) {
                if (!categoria.isEmpty()) {
                    if (!descricao.isEmpty()) {
                        return true;
                    } else {
                        Toast.makeText(DespesaActivity.this, "Preencha o campo descrição", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(DespesaActivity.this, "Preencha o campo categoria", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(DespesaActivity.this, "Preencha o campo data", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(DespesaActivity.this, "Preencha o campo valor", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void recuperarDespesaTotal() {

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = database.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(Double despesa) {

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = database.child("usuarios").child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesa);
    }

    private void limparCampos() {
        editValor.setText("");
        txtCategoria.setText("");
        txtDescricao.setText("");
    }

}