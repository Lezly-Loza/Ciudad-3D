package com.graphics;

public class ConstantesCiudad {
    public static final float CELDA = 12.0f;

    // Matriz urbana 11x11
    // 0: Calle | 1: Edificios | 2: Parques | 3: Hospital | 4: Policía
    // 5: Mall | 6: Comida | 7: Taller | 8: Gasolinera | 9: Colegio
    public static final int[][] MAPA = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 3, 0, 1, 0, 2, 0, 4, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 5, 0, 2, 0, 6, 0, 1, 0, 7, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 2, 0, 8, 0, 2, 0, 8, 0, 2, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 9, 0, 1, 0, 1, 0, 2, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 7, 0, 2, 0, 5, 0, 3, 0, 6, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public static final float LIMITE = (MAPA.length * CELDA) / 2.0f; // 66.0f

    public static final float[][] POS_FAROLAS = {
        {-28.5f, -28.5f}, { 7.5f, -28.5f}, { 43.5f, -28.5f},
        {-28.5f,   7.5f}, { 7.5f,   7.5f}, { 43.5f,   7.5f},
        {-28.5f,  43.5f}, { 7.5f,  43.5f}, { 43.5f,  43.5f}
    };

    public static float centro(int indice) {
        return -LIMITE + (CELDA * indice) + (CELDA / 2.0f);
    }
}