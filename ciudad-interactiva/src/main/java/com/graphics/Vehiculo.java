package com.graphics;

import org.lwjgl.glfw.GLFW;

public class Vehiculo extends AppCiudad {
    // Inicia en el centro de la calle 6 (X=12.0f, Z=12.0f), perfectamente orientado hacia adelante
    protected float autoX = 12.0f;
    protected float autoZ = 12.0f;
    protected float angulo = 0.0f;
    protected float velocidad = 0.0f;
    protected float radioAuto = 1.10f;

    protected boolean camaraAerea = false;
    protected boolean farosEncendidos = false;

    @Override
    protected void tecla(int key) {
        super.tecla(key);
        if (key == GLFW.GLFW_KEY_C) {
            camaraAerea = !camaraAerea;
        } else if (key == GLFW.GLFW_KEY_F) {
            farosEncendidos = !farosEncendidos;
        } else if (key == GLFW.GLFW_KEY_R) {
            reiniciar();
        }
    }

    private void reiniciar() {
        autoX = 12.0f;
        autoZ = 12.0f;
        angulo = 0.0f;
        velocidad = 0.0f;
        entregasRealizadas = 0;
        destinoX = centro(6);
        destinoZ = centro(2);
    }

    @Override
    protected void actualizar(float dt) {
        super.actualizar(dt);

        if (trafico != null) {
            for (AutoTrafico auto : trafico) {
                auto.actualizar(dt, autoX, autoZ);
            }
        }

        // 1. Aceleración longitudinal limpia
        float acel = 0.0f;
        if (pulsada(GLFW.GLFW_KEY_W) || pulsada(GLFW.GLFW_KEY_UP)) acel += 20.0f;
        if (pulsada(GLFW.GLFW_KEY_S) || pulsada(GLFW.GLFW_KEY_DOWN)) acel -= 16.0f;

        velocidad += acel * dt;

        float resistencia = pulsada(GLFW.GLFW_KEY_SPACE) ? 7.5f : 1.2f;
        velocidad *= (float) Math.exp(-resistencia * dt);
        velocidad = Math.max(-10.0f, Math.min(velocidad, 26.0f));

        // 2. Control de giro: solo se modifica el ángulo si el usuario presiona expresamente A/D
        float dir = 0.0f;
        if (pulsada(GLFW.GLFW_KEY_A) || pulsada(GLFW.GLFW_KEY_LEFT)) dir += 1.0f;
        if (pulsada(GLFW.GLFW_KEY_D) || pulsada(GLFW.GLFW_KEY_RIGHT)) dir -= 1.0f;

        if (Math.abs(velocidad) > 0.15f && Math.abs(dir) > 0.01f) {
            float sentido = velocidad >= 0 ? 1.0f : -1.0f;
            angulo += dir * 2.2f * sentido * dt;
        }

        // 3. Desplazamiento
        float frenteX = -(float) Math.sin(angulo);
        float frenteZ = -(float) Math.cos(angulo);

        float sigX = autoX + frenteX * velocidad * dt;
        float sigZ = autoZ + frenteZ * velocidad * dt;

        if (puedeCircular(sigX, sigZ)) {
            autoX = sigX;
            autoZ = sigZ;
        } else {
            velocidad = 0.0f;
        }

        // Checkpoint de entrega
        float dDestX = autoX - destinoX;
        float dDestZ = autoZ - destinoZ;
        if ((dDestX * dDestX + dDestZ * dDestZ) < (3.5f * 3.5f)) {
            entregasRealizadas++;
            if (entregasRealizadas % 3 == 1) {
                destinoX = centro(2); destinoZ = centro(8);
            } else if (entregasRealizadas % 3 == 2) {
                destinoX = centro(8); destinoZ = centro(8);
            } else {
                destinoX = centro(6); destinoZ = centro(2);
            }
        }

        actualizarTitulo();
    }

    private boolean puedeCircular(float nx, float nz) {
        if (Math.abs(nx) > (limite - radioAuto) || Math.abs(nz) > (limite - radioAuto)) {
            return false;
        }

        float radioObstaculo = (celda * 0.88f) / 2.0f;
        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (mapa[f][c] != 0) {
                    float cx = centro(c);
                    float cz = centro(f);

                    float cercaX = Math.max(cx - radioObstaculo, Math.min(nx, cx + radioObstaculo));
                    float cercaZ = Math.max(cz - radioObstaculo, Math.min(nz, cz + radioObstaculo));

                    float dx = nx - cercaX;
                    float dz = nz - cercaZ;

                    if ((dx * dx + dz * dz) < (radioAuto * radioAuto)) {
                        return false;
                    }
                }
            }
        }

        if (trafico != null) {
            for (AutoTrafico auto : trafico) {
                float dx = nx - auto.x;
                float dz = nz - auto.z;
                if ((dx * dx + dz * dz) < (radioAuto * 2.2f) * (radioAuto * 2.2f)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean pulsada(int k) {
        return GLFW.glfwGetKey(ventana, k) == GLFW.GLFW_PRESS;
    }

    private void actualizarTitulo() {
        int kmh = Math.round(Math.abs(velocidad) * 3.6f);
        String luces = farosEncendidos ? "ON" : "OFF";
        String tiempo = modoNoche ? "NOCHE" : "DÍA";
        String titulo = String.format("Ciudad 3D | %d km/h | Entregas: %d | Faros [F]: %s | Ciclo [N]: %s | [WASD] Conducir [Space] Freno [C] Cámara [M] Mapa [R] Reiniciar",
                kmh, entregasRealizadas, luces, tiempo);
        GLFW.glfwSetWindowTitle(ventana, titulo);
    }

    @Override
    protected void configurarCamara() {
        if (camaraAerea) {
            super.configurarCamara();
            return;
        }

        float distAtras = 8.5f;
        float alturaCam = 4.2f;

        float camX = autoX + (float) Math.sin(angulo) * distAtras;
        float camZ = autoZ + (float) Math.cos(angulo) * distAtras;

        float radioObstaculo = (celda * 0.88f) / 2.0f;
        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (mapa[f][c] != 0) {
                    float cx = centro(c);
                    float cz = centro(f);
                    if (Math.abs(camX - cx) < radioObstaculo + 0.6f && Math.abs(camZ - cz) < radioObstaculo + 0.6f) {
                        distAtras = 3.5f;
                        alturaCam = 5.8f;
                        camX = autoX + (float) Math.sin(angulo) * distAtras;
                        camZ = autoZ + (float) Math.cos(angulo) * distAtras;
                        break;
                    }
                }
            }
        }

        vector("uOjo", camX, alturaCam, camZ);
        vector("uObjetivo", autoX, 1.2f, autoZ);
        decimal("uAspecto", (float) ancho / (float) alto);
    }

    @Override
    protected void configurarLuces() {
        super.configurarLuces();

        if (farosEncendidos) {
            decimal("uFarosActivos", 1.0f);
            float frenteX = -(float) Math.sin(angulo);
            float frenteZ = -(float) Math.cos(angulo);
            float derX = (float) Math.cos(angulo);
            float derZ = -(float) Math.sin(angulo);

            vector("uFaroDir", frenteX, -0.15f, frenteZ);
            vector("uFaro1Pos", autoX + frenteX * 1.5f - derX * 0.65f, 0.55f, autoZ + frenteZ * 1.5f - derZ * 0.65f);
            vector("uFaro2Pos", autoX + frenteX * 1.5f + derX * 0.65f, 0.55f, autoZ + frenteZ * 1.5f + derZ * 0.65f);
        } else {
            decimal("uFarosActivos", 0.0f);
        }
    }

    @Override
    protected void escena() {
        super.escena();
        dibujarAuto();
    }

    private void dibujarAuto() {
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float dX = (float) Math.cos(angulo);
        float dZ = -(float) Math.sin(angulo);

        // Chasis principal
        cajaGirada(autoX, 0.50f, autoZ, 1.8f, 0.55f, 3.2f, 0.88f, 0.15f, 0.15f, 0.0f, angulo);
        // Cabina
        cajaGirada(autoX - fX * 0.3f, 0.95f, autoZ - fZ * 0.3f, 1.35f, 0.50f, 1.8f, 0.18f, 0.28f, 0.58f, 0.0f, angulo);

        // 4 Ruedas
        float[][] posRuedas = {
            {-0.95f, 0.9f}, {0.95f, 0.9f},
            {-0.95f, -0.9f}, {0.95f, -0.9f}
        };
        for (float[] pr : posRuedas) {
            float rx = autoX + dX * pr[0] + fX * pr[1];
            float rz = autoZ + dZ * pr[0] + fZ * pr[1];
            cajaGirada(rx, 0.30f, rz, 0.32f, 0.58f, 0.75f, 0.08f, 0.08f, 0.08f, 0.0f, angulo);
        }

        // Focos visuales delanteros
        float emisionFaros = farosEncendidos ? 1.0f : 0.0f;
        float colLuz = farosEncendidos ? 1.0f : 0.4f;
        cajaGirada(autoX + fX * 1.6f - dX * 0.6f, 0.55f, autoZ + fZ * 1.6f - dZ * 0.6f, 0.22f, 0.18f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);
        cajaGirada(autoX + fX * 1.6f + dX * 0.6f, 0.55f, autoZ + fZ * 1.6f - dZ * 0.6f, 0.22f, 0.18f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);

        // Focos traseros
        cajaGirada(autoX - fX * 1.6f - dX * 0.6f, 0.55f, autoZ - fZ * 1.6f - dZ * 0.6f, 0.22f, 0.18f, 0.08f, 0.9f, 0.08f, 0.08f, 0.8f, angulo);
        cajaGirada(autoX - fX * 1.6f + dX * 0.6f, 0.55f, autoZ - fZ * 1.6f - dZ * 0.6f, 0.22f, 0.18f, 0.08f, 0.9f, 0.08f, 0.08f, 0.8f, angulo);
    }

    public static void main(String[] args) {
        new Vehiculo().run();
    }
}