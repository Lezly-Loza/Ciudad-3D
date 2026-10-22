package com.graphics;

public class TraficoManager {

    public static class AutoTrafico {
        public float x, z, angulo, velBase, velActual;
        public float r, g, b;
        public int tipo; // 0: Civil, 1: Policía, 2: Taxi, 3: Autobús, 4: Pickup
        public float[][] ruta;
        public int targetIdx = 0;
        public boolean enPersecucion = false;

        public AutoTrafico(float[][] ruta, float r, float g, float b, float vel, int tipo) {
            this.ruta = ruta;
            this.x = ruta[0][0];
            this.z = ruta[0][1];
            this.r = r; this.g = g; this.b = b;
            this.velBase = vel;
            this.velActual = vel;
            this.angulo = 0.0f;
            this.tipo = tipo;
        }

        public void actualizar(float dt, float jugadorX, float jugadorZ, float chataX, float chataZ, boolean gameOver) {
            // MODO PERSECUCIÓN POLICIAL
            if (tipo == 1 && gameOver) {
                enPersecucion = true;
                float dx = jugadorX - x;
                float dz = jugadorZ - z;
                float dist = (float) Math.sqrt(dx * dx + dz * dz);

                // Si se colocó junto al jugador, se detiene
                if (dist < 3.4f) {
                    velActual = 0.0f;
                    return;
                }

                float anguloDeseado = (float) Math.atan2(-dx, -dz);
                float fX = -(float) Math.sin(anguloDeseado);
                float fZ = -(float) Math.cos(anguloDeseado);

                float paso = 10.5f * dt;
                float sigX = x + fX * paso;
                float sigZ = z + fZ * paso;

                // NUNCA ATRAVIESA EDIFICIOS: verifica colisión
                if (ColisionesManager.puedeCircular(sigX, sigZ, 1.15f, null, null)) {
                    x = sigX;
                    z = sigZ;
                    angulo = anguloDeseado;
                } else {
                    // Intenta desplazarse por el carril vehicular en X o en Z
                    if (ColisionesManager.puedeCircular(x + fX * paso, z, 1.15f, null, null)) {
                        x += fX * paso;
                    } else if (ColisionesManager.puedeCircular(x, z + fZ * paso, 1.15f, null, null)) {
                        z += fZ * paso;
                    }
                }
                return;
            }

            // PATRULLAJE NORMAL EN RED VIAL
            float djX = jugadorX - x;
            float djZ = jugadorZ - z;
            float distJugador = (float) Math.sqrt(djX * djX + djZ * djZ);
            float dcX = chataX - x;
            float dcZ = chataZ - z;
            float distChata = (float) Math.sqrt(dcX * dcX + dcZ * dcZ);
            float distMin = Math.min(distJugador, distChata);

            if (distMin < 5.5f) {
                velActual = Math.max(0.0f, velActual - 40.0f * dt);
            } else {
                velActual = Math.min(velBase, velActual + 15.0f * dt);
            }

            float tx = ruta[targetIdx][0];
            float tz = ruta[targetIdx][1];
            float dx = tx - x;
            float dz = tz - z;
            float dist = (float) Math.sqrt(dx * dx + dz * dz);

            if (dist < 1.2f) {
                targetIdx = (targetIdx + 1) % ruta.length;
            } else {
                angulo = (float) Math.atan2(-dx, -dz);
                float fX = -(float) Math.sin(angulo);
                float fZ = -(float) Math.cos(angulo);

                float sigX = x + fX * velActual * dt;
                float sigZ = z + fZ * velActual * dt;

                if (ColisionesManager.puedeCircular(sigX, sigZ, 1.15f, null, null)) {
                    x = sigX;
                    z = sigZ;
                } else {
                    targetIdx = (targetIdx + 1) % ruta.length;
                }
            }
        }
    }

    public static AutoTrafico[] crearTrafico() {
        float c0 = ConstantesCiudad.centro(0);
        float c2 = ConstantesCiudad.centro(2);
        float c4 = ConstantesCiudad.centro(4);
        float c6 = ConstantesCiudad.centro(6);
        float c8 = ConstantesCiudad.centro(8);
        float c10 = ConstantesCiudad.centro(10);

        float[][] r1 = { {c2, c2}, {c8, c2}, {c8, c8}, {c2, c8} };
        float[][] r2 = { {c4, c4}, {c4, c6}, {c6, c6}, {c6, c4} };
        float[][] r3 = { {c0, c0}, {c10, c0}, {c10, c10}, {c0, c10} };
        float[][] r4 = { {c2, c6}, {c8, c6}, {c8, c2}, {c2, c2} }; // Ruta para Autobús
        float[][] r5 = { {c0, c4}, {c10, c4}, {c10, c8}, {c0, c8} }; // Ruta para Pickup

        return new AutoTrafico[] {
            new AutoTrafico(r1, 0.12f, 0.12f, 0.15f, 10.0f, 1), // Policía
            new AutoTrafico(r2, 0.98f, 0.82f, 0.10f, 9.0f,  2), // Taxi
            new AutoTrafico(r3, 0.20f, 0.65f, 0.95f, 9.5f,  0), // Civil Sedan
            new AutoTrafico(r4, 0.98f, 0.68f, 0.12f, 7.5f,  3), // Autobús Escolar
            new AutoTrafico(r5, 0.85f, 0.22f, 0.18f, 10.0f, 4)  // Camioneta Pickup
        };
    }

    public static void dibujar(RenderizadorOpenGL r, AutoTrafico[] trafico, float tiempoGlobal, boolean modoNoche) {
        if (trafico == null) return;
        for (AutoTrafico auto : trafico) {
            switch (auto.tipo) {
                case 1 -> dibujarPatrullaDetallada(r, auto, tiempoGlobal);
                case 2 -> dibujarTaxiDetallado(r, auto, modoNoche);
                case 3 -> dibujarAutobusEscolar(r, auto);
                case 4 -> dibujarPickup(r, auto);
                default -> dibujarAutoCivil(r, auto);
            }
        }
    }

    // 1. AUTOBÚS ESCOLAR AMARILLO
    private static void dibujarAutobusEscolar(RenderizadorOpenGL r, AutoTrafico auto) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        // Carrocería alargada
        r.cajaGirada(auto.x, 0.90f, auto.z, 2.1f, 1.40f, 5.2f, 0.96f, 0.72f, 0.10f, 0.0f, auto.angulo);
        // Franja negra perimetral
        r.cajaGirada(auto.x, 0.65f, auto.z, 2.12f, 0.14f, 5.0f, 0.15f, 0.15f, 0.15f, 0.0f, auto.angulo);
        // Ventanillas laterales continuas
        r.cajaGirada(auto.x, 1.15f, auto.z, 2.14f, 0.45f, 4.4f, 0.18f, 0.24f, 0.32f, 0.0f, auto.angulo);

        // Ruedas dobles
        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 1.05f, 1.8f);
        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 1.05f, -1.8f);
    }

    // 2. CAMIONETA PICKUP ROJA ESTILIZADA
    private static void dibujarPickup(RenderizadorOpenGL r, AutoTrafico auto) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        // Chasis inferior
        r.cajaGirada(auto.x, 0.45f, auto.z, 1.85f, 0.50f, 3.4f, 0.88f, 0.20f, 0.18f, 0.0f, auto.angulo);
        // Cabina delantera
        r.cajaGirada(auto.x + fX * 0.45f, 0.92f, auto.z + fZ * 0.45f, 1.45f, 0.48f, 1.6f, 0.18f, 0.25f, 0.35f, 0.0f, auto.angulo);
        // Tolva trasera descubierta
        r.cajaGirada(auto.x - fX * 0.95f, 0.62f, auto.z - fZ * 0.95f, 1.70f, 0.30f, 1.3f, 0.25f, 0.25f, 0.28f, 0.0f, auto.angulo);

        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 0.95f, 1.1f);
    }

    private static void dibujarTaxiDetallado(RenderizadorOpenGL r, AutoTrafico auto, boolean modoNoche) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        r.cajaGirada(auto.x, 0.45f, auto.z, 1.8f, 0.48f, 3.2f, 0.98f, 0.82f, 0.10f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x, 0.48f, auto.z, 1.82f, 0.10f, 2.8f, 0.15f, 0.15f, 0.15f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.20f, 0.88f, auto.z - fZ * 0.20f, 1.4f, 0.45f, 1.7f, 0.18f, 0.22f, 0.28f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.20f, 1.12f, auto.z - fZ * 0.20f, 1.35f, 0.06f, 1.6f, 0.98f, 0.82f, 0.10f, 0.0f, auto.angulo);

        float emTaxi = modoNoche ? 1.0f : 0.0f;
        r.cajaGirada(auto.x - fX * 0.20f, 1.25f, auto.z - fZ * 0.20f, 0.75f, 0.18f, 0.35f, 1.0f, 0.95f, 0.35f, emTaxi, auto.angulo);

        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 0.92f, 1.05f);
    }

    private static void dibujarPatrullaDetallada(RenderizadorOpenGL r, AutoTrafico auto, float tiempoGlobal) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        r.cajaGirada(auto.x, 0.45f, auto.z, 1.8f, 0.48f, 3.2f, 0.12f, 0.12f, 0.15f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x, 0.46f, auto.z, 1.82f, 0.46f, 1.3f, 0.96f, 0.96f, 0.98f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.20f, 0.88f, auto.z - fZ * 0.20f, 1.4f, 0.45f, 1.7f, 0.15f, 0.18f, 0.24f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.20f, 1.12f, auto.z - fZ * 0.20f, 1.35f, 0.06f, 1.6f, 0.96f, 0.96f, 0.98f, 0.0f, auto.angulo);

        float parpadeo = (float) Math.sin(tiempoGlobal * (auto.enPersecucion ? 24.0f : 12.0f));
        float emRojo = parpadeo > 0 ? 1.0f : 0.15f;
        float emAzul = parpadeo < 0 ? 1.0f : 0.15f;

        r.cajaGirada(auto.x - fX * 0.20f - dX * 0.35f, 1.22f, auto.z - fZ * 0.20f - dZ * 0.35f, 0.35f, 0.12f, 0.22f, 1.0f, 0.08f, 0.08f, emRojo, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.20f + dX * 0.35f, 1.22f, auto.z - fZ * 0.20f + dZ * 0.35f, 0.35f, 0.12f, 0.22f, 0.08f, 0.40f, 1.0f, emAzul, auto.angulo);

        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 0.92f, 1.05f);
    }

    private static void dibujarAutoCivil(RenderizadorOpenGL r, AutoTrafico auto) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        r.cajaGirada(auto.x, 0.45f, auto.z, 1.75f, 0.48f, 3.1f, auto.r, auto.g, auto.b, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.15f, 0.88f, auto.z - fZ * 0.15f, 1.35f, 0.45f, 1.65f, 0.18f, 0.22f, 0.28f, 0.0f, auto.angulo);
        dibujarRuedas(r, auto.x, auto.z, auto.angulo, dX, dZ, fX, fZ, 0.90f, 1.0f);
    }

    private static void dibujarRuedas(RenderizadorOpenGL r, float ax, float az, float rot,
                                      float dX, float dZ, float fX, float fZ, float lat, float lon) {
        float[][] pos = { {-lat, lon}, {lat, lon}, {-lat, -lon}, {lat, -lon} };
        for (float[] p : pos) {
            float rx = ax + dX * p[0] + fX * p[1];
            float rz = az + dZ * p[0] + fZ * p[1];
            r.cajaGirada(rx, 0.26f, rz, 0.22f, 0.52f, 0.62f, 0.12f, 0.12f, 0.12f, 0.0f, rot);
            r.cajaGirada(rx, 0.26f, rz, 0.24f, 0.26f, 0.26f, 0.75f, 0.75f, 0.78f, 0.0f, rot);
        }
    }
}