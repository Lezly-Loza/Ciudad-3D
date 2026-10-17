package com.graphics;

public class TraficoManager {

    public static class AutoTrafico {
        public float x, z, angulo, velBase, velActual;
        public float r, g, b;
        public int tipo; // 0: Civil, 1: Policía, 2: Taxi
        public float[][] ruta;
        public int targetIdx = 0;

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

        public void actualizar(float dt, float jugadorX, float jugadorZ, float chataX, float chataZ) {
            float djX = jugadorX - x;
            float djZ = jugadorZ - z;
            float distJugador = (float) Math.sqrt(djX * djX + djZ * djZ);

            float dcX = chataX - x;
            float dcZ = chataZ - z;
            float distChata = (float) Math.sqrt(dcX * dcX + dcZ * dcZ);

            float distMin = Math.min(distJugador, distChata);

            if (distMin < 5.8f) {
                velActual = Math.max(0.0f, velActual - 45.0f * dt);
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
                x += fX * velActual * dt;
                z += fZ * velActual * dt;
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

        return new AutoTrafico[] {
            new AutoTrafico(r1, 0.10f, 0.10f, 0.12f, 10.5f, 1),
            new AutoTrafico(r2, 0.98f, 0.85f, 0.05f, 9.0f,  2),
            new AutoTrafico(r3, 0.20f, 0.70f, 0.95f, 9.5f,  0)
        };
    }

    public static void dibujar(RenderizadorOpenGL r, AutoTrafico[] trafico, float tiempoGlobal, boolean modoNoche) {
        if (trafico == null) return;
        for (AutoTrafico auto : trafico) {
            if (auto.tipo == 1) {
                dibujarPatrullaPolicia(r, auto, tiempoGlobal);
            } else if (auto.tipo == 2) {
                dibujarTaxi(r, auto, modoNoche);
            } else {
                r.cajaGirada(auto.x, 0.45f, auto.z, 1.6f, 0.5f, 2.8f, auto.r, auto.g, auto.b, 0.0f, auto.angulo);
                r.cajaGirada(auto.x, 0.85f, auto.z, 1.2f, 0.45f, 1.5f, 0.15f, 0.2f, 0.35f, 0.0f, auto.angulo);
            }
        }
    }

    private static void dibujarTaxi(RenderizadorOpenGL r, AutoTrafico auto, boolean modoNoche) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);

        r.cajaGirada(auto.x, 0.45f, auto.z, 1.7f, 0.5f, 3.0f, 0.98f, 0.85f, 0.08f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x, 0.48f, auto.z, 1.72f, 0.12f, 2.6f, 0.15f, 0.15f, 0.15f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.2f, 0.88f, auto.z - fZ * 0.2f, 1.3f, 0.45f, 1.6f, 0.18f, 0.25f, 0.35f, 0.0f, auto.angulo);

        float emTaxi = modoNoche ? 1.0f : 0.0f;
        r.cajaGirada(auto.x - fX * 0.2f, 1.20f, auto.z - fZ * 0.2f, 0.75f, 0.18f, 0.30f, 1.0f, 0.95f, 0.30f, emTaxi, auto.angulo);
    }

    private static void dibujarPatrullaPolicia(RenderizadorOpenGL r, AutoTrafico auto, float tiempoGlobal) {
        float fX = -(float) Math.sin(auto.angulo);
        float fZ = -(float) Math.cos(auto.angulo);
        float dX =  (float) Math.cos(auto.angulo);
        float dZ = -(float) Math.sin(auto.angulo);

        r.cajaGirada(auto.x, 0.45f, auto.z, 1.7f, 0.5f, 3.0f, 0.10f, 0.10f, 0.12f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x, 0.46f, auto.z, 1.72f, 0.48f, 1.2f, 0.95f, 0.95f, 0.95f, 0.0f, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.2f, 0.88f, auto.z - fZ * 0.2f, 1.3f, 0.45f, 1.6f, 0.15f, 0.20f, 0.30f, 0.0f, auto.angulo);

        float parpadeo = (float) Math.sin(tiempoGlobal * 14.0f);
        float emRojo = parpadeo > 0 ? 1.0f : 0.15f;
        float emAzul = parpadeo < 0 ? 1.0f : 0.15f;

        r.cajaGirada(auto.x - fX * 0.2f - dX * 0.35f, 1.15f, auto.z - fZ * 0.2f - dZ * 0.35f, 0.35f, 0.12f, 0.22f, 1.0f, 0.05f, 0.05f, emRojo, auto.angulo);
        r.cajaGirada(auto.x - fX * 0.2f + dX * 0.35f, 1.15f, auto.z - fZ * 0.2f + dZ * 0.35f, 0.35f, 0.12f, 0.22f, 0.05f, 0.30f, 1.0f, emAzul, auto.angulo);
    }
}