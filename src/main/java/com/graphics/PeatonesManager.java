package com.graphics;

public class PeatonesManager {
    public static class Peaton {
        float x, z, dirX, dirZ, angulo;
        float r, g, b; // Color de camisa
        float minVal, maxVal;
        boolean ejeX;

        public Peaton(float x, float z, boolean ejeX, float minVal, float maxVal, float r, float g, float b) {
            this.x = x;
            this.z = z;
            this.ejeX = ejeX;
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.dirX = ejeX ? 1.0f : 0.0f;
            this.dirZ = !ejeX ? 1.0f : 0.0f;
            this.angulo = ejeX ? (float)(Math.PI / 2.0) : 0.0f;
            this.r = r; this.g = g; this.b = b;
        }

        public void actualizar(float dt) {
            float vel = 1.8f;
            if (ejeX) {
                x += dirX * vel * dt;
                if (x > maxVal) { dirX = -1.0f; angulo = (float)(-Math.PI / 2.0); }
                if (x < minVal) { dirX = 1.0f; angulo = (float)(Math.PI / 2.0); }
            } else {
                z += dirZ * vel * dt;
                if (z > maxVal) { dirZ = -1.0f; angulo = (float)Math.PI; }
                if (z < minVal) { dirZ = 1.0f; angulo = 0.0f; }
            }
        }
    }

    public static Peaton[] crearPeatones() {
        return new Peaton[] {
            new Peaton(ConstantesCiudad.centro(1) + 4.5f, ConstantesCiudad.centro(1), false, ConstantesCiudad.centro(1) - 4.0f, ConstantesCiudad.centro(1) + 4.0f, 0.20f, 0.65f, 0.85f),
            new Peaton(ConstantesCiudad.centro(3), ConstantesCiudad.centro(3) + 4.5f, true, ConstantesCiudad.centro(3) - 4.0f, ConstantesCiudad.centro(3) + 4.0f, 0.95f, 0.35f, 0.25f),
            new Peaton(ConstantesCiudad.centro(5) - 4.5f, ConstantesCiudad.centro(5), false, ConstantesCiudad.centro(5) - 4.0f, ConstantesCiudad.centro(5) + 4.0f, 0.30f, 0.80f, 0.30f),
            new Peaton(ConstantesCiudad.centro(7), ConstantesCiudad.centro(7) - 4.5f, true, ConstantesCiudad.centro(7) - 4.0f, ConstantesCiudad.centro(7) + 4.0f, 0.95f, 0.85f, 0.15f)
        };
    }

    public static void dibujarPeatones(RenderizadorOpenGL r, Peaton[] peatones, float tiempoGlobal) {
        if (peatones == null) return;
        for (Peaton p : peatones) {
            dibujarHumanoide(r, p.x, p.z, p.angulo, p.r, p.g, p.b, tiempoGlobal, false);
        }
    }

    // Modelo 3D de persona articulada estilo diorama
    public static void dibujarHumanoide(RenderizadorOpenGL r, float x, float z, float rot,
                                         float cr, float cg, float cb, float tiempo, boolean manosArriba) {
        float fX = -(float) Math.sin(rot);
        float fZ = -(float) Math.cos(rot);
        float dX =  (float) Math.cos(rot);
        float dZ = -(float) Math.sin(rot);

        float oscilacion = (float) Math.sin(tiempo * 8.0f) * 0.22f;

        // Piernas (Pantalón azul marino)
        if (!manosArriba) {
            r.cajaGirada(x - dX * 0.12f + fX * oscilacion, 0.35f, z - dZ * 0.12f + fZ * oscilacion, 0.14f, 0.70f, 0.14f, 0.18f, 0.22f, 0.35f, 0.0f, rot);
            r.cajaGirada(x + dX * 0.12f - fX * oscilacion, 0.35f, z + dZ * 0.12f - fZ * oscilacion, 0.14f, 0.70f, 0.14f, 0.18f, 0.22f, 0.35f, 0.0f, rot);
        } else {
            r.cajaGirada(x - dX * 0.12f, 0.35f, z - dZ * 0.12f, 0.14f, 0.70f, 0.14f, 0.18f, 0.22f, 0.35f, 0.0f, rot);
            r.cajaGirada(x + dX * 0.12f, 0.35f, z + dZ * 0.12f, 0.14f, 0.70f, 0.14f, 0.18f, 0.22f, 0.35f, 0.0f, rot);
        }

        // Torso / Camisa
        r.cajaGirada(x, 0.95f, z, 0.42f, 0.55f, 0.26f, cr, cg, cb, 0.0f, rot);

        // Brazos
        if (manosArriba) {
            // Brazos alzados en señal de rendición
            r.cajaGirada(x - dX * 0.28f, 1.35f, z - dZ * 0.28f, 0.11f, 0.45f, 0.11f, cr, cg, cb, 0.0f, rot);
            r.cajaGirada(x + dX * 0.28f, 1.35f, z + dZ * 0.28f, 0.11f, 0.45f, 0.11f, cr, cg, cb, 0.0f, rot);
        } else {
            r.cajaGirada(x - dX * 0.28f - fX * oscilacion, 0.90f, z - dZ * 0.28f - fZ * oscilacion, 0.11f, 0.45f, 0.11f, cr, cg, cb, 0.0f, rot);
            r.cajaGirada(x + dX * 0.28f + fX * oscilacion, 0.90f, z + dZ * 0.28f + fZ * oscilacion, 0.11f, 0.45f, 0.11f, cr, cg, cb, 0.0f, rot);
        }

        // Cabeza (Tono piel)
        r.cajaGirada(x, 1.40f, z, 0.28f, 0.30f, 0.28f, 0.95f, 0.80f, 0.65f, 0.0f, rot);
        // Cabello / Gorra
        r.cajaGirada(x, 1.54f, z, 0.30f, 0.10f, 0.30f, 0.30f, 0.20f, 0.12f, 0.0f, rot);
    }
}