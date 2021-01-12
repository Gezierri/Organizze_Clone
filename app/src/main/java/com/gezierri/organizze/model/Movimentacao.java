package com.gezierri.organizze.model;


import com.gezierri.organizze.config.ConfiguracaoFirebase;
import com.gezierri.organizze.helper.Base64Custom;
import com.gezierri.organizze.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {


    private String data;
    private String descricao;
    private String categoria;
    private double valor;
    private String tipo;
    private String key;

    public Movimentacao() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void salvar(String dataEscolhida){

        String mesAno = DateCustom.mesAno(dataEscolhida);
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
        String idUsuario = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());

        DatabaseReference firebase = ConfiguracaoFirebase.getDatabase();
        firebase.child("movimentacao")
                .child(idUsuario)
                .child(mesAno)
                .push()
                .setValue(this);
    }
}
