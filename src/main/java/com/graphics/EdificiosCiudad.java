package com.graphics;

public class EdificiosCiudad {

    public static void dibujarHospital(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz - 2.0f, celda * 0.88f, 0.24f, 6.0f, 0.50f, 0.50f, 0.52f, 0.0f);
        r.caja(px, 0.01f, pz + 2.5f, celda * 0.88f, 0.02f, 4.5f, 0.22f, 0.22f, 0.24f, 0.0f);

        for (float ox = -3.5f; ox <= 3.5f; ox += 2.2f) {
            r.caja(px + ox, 0.02f, pz + 2.5f, 0.12f, 0.02f, 3.8f, 0.95f, 0.95f, 0.95f, 0.0f);
        }

        r.caja(px, 4.5f, pz - 2.0f, 8.6f, 8.5f, 5.5f, 0.92f, 0.92f, 0.94f, 0.0f);
        r.caja(px, 8.85f, pz - 2.0f, 5.5f, 0.18f, 5.0f, 0.30f, 0.30f, 0.32f, 0.0f);
        r.caja(px, 8.96f, pz - 2.0f, 3.2f, 0.05f, 0.5f, 0.95f, 0.95f, 0.20f, 0.0f);

        // Cruz Roja
        r.caja(px, 6.2f, pz + 0.85f, 2.2f, 0.65f, 0.2f, 0.95f, 0.08f, 0.08f, 1.0f);
        r.caja(px, 6.2f, pz + 0.85f, 0.65f, 2.2f, 0.2f, 0.95f, 0.08f, 0.08f, 1.0f);

        r.caja(px, 7.8f, pz + 0.85f, 5.2f, 0.8f, 0.15f, 0.10f, 0.75f, 0.40f, 1.0f);
        r.caja(px, 2.2f, pz + 1.2f, 4.2f, 0.2f, 2.2f, 0.85f, 0.15f, 0.15f, 0.0f);
        dibujarVentanas(r, px, pz - 2.0f, 8.5f, 5.5f, modoNoche);
    }

    public static void dibujarPolicia(RenderizadorOpenGL r, float px, float pz, float tiempoGlobal, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz - 2.0f, celda * 0.88f, 0.24f, 6.0f, 0.45f, 0.45f, 0.48f, 0.0f);
        r.caja(px, 0.01f, pz + 2.5f, celda * 0.88f, 0.02f, 4.5f, 0.20f, 0.20f, 0.22f, 0.0f);

        for (float ox = -3.5f; ox <= 3.5f; ox += 2.2f) {
            r.caja(px + ox, 0.02f, pz + 2.5f, 0.12f, 0.02f, 3.8f, 0.95f, 0.85f, 0.10f, 0.0f);
        }

        r.caja(px, 4.0f, pz - 2.0f, 8.8f, 7.8f, 5.5f, 0.16f, 0.22f, 0.36f, 0.0f);
        r.caja(px, 6.2f, pz + 0.85f, 5.5f, 1.1f, 0.15f, 0.15f, 0.55f, 1.0f, 1.0f);

        float parpadeo = (float) Math.sin(tiempoGlobal * 10.0f);
        float em1 = parpadeo > 0 ? 1.0f : 0.2f;
        float em2 = parpadeo < 0 ? 1.0f : 0.2f;
        r.caja(px - 3.8f, 8.1f, pz + 0.6f, 0.6f, 0.5f, 0.6f, 1.0f, 0.1f, 0.1f, em1);
        r.caja(px + 3.8f, 8.1f, pz + 0.6f, 0.6f, 0.5f, 0.6f, 0.1f, 0.3f, 1.0f, em2);

        r.caja(px + 3.0f, 9.8f, pz - 3.0f, 0.18f, 3.8f, 0.18f, 0.80f, 0.80f, 0.85f, 0.0f);
        dibujarVentanas(r, px, pz - 2.0f, 7.8f, 5.5f, modoNoche);
    }

    public static void dibujarTaller(RenderizadorOpenGL r, float px, float pz) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz - 2.2f, celda * 0.88f, 0.24f, 5.5f, 0.45f, 0.45f, 0.45f, 0.0f);
        r.caja(px, 0.01f, pz + 2.0f, celda * 0.88f, 0.02f, 5.0f, 0.25f, 0.25f, 0.27f, 0.0f);

        r.caja(px, 3.2f, pz - 2.2f, 8.8f, 6.0f, 5.0f, 0.35f, 0.38f, 0.42f, 0.0f);
        r.caja(px - 2.2f, 1.8f, pz + 0.35f, 2.8f, 3.4f, 0.12f, 0.88f, 0.70f, 0.15f, 0.0f);
        r.caja(px + 2.2f, 1.8f, pz + 0.35f, 2.8f, 3.4f, 0.12f, 0.88f, 0.70f, 0.15f, 0.0f);

        r.caja(px, 5.6f, pz + 0.40f, 5.5f, 0.9f, 0.15f, 0.95f, 0.45f, 0.05f, 1.0f);
        r.caja(px - 3.8f, 0.35f, pz + 2.5f, 0.8f, 0.5f, 0.8f, 0.08f, 0.08f, 0.08f, 0.0f);
        r.caja(px - 3.8f, 0.75f, pz + 2.5f, 0.7f, 0.4f, 0.7f, 0.08f, 0.08f, 0.08f, 0.0f);
    }

    public static void dibujarGasolinera(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.015f, pz, celda * 0.88f, 0.03f, celda * 0.88f, 0.22f, 0.22f, 0.24f, 0.0f);

        r.caja(px - 2.5f, 0.025f, pz, 0.15f, 0.01f, 8.0f, 0.95f, 0.85f, 0.10f, 0.0f);
        r.caja(px + 2.5f, 0.025f, pz, 0.15f, 0.01f, 8.0f, 0.95f, 0.85f, 0.10f, 0.0f);

        r.caja(px, 5.5f, pz, 10.2f, 0.55f, 9.2f, 0.88f, 0.18f, 0.18f, 0.0f);
        r.caja(px, 5.5f, pz + 4.65f, 10.2f, 0.32f, 0.12f, 1.0f, 0.92f, 0.15f, 1.0f);
        r.caja(px, 5.5f, pz - 4.65f, 10.2f, 0.32f, 0.12f, 1.0f, 0.92f, 0.15f, 1.0f);
        r.caja(px, 6.0f, pz + 4.70f, 6.5f, 0.90f, 0.18f, 0.98f, 0.95f, 0.10f, 1.0f);

        float[][] colsGas = { {-4.8f, -3.8f}, {4.8f, -3.8f}, {-4.8f, 3.8f}, {4.8f, 3.8f} };
        for (float[] cg : colsGas) {
            r.caja(px + cg[0], 2.75f, pz + cg[1], 0.35f, 5.5f, 0.35f, 0.75f, 0.75f, 0.80f, 0.0f);
            r.caja(px + cg[0], 0.45f, pz + cg[1], 0.50f, 0.90f, 0.50f, 0.95f, 0.80f, 0.10f, 0.0f);
        }

        float emPlafon = modoNoche ? 1.0f : 0.0f;
        r.caja(px, 5.20f, pz, 4.5f, 0.05f, 7.5f, 1.0f, 1.0f, 0.95f, emPlafon);

        float[] islasX = {-3.6f, 3.6f};
        for (float ix : islasX) {
            r.caja(px + ix, 0.15f, pz, 0.75f, 0.25f, 4.5f, 0.42f, 0.42f, 0.44f, 0.0f);
            r.caja(px + ix, 0.95f, pz - 1.2f, 0.50f, 1.4f, 0.75f, 0.15f, 0.65f, 0.25f, 0.0f);
            r.caja(px + ix + (ix > 0 ? -0.27f : 0.27f), 1.15f, pz - 1.2f, 0.05f, 0.30f, 0.35f, 0.95f, 0.95f, 0.95f, 1.0f);

            r.caja(px + ix, 0.95f, pz + 1.2f, 0.50f, 1.4f, 0.75f, 0.85f, 0.15f, 0.15f, 0.0f);
            r.caja(px + ix + (ix > 0 ? -0.27f : 0.27f), 1.15f, pz + 1.2f, 0.05f, 0.30f, 0.35f, 0.95f, 0.95f, 0.95f, 1.0f);
        }
    }

    public static void dibujarMall(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.52f, 0.52f, 0.54f, 0.0f);
        r.caja(px, 4.2f, pz, 9.0f, 8.2f, 9.0f, 0.85f, 0.85f, 0.88f, 0.0f);

        float emVidrio = modoNoche ? 1.0f : 0.0f;
        r.caja(px, 4.0f, pz + 4.55f, 6.5f, 6.0f, 0.15f, 0.30f, 0.65f, 0.90f, emVidrio);
        r.caja(px, 7.5f, pz + 4.65f, 4.8f, 1.2f, 0.20f, 0.95f, 0.15f, 0.45f, 1.0f);

        r.caja(px - 3.5f, 2.5f, pz + 4.8f, 0.12f, 4.8f, 0.12f, 0.70f, 0.70f, 0.70f, 0.0f);
        r.caja(px - 3.5f, 4.2f, pz + 5.1f, 0.05f, 1.2f, 0.60f, 0.95f, 0.85f, 0.10f, 0.0f);
        r.caja(px + 3.5f, 2.5f, pz + 4.8f, 0.12f, 4.8f, 0.12f, 0.70f, 0.70f, 0.70f, 0.0f);
        r.caja(px + 3.5f, 4.2f, pz + 5.1f, 0.05f, 1.2f, 0.60f, 0.10f, 0.75f, 0.95f, 0.0f);

        dibujarVentanas(r, px, pz, 8.2f, 9.0f, modoNoche);
    }

    public static void dibujarComida(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.55f, 0.52f, 0.48f, 0.0f);
        r.caja(px, 3.2f, pz - 1.8f, 8.8f, 6.0f, 5.0f, 0.82f, 0.42f, 0.28f, 0.0f);

        r.caja(px - 2.2f, 5.5f, pz + 0.75f, 2.4f, 0.8f, 0.15f, 0.95f, 0.60f, 0.10f, 1.0f);
        r.caja(px + 2.2f, 5.5f, pz + 0.75f, 2.4f, 0.8f, 0.15f, 0.95f, 0.10f, 0.10f, 1.0f);

        r.caja(px - 2.2f, 2.5f, pz + 1.2f, 2.8f, 0.2f, 1.2f, 0.95f, 0.20f, 0.20f, 0.0f);
        r.caja(px + 2.2f, 2.5f, pz + 1.2f, 2.8f, 0.2f, 1.2f, 0.20f, 0.75f, 0.35f, 0.0f);

        r.caja(px, 0.13f, pz + 2.4f, 8.0f, 0.02f, 3.5f, 0.68f, 0.60f, 0.50f, 0.0f);
        float[] mesasX = {-2.5f, 0.0f, 2.5f};
        for (float mx : mesasX) {
            r.caja(px + mx, 0.45f, pz + 2.4f, 0.9f, 0.08f, 0.9f, 0.85f, 0.85f, 0.85f, 0.0f);
            r.caja(px + mx, 0.22f, pz + 2.4f, 0.1f, 0.44f, 0.1f, 0.20f, 0.20f, 0.20f, 0.0f);
            r.caja(px + mx, 1.3f, pz + 2.4f, 0.06f, 1.8f, 0.06f, 0.30f, 0.30f, 0.30f, 0.0f);
            r.caja(px + mx, 1.85f, pz + 2.4f, 1.6f, 0.25f, 1.6f, 0.95f, 0.80f, 0.15f, 0.0f);
        }

        dibujarVentanas(r, px, pz - 1.8f, 6.0f, 5.0f, modoNoche);
    }

    public static void dibujarColegio(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.48f, 0.48f, 0.48f, 0.0f);
        r.caja(px, 3.0f, pz - 1.8f, 8.6f, 5.6f, 4.8f, 0.72f, 0.35f, 0.25f, 0.0f);

        r.caja(px, 0.13f, pz + 2.2f, 7.0f, 0.02f, 4.0f, 0.25f, 0.55f, 0.35f, 0.0f);
        r.caja(px, 0.14f, pz + 2.2f, 6.6f, 0.02f, 0.15f, 0.95f, 0.95f, 0.95f, 0.0f);

        r.caja(px, 6.8f, pz - 1.8f, 2.0f, 2.2f, 2.0f, 0.85f, 0.85f, 0.80f, 0.0f);
        r.caja(px, 7.0f, pz + 0.6f - 1.8f, 0.8f, 0.8f, 0.1f, 1.0f, 1.0f, 1.0f, 1.0f);

        dibujarVentanas(r, px, pz - 1.8f, 5.6f, 4.8f, modoNoche);
    }

    public static void dibujarManzanaSubdividida(RenderizadorOpenGL r, float px, float pz, int f, int c, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.48f, 0.48f, 0.50f, 0.0f);
        r.caja(px, 0.05f, pz, celda * 0.90f, 0.10f, celda * 0.90f, 0.38f, 0.38f, 0.40f, 0.0f);

        int seed = (f * 7 + c * 13);
        int tipoDistribucion = seed % 2;

        if (tipoDistribucion == 0) {
            float[][] offset4 = { {-2.3f, -2.3f}, {2.3f, -2.3f}, {-2.3f, 2.3f}, {2.3f, 2.3f} };
            for (int k = 0; k < 4; k++) {
                float ex = px + offset4[k][0];
                float ez = pz + offset4[k][1];
                float anchoSub = 4.2f;
                float altoSub = 5.2f + ((seed + k * 11) % 8) * 1.4f;

                float cr = 0.38f + ((seed + k * 3) % 5) * 0.07f;
                float cg = 0.38f + ((seed + k * 7) % 5) * 0.07f;
                float cb = 0.48f + ((seed + k * 2) % 5) * 0.08f;

                r.caja(ex, altoSub / 2.0f + 0.24f, ez, anchoSub, altoSub, anchoSub, cr, cg, cb, 0.0f);
                r.caja(ex, altoSub + 0.30f, ez, anchoSub + 0.2f, 0.16f, anchoSub + 0.2f, cr * 0.85f, cg * 0.85f, cb * 0.85f, 0.0f);
                r.caja(ex - 0.5f, altoSub + 0.75f, ez - 0.5f, 1.4f, 0.8f, 1.4f, 0.35f, 0.35f, 0.38f, 0.0f);

                dibujarVentanas(r, ex, ez, altoSub, anchoSub, modoNoche);
                if (k == 0 || k == 3) {
                    dibujarTiendaComercial(r, ex, ez, anchoSub, offset4[k][1], modoNoche);
                }
            }
        } else {
            float[][] offset2 = { {0.0f, -2.4f}, {0.0f, 2.4f} };
            for (int k = 0; k < 2; k++) {
                float ex = px + offset2[k][0];
                float ez = pz + offset2[k][1];
                float anchoX = celda * 0.78f;
                float profZ = 4.3f;
                float altoSub = 6.2f + ((seed + k * 9) % 7) * 1.5f;

                float cr = 0.42f + ((seed + k * 5) % 4) * 0.08f;
                float cg = 0.40f + ((seed + k * 2) % 4) * 0.08f;
                float cb = 0.50f + ((seed + k * 6) % 4) * 0.07f;

                r.caja(ex, altoSub / 2.0f + 0.24f, ez, anchoX, altoSub, profZ, cr, cg, cb, 0.0f);
                r.caja(ex, altoSub + 0.30f, ez, anchoX + 0.2f, 0.16f, profZ + 0.2f, cr * 0.85f, cg * 0.85f, cb * 0.85f, 0.0f);
                r.caja(ex + 1.0f, altoSub + 0.75f, ez, 1.8f, 0.8f, 1.6f, 0.35f, 0.35f, 0.38f, 0.0f);

                dibujarVentanas(r, ex, ez, altoSub, profZ, modoNoche);
                dibujarTiendaComercial(r, ex, ez, profZ, offset2[k][1], modoNoche);
            }
        }
    }

    private static void dibujarTiendaComercial(RenderizadorOpenGL r, float ex, float ez, float anchoPared, float dirZ, boolean modoNoche) {
        float fz = (dirZ > 0) ? 1.0f : -1.0f;
        float posZCara = ez + fz * (anchoPared / 2.0f + 0.03f);

        float emisionVitrina = modoNoche ? 1.0f : 0.0f;
        float colVit = modoNoche ? 0.98f : 0.62f;
        r.caja(ex, 1.1f, posZCara, anchoPared * 0.72f, 1.4f, 0.06f, colVit, colVit * 0.92f, 0.55f, emisionVitrina);

        float colToldoR = ((int) Math.abs(ex * 3) % 2 == 0) ? 0.92f : 0.96f;
        float colToldoG = (colToldoR > 0.93f) ? 0.96f : 0.15f;
        float colToldoB = (colToldoR > 0.93f) ? 0.96f : 0.15f;
        r.caja(ex, 2.05f, posZCara + fz * 0.45f, anchoPared * 0.76f, 0.18f, 0.85f, colToldoR, colToldoG, colToldoB, 0.0f);
    }

    public static void dibujarParque(RenderizadorOpenGL r, float px, float pz, int variante) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.18f, 0.52f, 0.22f, 0.0f);
        r.caja(px, 0.05f, pz, celda * 0.90f, 0.10f, celda * 0.90f, 0.40f, 0.40f, 0.42f, 0.0f);

        if (variante == 0) {
            r.caja(px, 0.13f, pz, 2.2f, 0.02f, celda * 0.88f, 0.72f, 0.65f, 0.52f, 0.0f);
            r.caja(px, 0.13f, pz, celda * 0.88f, 0.02f, 2.2f, 0.72f, 0.65f, 0.52f, 0.0f);

            r.caja(px, 1.4f, pz, 0.5f, 2.6f, 0.5f, 0.36f, 0.20f, 0.08f, 0.0f);
            r.caja(px, 3.2f, pz, 2.8f, 1.4f, 2.8f, 0.12f, 0.58f, 0.16f, 0.0f);
            r.caja(px, 4.1f, pz, 1.8f, 1.0f, 1.8f, 0.16f, 0.64f, 0.20f, 0.0f);

            dibujarBanco(r, px + 2.2f, pz, (float) (-Math.PI / 2.0));
            dibujarBanco(r, px - 2.2f, pz, (float) (Math.PI / 2.0));
            dibujarBanco(r, px, pz + 2.2f, (float) Math.PI);
            dibujarBanco(r, px, pz - 2.2f, 0.0f);
        } else if (variante == 1) {
            r.caja(px, 0.13f, pz, 1.8f, 0.02f, celda * 0.88f, 0.75f, 0.68f, 0.55f, 0.0f);
            for (float sz : new float[]{-1.8f, 1.8f}) {
                r.caja(px - 2.0f, 1.0f, pz + sz, 0.35f, 1.8f, 0.35f, 0.35f, 0.18f, 0.08f, 0.0f);
                r.caja(px - 2.0f, 2.2f, pz + sz, 1.9f, 0.9f, 1.9f, 0.10f, 0.45f, 0.14f, 0.0f);
                r.caja(px - 2.0f, 2.9f, pz + sz, 1.3f, 0.8f, 1.3f, 0.12f, 0.52f, 0.16f, 0.0f);
                r.caja(px - 2.0f, 3.5f, pz + sz, 0.7f, 0.7f, 0.7f, 0.15f, 0.60f, 0.20f, 0.0f);
                dibujarBanco(r, px + 1.6f, pz + sz, (float) (-Math.PI / 2.0));
            }
        } else if (variante == 2) {
            r.caja(px, 0.30f, pz, 2.6f, 0.35f, 2.6f, 0.60f, 0.60f, 0.62f, 0.0f);
            r.caja(px, 0.40f, pz, 2.2f, 0.20f, 2.2f, 0.20f, 0.65f, 0.85f, 0.0f);
            dibujarBanco(r, px, pz - 2.5f, 0.0f);
            dibujarBanco(r, px, pz + 2.5f, (float) Math.PI);

            float[][] arbustos = { {-3.2f, -3.2f}, {3.2f, -3.2f}, {-3.2f, 3.2f}, {3.2f, 3.2f} };
            for (float[] arb : arbustos) {
                r.caja(px + arb[0], 0.55f, pz + arb[1], 1.1f, 0.9f, 1.1f, 0.14f, 0.58f, 0.18f, 0.0f);
            }
        } else {
            dibujarBanco(r, px - 2.2f, pz, (float) (Math.PI / 2.0));
            dibujarBanco(r, px + 2.2f, pz, (float) (-Math.PI / 2.0));
            dibujarBanco(r, px, pz + 2.4f, (float) Math.PI);

            for (float sx : new float[]{-1.8f, 1.8f}) {
                r.caja(px + sx, 1.1f, pz - 2.2f, 0.35f, 2.0f, 0.35f, 0.36f, 0.22f, 0.10f, 0.0f);
                r.caja(px + sx, 2.4f, pz - 2.2f, 1.9f, 1.3f, 1.9f, 0.12f, 0.55f, 0.16f, 0.0f);
            }
        }
    }

    private static void dibujarBanco(RenderizadorOpenGL r, float x, float z, float rot) {
        float fX = -(float) Math.sin(rot);
        float fZ =  (float) Math.cos(rot);
        float dX =  (float) Math.cos(rot);
        float dZ =  (float) Math.sin(rot);

        float[][] posPatas = { {-0.65f, 0.0f}, {0.65f, 0.0f} };
        for (float[] p : posPatas) {
            float px = x + dX * p[0];
            float pz = z + dZ * p[0];
            r.cajaGirada(px, 0.22f, pz, 0.08f, 0.44f, 0.45f, 0.15f, 0.15f, 0.18f, 0.0f, rot);
            r.cajaGirada(px - fX * 0.20f, 0.55f, pz - fZ * 0.20f, 0.08f, 0.40f, 0.08f, 0.15f, 0.15f, 0.18f, 0.0f, rot);
        }

        r.cajaGirada(x - fX * 0.08f, 0.44f, z - fZ * 0.08f, 1.50f, 0.06f, 0.20f, 0.58f, 0.32f, 0.14f, 0.0f, rot);
        r.cajaGirada(x + fX * 0.12f, 0.44f, z + fZ * 0.12f, 1.50f, 0.06f, 0.20f, 0.58f, 0.32f, 0.14f, 0.0f, rot);
        r.cajaGirada(x - fX * 0.22f, 0.68f, z - fZ * 0.22f, 1.50f, 0.24f, 0.06f, 0.58f, 0.32f, 0.14f, 0.0f, rot);
    }

    public static void dibujarInterseccionSemaforos(RenderizadorOpenGL r, float px, float pz, float tiempoGlobal) {
        float celda = ConstantesCiudad.CELDA;
        float margenAcera = (celda * 0.88f) / 2.0f - 0.45f;

        float s1X = px + margenAcera; float s1Z = pz - margenAcera;
        r.caja(s1X, 2.3f, s1Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        r.caja(s1X - 0.6f, 4.4f, s1Z, 1.2f, 0.14f, 0.12f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(r, s1X - 1.2f, 4.2f, s1Z, 0.0f, tiempoGlobal);

        float s2X = px - margenAcera; float s2Z = pz + margenAcera;
        r.caja(s2X, 2.3f, s2Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        r.caja(s2X + 0.6f, 4.4f, s2Z, 1.2f, 0.14f, 0.12f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(r, s2X + 1.2f, 4.2f, s2Z, (float) Math.PI, tiempoGlobal);

        float s3X = px - margenAcera; float s3Z = pz - margenAcera;
        r.caja(s3X, 2.3f, s3Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        r.caja(s3X, 4.4f, s3Z + 0.6f, 0.12f, 0.14f, 1.2f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(r, s3X, 4.2f, s3Z + 1.2f, (float) (Math.PI / 2.0), tiempoGlobal + 4.0f);

        float s4X = px + margenAcera; float s4Z = pz + margenAcera;
        r.caja(s4X, 2.3f, s4Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        r.caja(s4X, 4.4f, s4Z - 0.6f, 0.12f, 0.14f, 1.2f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(r, s4X, 4.2f, s4Z - 1.2f, (float) (-Math.PI / 2.0), tiempoGlobal + 4.0f);
    }

    private static void dibujarCabinaLuces(RenderizadorOpenGL r, float x, float y, float z, float rot, float tiempo) {
        r.cajaGirada(x, y, z, 0.35f, 1.15f, 0.35f, 0.10f, 0.10f, 0.10f, 0.0f, rot);

        float ciclo = tiempo % 8.0f;
        float vr = 0.08f, vg = 0.08f, vb = 0.08f, emV = 0.0f;
        float ar = 0.08f, ag = 0.08f, ab = 0.08f, emA = 0.0f;
        float rr = 0.08f, rg = 0.08f, rb = 0.08f, emR = 0.0f;

        if (ciclo < 3.2f) {
            vg = 1.0f; emV = 1.0f;
        } else if (ciclo < 4.4f) {
            ar = 1.0f; ag = 0.85f; emA = 1.0f;
        } else {
            rr = 1.0f; emR = 1.0f;
        }

        float dirCaraX = (float) Math.sin(rot);
        float dirCaraZ = -(float) Math.cos(rot);
        float distCara = 0.19f;
        float posX = x + dirCaraX * distCara;
        float posZ = z + dirCaraZ * distCara;

        r.cajaGirada(posX, y + 0.34f, posZ, 0.18f, 0.18f, 0.05f, rr, rg, rb, emR, rot);
        r.cajaGirada(posX, y,         posZ, 0.18f, 0.18f, 0.05f, ar, ag, ab, emA, rot);
        r.cajaGirada(posX, y - 0.34f, posZ, 0.18f, 0.18f, 0.05f, vr, vg, vb, emV, rot);
    }

    public static void dibujarVentanas(RenderizadorOpenGL r, float px, float pz, float h, float anchoE, boolean modoNoche) {
        float emision = modoNoche ? 1.0f : 0.0f;
        float cr = modoNoche ? 1.0f : 0.35f;
        float cg = modoNoche ? 0.88f : 0.48f;
        float cb = modoNoche ? 0.35f : 0.65f;

        for (float y = 2.4f; y < h - 0.8f; y += 1.8f) {
            r.caja(px, y, pz + anchoE / 2.0f + 0.02f, anchoE * 0.75f, 0.7f, 0.04f, cr, cg, cb, emision);
            r.caja(px, y, pz - anchoE / 2.0f - 0.02f, anchoE * 0.75f, 0.7f, 0.04f, cr, cg, cb, emision);
            r.caja(px + anchoE / 2.0f + 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.75f, cr, cg, cb, emision);
            r.caja(px - anchoE / 2.0f - 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.75f, cr, cg, cb, emision);
        }
    }
}