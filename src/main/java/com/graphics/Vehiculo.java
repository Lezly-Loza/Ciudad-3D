package com.graphics;

import org.lwjgl.glfw.GLFW;

public class Vehiculo extends AppCiudad {
    protected float autoX = 12.0f;
    protected float autoZ = 12.0f;
    protected float angulo = 0.0f;
    protected float velocidad = 0.0f;
    protected float radioAuto = 1.05f;

    // Chata articulada
    protected float distEnganche = 3.2f;
    protected float radioChata = 1.15f;
    protected float anguloChata = 0.0f;
    protected float chataX = 12.0f;
    protected float chataZ = 12.0f + 1.55f + 3.2f;

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

        anguloChata = 0.0f;
        chataX = autoX;
        chataZ = autoZ + 1.55f + distEnganche;

        entregasRealizadas = 0;
        destinoX = ConstantesCiudad.centro(6);
        destinoZ = ConstantesCiudad.centro(2);
    }

    @Override
    protected void actualizar(float dt) {
        super.actualizar(dt);

        if (trafico != null) {
            for (TraficoManager.AutoTrafico auto : trafico) {
                auto.actualizar(dt, autoX, autoZ, chataX, chataZ);
            }
        }

        // 1. Aceleración
        float acel = 0.0f;
        if (pulsada(GLFW.GLFW_KEY_W) || pulsada(GLFW.GLFW_KEY_UP)) acel += 20.0f;
        if (pulsada(GLFW.GLFW_KEY_S) || pulsada(GLFW.GLFW_KEY_DOWN)) acel -= 16.0f;

        velocidad += acel * dt;
        float resistencia = pulsada(GLFW.GLFW_KEY_SPACE) ? 8.5f : 1.35f;
        velocidad *= (float) Math.exp(-resistencia * dt);
        velocidad = Math.max(-10.0f, Math.min(velocidad, 25.0f));

        // 2. Giro y alineación
        float dir = 0.0f;
        if (pulsada(GLFW.GLFW_KEY_A) || pulsada(GLFW.GLFW_KEY_LEFT)) dir += 1.0f;
        if (pulsada(GLFW.GLFW_KEY_D) || pulsada(GLFW.GLFW_KEY_RIGHT)) dir -= 1.0f;

        if (Math.abs(dir) > 0.01f && Math.abs(velocidad) > 0.15f) {
            float sentido = velocidad >= 0 ? 1.0f : -1.0f;
            angulo += dir * 2.0f * sentido * dt;
        } else if (Math.abs(velocidad) > 0.4f) {
            float dosPi = (float) (Math.PI * 2.0);
            float norm = ((angulo % dosPi) + dosPi) % dosPi;
            float target = (float) (Math.round(norm / (Math.PI * 0.5)) * (Math.PI * 0.5));
            float delta = target - norm;
            if (Math.abs(delta) < 0.28f) angulo += delta * 5.0f * dt;
        }

        // 3. Cinemática del tractor
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float sigAutoX = autoX + fX * velocidad * dt;
        float sigAutoZ = autoZ + fZ * velocidad * dt;

        float sigHitchX = sigAutoX - fX * 1.55f;
        float sigHitchZ = sigAutoZ - fZ * 1.55f;

        // 4. Cinemática de la chata
        float sigAnguloChata = anguloChata;
        if (Math.abs(velocidad) > 0.01f) {
            float diff = angulo - anguloChata;
            while (diff > Math.PI) diff -= (float) (Math.PI * 2.0);
            while (diff < -Math.PI) diff += (float) (Math.PI * 2.0);

            float factorSentido = velocidad >= 0 ? 1.0f : -1.0f;
            sigAnguloChata += (velocidad / distEnganche) * (float) Math.sin(diff) * dt * factorSentido;

            float diffFinal = sigAnguloChata - angulo;
            while (diffFinal > Math.PI) diffFinal -= (float) (Math.PI * 2.0);
            while (diffFinal < -Math.PI) diffFinal += (float) (Math.PI * 2.0);

            float maxTijera = 0.95f;
            if (diffFinal > maxTijera) sigAnguloChata = angulo + maxTijera;
            if (diffFinal < -maxTijera) sigAnguloChata = angulo - maxTijera;
        }

        float sigChataX = sigHitchX + (float) Math.sin(sigAnguloChata) * distEnganche;
        float sigChataZ = sigHitchZ + (float) Math.cos(sigAnguloChata) * distEnganche;

        // 5. Validación mediante ColisionesManager
        boolean autoLibre = ColisionesManager.puedeCircular(sigAutoX, sigAutoZ, radioAuto, postesSemaforos, trafico);
        boolean chataLibre = ColisionesManager.puedeCircular(sigChataX, sigChataZ, radioChata, postesSemaforos, trafico);

        if (autoLibre && chataLibre) {
            autoX = sigAutoX;
            autoZ = sigAutoZ;
            chataX = sigChataX;
            chataZ = sigChataZ;
            anguloChata = sigAnguloChata;
        } else {
            velocidad = 0.0f;
        }

        // Checkpoints
        float dDestX = autoX - destinoX;
        float dDestZ = autoZ - destinoZ;
        if ((dDestX * dDestX + dDestZ * dDestZ) < (3.5f * 3.5f)) {
            entregasRealizadas++;
            if (entregasRealizadas % 3 == 1) {
                destinoX = ConstantesCiudad.centro(2); destinoZ = ConstantesCiudad.centro(8);
            } else if (entregasRealizadas % 3 == 2) {
                destinoX = ConstantesCiudad.centro(8); destinoZ = ConstantesCiudad.centro(8);
            } else {
                destinoX = ConstantesCiudad.centro(6); destinoZ = ConstantesCiudad.centro(2);
            }
        }

        actualizarTitulo();
    }

    private boolean pulsada(int k) {
        return GLFW.glfwGetKey(ventana, k) == GLFW.GLFW_PRESS;
    }

    private void actualizarTitulo() {
        int kmh = Math.round(Math.abs(velocidad) * 3.6f);
        String luces = farosEncendidos ? "ON" : "OFF";
        String tiempo = modoNoche ? "NOCHE" : "DÍA";
        String titulo = String.format("Ciudad 3D | %d km/h | Entregas: %d | Faros [F]: %s | Modo [N]: %s | [WASD/Flechas] Conducir [Space] Freno [C] Cámara [M] Mapa [R] Reiniciar",
                kmh, entregasRealizadas, luces, tiempo);
        GLFW.glfwSetWindowTitle(ventana, titulo);
    }

    @Override
    protected void configurarCamara() {
        if (camaraAerea) {
            super.configurarCamara();
            return;
        }

        float distAtras = 11.5f;
        float alturaCam = 5.2f;

        float camX = autoX + (float) Math.sin(angulo) * distAtras;
        float camZ = autoZ + (float) Math.cos(angulo) * distAtras;

        float radioObstaculo = (ConstantesCiudad.CELDA * 0.88f) / 2.0f;
        int[][] mapa = ConstantesCiudad.MAPA;

        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (mapa[f][c] != 0) {
                    float cx = ConstantesCiudad.centro(c);
                    float cz = ConstantesCiudad.centro(f);
                    if (Math.abs(camX - cx) < radioObstaculo + 0.6f && Math.abs(camZ - cz) < radioObstaculo + 0.6f) {
                        distAtras = 4.2f;
                        alturaCam = 6.2f;
                        camX = autoX + (float) Math.sin(angulo) * distAtras;
                        camZ = autoZ + (float) Math.cos(angulo) * distAtras;
                        break;
                    }
                }
            }
        }

        renderizador.vector("uOjo", camX, alturaCam, camZ);
        renderizador.vector("uObjetivo", autoX, 1.2f, autoZ);
        renderizador.decimal("uAspecto", (float) ancho / (float) alto);
    }

    @Override
    protected void configurarLuces() {
        super.configurarLuces();

        if (farosEncendidos) {
            renderizador.decimal("uFarosActivos", 1.0f);
            float fX = -(float) Math.sin(angulo);
            float fZ = -(float) Math.cos(angulo);
            float dX =  (float) Math.cos(angulo);
            float dZ = -(float) Math.sin(angulo);

            renderizador.vector("uFaroDir", fX, -0.15f, fZ);
            renderizador.vector("uFaro1Pos", autoX + fX * 1.5f - dX * 0.65f, 0.55f, autoZ + fZ * 1.5f - dZ * 0.65f);
            renderizador.vector("uFaro2Pos", autoX + fX * 1.5f + dX * 0.65f, 0.55f, autoZ + fZ * 1.5f + dZ * 0.65f);
        } else {
            renderizador.decimal("uFarosActivos", 0.0f);
        }
    }

    @Override
    protected void escena() {
        super.escena();
        dibujarAutoConChata();
    }

    private void dibujarAutoConChata() {
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float dX =  (float) Math.cos(angulo);
        float dZ = -(float) Math.sin(angulo);

        // 1. Tractor
        renderizador.cajaGirada(autoX, 0.45f, autoZ, 1.8f, 0.50f, 3.0f, 0.88f, 0.15f, 0.15f, 0.0f, angulo);
        renderizador.cajaGirada(autoX - fX * 0.25f, 0.88f, autoZ - fZ * 0.25f, 1.35f, 0.48f, 1.70f, 0.18f, 0.28f, 0.58f, 0.0f, angulo);

        float lateral = 0.88f;
        float longitudinal = 0.95f;
        float[][] posRuedas = {
            {-lateral,  longitudinal}, { lateral,  longitudinal},
            {-lateral, -longitudinal}, { lateral, -longitudinal}
        };

        for (float[] p : posRuedas) {
            float rx = autoX + dX * p[0] + fX * p[1];
            float rz = autoZ + dZ * p[0] + fZ * p[1];
            renderizador.cajaGirada(rx, 0.26f, rz, 0.24f, 0.52f, 0.65f, 0.08f, 0.08f, 0.08f, 0.0f, angulo);
        }

        float emisionFaros = farosEncendidos ? 1.0f : 0.0f;
        float colLuz = farosEncendidos ? 1.0f : 0.4f;
        renderizador.cajaGirada(autoX + fX * 1.52f - dX * 0.55f, 0.50f, autoZ + fZ * 1.52f - dZ * 0.55f, 0.20f, 0.16f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);
        renderizador.cajaGirada(autoX + fX * 1.52f + dX * 0.55f, 0.50f, autoZ + fZ * 1.52f + dZ * 0.55f, 0.20f, 0.16f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);

        renderizador.cajaGirada(autoX - fX * 1.52f - dX * 0.55f, 0.50f, autoZ - fZ * 1.52f - dZ * 0.55f, 0.20f, 0.16f, 0.08f, 0.9f, 0.08f, 0.08f, 0.8f, angulo);
        renderizador.cajaGirada(autoX - fX * 1.52f + dX * 0.55f, 0.50f, autoZ - fZ * 1.52f + dZ * 0.55f, 0.20f, 0.16f, 0.08f, 0.9f, 0.08f, 0.08f, 0.8f, angulo);

        // 2. Enganche
        float hitchX = autoX - fX * 1.55f;
        float hitchZ = autoZ - fZ * 1.55f;
        renderizador.caja(hitchX, 0.35f, hitchZ, 0.16f, 0.16f, 0.16f, 0.12f, 0.12f, 0.12f, 0.0f);

        float midX = (hitchX + chataX) * 0.5f;
        float midZ = (hitchZ + chataZ) * 0.5f;
        float distReal = (float) Math.sqrt((chataX - hitchX) * (chataX - hitchX) + (chataZ - hitchZ) * (chataZ - hitchZ));
        renderizador.cajaGirada(midX, 0.35f, midZ, 0.12f, 0.10f, distReal, 0.20f, 0.20f, 0.22f, 0.0f, anguloChata);

        // 3. Chata
        float cfX = -(float) Math.sin(anguloChata);
        float cfZ = -(float) Math.cos(anguloChata);
        float cdX =  (float) Math.cos(anguloChata);
        float cdZ = -(float) Math.sin(anguloChata);

        renderizador.cajaGirada(chataX, 0.38f, chataZ, 1.8f, 0.14f, 2.4f, 0.30f, 0.30f, 0.34f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX - cdX * 0.85f, 0.60f, chataZ - cdZ * 0.85f, 0.08f, 0.40f, 2.4f, 0.70f, 0.15f, 0.15f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX + cdX * 0.85f, 0.60f, chataZ + cdZ * 0.85f, 0.08f, 0.40f, 2.4f, 0.70f, 0.15f, 0.15f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX + cfX * 1.15f, 0.60f, chataZ + cfZ * 1.15f, 1.8f, 0.40f, 0.08f, 0.70f, 0.15f, 0.15f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX - cfX * 1.15f, 0.60f, chataZ - cfZ * 1.15f, 1.8f, 0.40f, 0.08f, 0.70f, 0.15f, 0.15f, 0.0f, anguloChata);

        float[][] ruedasChata = { {-0.92f, 0.0f}, {0.92f, 0.0f} };
        for (float[] rc : ruedasChata) {
            float rx = chataX + cdX * rc[0];
            float rz = chataZ + cdZ * rc[0];
            renderizador.cajaGirada(rx, 0.26f, rz, 0.24f, 0.52f, 0.65f, 0.08f, 0.08f, 0.08f, 0.0f, anguloChata);
        }

        renderizador.cajaGirada(chataX, 0.68f, chataZ - cfX * 0.25f, 0.85f, 0.55f, 0.85f, 0.75f, 0.52f, 0.28f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX, 0.64f, chataZ + cfX * 0.35f, 0.75f, 0.48f, 0.75f, 0.65f, 0.45f, 0.22f, 0.0f, anguloChata);

        renderizador.cajaGirada(chataX - cfX * 1.21f - cdX * 0.65f, 0.42f, chataZ - cfZ * 1.21f - cdZ * 0.65f, 0.16f, 0.14f, 0.04f, 0.95f, 0.10f, 0.10f, 0.9f, anguloChata);
        renderizador.cajaGirada(chataX - cfX * 1.21f + cdX * 0.65f, 0.42f, chataZ - cfZ * 1.21f + cdZ * 0.65f, 0.16f, 0.14f, 0.04f, 0.95f, 0.10f, 0.10f, 0.9f, anguloChata);
    }

    public static void main(String[] args) {
        new Vehiculo().run();
    }
}