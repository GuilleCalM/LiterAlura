package com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Idioma {
    Español("es","Español"),
    Ingles("en","Ingles"),
    Otro("otro", "Otro");

    private String idiomaApi;
    private String idiomaEspaniol;

    Idioma(String idiomaApi, String idiomaEspaniol) {
        this.idiomaApi = idiomaApi;
        this.idiomaEspaniol = idiomaEspaniol;
    }

    public static Idioma fromString(String text){
        for(Idioma idioma : Idioma.values()){
            if(idioma.idiomaApi.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        return Otro;
    }

    public static Idioma fromEspaniol(String text){
        for(Idioma idioma : Idioma.values()){
            if(idioma.idiomaEspaniol.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        return Otro;
    }

}
