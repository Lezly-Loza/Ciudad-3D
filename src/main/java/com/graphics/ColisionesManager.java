package com.graphics;

import java.util.List;

public class ColisionesManager {

    public static boolean puedeCircular(float nx, float nz, float radioCuerpo,
                                        List<float[]> postesSemaforos,
                                        TraficoManager.AutoTrafico[] trafico) {
        float celda = ConstantesCiudad.CELDA;
        float limite = ConstantesCiudad.LIMITE;
        int[][] mapa = ConstantesCiudad.MAPA;

        if (Math.abs(nx) > (limite - radioCuerpo) || Math.abs(nz) > (limite - radioCuerpo)) {
            return false;
        }

        float radioObstaculo = (celda * 0.88f) / 2.0f;
        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                int tipo = mapa[f][c];
                if (tipo != 0) {
                    float cx = ConstantesCiudad.centro(c);
                    float cz = ConstantesCiudad.centro(f);

                    // A) Gasolinera (tipo 8)
                    if (tipo == 8) {
                        float[][] colsGas = { {-4.8f, -3.8f}, {4.8f, -3.8f}, {-4.8f, 3.8f}, {4.8f, 3.8f} };
                        for (float[] cg : colsGas) {
                            float dx = nx - (cx + cg[0]);
                            float dz = nz - (cz + cg[1]);
                            if ((dx * dx + dz * dz) < (radioCuerpo + 0.30f) * (radioCuerpo + 0.30f)) return false;
                        }

                        float[] islasX = {-3.6f, 3.6f};
                        for (float ix : islasX) {
                            float cercaX = Math.max((cx + ix) - 0.40f, Math.min(nx, (cx + ix) + 0.40f));
                            float cercaZ = Math.max(cz - 2.25f, Math.min(nz, cz + 2.25f));
                            float dx = nx - cercaX;
                            float dz = nz - cercaZ;
                            if ((dx * dx + dz * dz) < (radioCuerpo * radioCuerpo)) return false;
                        }
                        continue;
                    }

                    // B) Hospital (3), Policía (4), Taller (7): estacionamientos libres
                    if (tipo == 3 || tipo == 4 || tipo == 7) {
                        if (nz > cz && nz < cz + radioObstaculo + 0.5f && Math.abs(nx - cx) < radioObstaculo - 0.4f) {
                            continue;
                        }
                    }

                    // C) Manzanas normales
                    float cercaX = Math.max(cx - radioObstaculo, Math.min(nx, cx + radioObstaculo));
                    float cercaZ = Math.max(cz - radioObstaculo, Math.min(nz, cz + radioObstaculo));

                    float dx = nx - cercaX;
                    float dz = nz - cercaZ;

                    if ((dx * dx + dz * dz) < (radioCuerpo * radioCuerpo)) {
                        return false;
                    }
                }
            }
        }

        // Farolas
        float rFarola = 0.55f;
        for (float[] farola : ConstantesCiudad.POS_FAROLAS) {
            float dx = nx - farola[0];
            float dz = nz - farola[1];
            if ((dx * dx + dz * dz) < (radioCuerpo + rFarola) * (radioCuerpo + rFarola)) {
                return false;
            }
        }

        // Semáforos
        float rSemaforo = 0.45f;
        if (postesSemaforos != null) {
            for (float[] sem : postesSemaforos) {
                float dx = nx - sem[0];
                float dz = nz - sem[1];
                if ((dx * dx + dz * dz) < (radioCuerpo + rSemaforo) * (radioCuerpo + rSemaforo)) {
                    return false;
                }
            }
        }

        // Tráfico autónomo
        if (trafico != null) {
            for (TraficoManager.AutoTrafico auto : trafico) {
                float dx = nx - auto.x;
                float dz = nz - auto.z;
                if ((dx * dx + dz * dz) < (radioCuerpo + 1.15f) * (radioCuerpo + 1.15f)) {
                    return false;
                }
            }
        }

        return true;
    }
}