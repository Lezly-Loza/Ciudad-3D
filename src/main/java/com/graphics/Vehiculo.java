package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11; // <-- IMPORT OBLIGATORIO CORREGIDO

public class Vehiculo extends AppCiudad {
    protected float autoX = 12.0f;
    protected float autoZ = 12.0f;
    protected float angulo = 0.0f;
    protected float velocidad = 0.0f;
    protected float radioAuto = 1.05f;

    // Chata articulada mejorada
    protected float distEnganche = 3.2f;
    protected float radioChata = 1.15f;
    protected float anguloChata = 0.0f;
    protected float chataX = 12.0f;
    protected float chataZ = 12.0f + 1.55f + 3.2f;

    protected boolean camaraAerea = false;
    protected boolean farosEncendidos = false;

    // SISTEMA DE DAÑO / INTEGRIDAD Y GAME OVER POLICIAL
    protected float saludAuto = 100.0f; // 100% de integridad
    protected boolean gameOver = false;
    protected String guiaGps = "ADELANTE";

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

        saludAuto = 100.0f;
        gameOver = false;
        entregasRealizadas = 0;
        destinoX = ConstantesCiudad.centro(6);
        destinoZ = ConstantesCiudad.centro(2);
    }

    @Override
    protected void actualizar(float dt) {
        super.actualizar(dt);

        if (trafico != null) {
            for (TraficoManager.AutoTrafico auto : trafico) {
                auto.actualizar(dt, autoX, autoZ, chataX, chataZ, gameOver);
            }
        }

        // Si la patrulla te detuvo tras perder toda la integridad
        if (gameOver) {
            velocidad = 0.0f;
            actualizarTitulo();
            return;
        }

        // 1. Aceleración y Freno
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

        // 3. Simulación cinemática del tractor
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

        // 5. Validación de Colisión y Daño
        boolean autoLibre = ColisionesManager.puedeCircular(sigAutoX, sigAutoZ, radioAuto, postesSemaforos, trafico);
        boolean chataLibre = ColisionesManager.puedeCircular(sigChataX, sigChataZ, radioChata, postesSemaforos, trafico);

        if (autoLibre && chataLibre) {
            autoX = sigAutoX;
            autoZ = sigAutoZ;
            chataX = sigChataX;
            chataZ = sigChataZ;
            anguloChata = sigAnguloChata;
        } else {
            // Impacto físico: resta salud según la fuerza del choque
            if (Math.abs(velocidad) > 1.0f) {
                saludAuto -= Math.abs(velocidad) * 1.2f;
                if (saludAuto <= 0.0f) {
                    saludAuto = 0.0f;
                    gameOver = true; // Activa persecución policial
                }
            }
            velocidad = 0.0f;
        }

        // 6. Cálculo de Guía de Navegación GPS hacia el Destino
        calcularGuiaGps();

        // 7. Checkpoints
        float dDestX = autoX - destinoX;
        float dDestZ = autoZ - destinoZ;
        if ((dDestX * dDestX + dDestZ * dDestZ) < (3.5f * 3.5f)) {
            entregasRealizadas++;
            // Reparación parcial por entrega exitosa
            saludAuto = Math.min(100.0f, saludAuto + 25.0f);
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

    private void calcularGuiaGps() {
        float dx = destinoX - autoX;
        float dz = destinoZ - autoZ;
        float dist = (float) Math.sqrt(dx * dx + dz * dz);

        // Vector frontal y lateral del auto
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float dX =  (float) Math.cos(angulo);
        float dZ = -(float) Math.sin(angulo);

        float dotFront = (dx * fX + dz * fZ) / dist;
        float dotLateral = (dx * dX + dz * dZ) / dist;

        if (dotFront > 0.65f) {
            guiaGps = "ADELANTE (" + Math.round(dist) + "m)";
        } else if (dotFront < -0.65f) {
            guiaGps = "GIRAR / ATRAS (" + Math.round(dist) + "m)";
        } else if (dotLateral > 0.0f) {
            guiaGps = "DERECHA ▶ (" + Math.round(dist) + "m)";
        } else {
            guiaGps = "◀ IZQUIERDA (" + Math.round(dist) + "m)";
        }
    }

    private boolean pulsada(int k) {
        return GLFW.glfwGetKey(ventana, k) == GLFW.GLFW_PRESS;
    }

    private void actualizarTitulo() {
        if (gameOver) {
            GLFW.glfwSetWindowTitle(ventana, "¡¡ VEHICULO DESTRUIDO !! DETENIDO POR LA POLICIA | Presiona [R] para reiniciar");
            return;
        }

        int kmh = Math.round(Math.abs(velocidad) * 3.6f);
        String luces = farosEncendidos ? "ON" : "OFF";
        String tiempo = modoNoche ? "NOCHE" : "DIA";
        String titulo = String.format("GPS: %s | Salud: %d%% | %d km/h | Entregas: %d | Faros [F]: %s | Modo [N]: %s | [R] Reiniciar",
                guiaGps, (int)saludAuto, kmh, entregasRealizadas, luces, tiempo);
        GLFW.glfwSetWindowTitle(ventana, titulo);
    }

 @Override
    protected void escena() {
        super.escena();
        dibujarAutoConChataDetallado();
        dibujarAnimacionArresto();
        dibujarHUDIntegridad();
    }

    // ANIMACIÓN: EL CONDUCTOR BAJA CON LAS MANOS ARRIBA Y EL POLICÍA LO APUNTA/AGARRA
    private void dibujarAnimacionArresto() {
        if (!gameOver) return;

        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float dX =  (float) Math.cos(angulo);
        float dZ = -(float) Math.sin(angulo);

        // Posición del conductor fuera del auto, junto a la puerta
        float condX = autoX - dX * 1.6f;
        float condZ = autoZ - dZ * 1.6f;
        // Conductor con camisa roja y manos arriba
        PeatonesManager.dibujarHumanoide(renderizador, condX, condZ, angulo + (float)(Math.PI / 2.0),
                                         0.85f, 0.15f, 0.15f, tiempoGlobal, true);

        // Posición del policía frente al conductor
        float polX = condX - dX * 1.2f;
        float polZ = condZ - dZ * 1.2f;
        // Oficial con uniforme azul marino
        PeatonesManager.dibujarHumanoide(renderizador, polX, polZ, angulo - (float)(Math.PI / 2.0),
                                         0.12f, 0.15f, 0.35f, tiempoGlobal, false);
    }

    // HUD: BARRA DE SALUD Y CARTEL "ARRESTADO" EN PIXEL-ART
    private void dibujarHUDIntegridad() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // 1. Barra de salud (esquina inferior izquierda)
        renderizador.dibujarRect2D(-0.65f, -0.88f, 0.55f, 0.06f, 0.12f, 0.12f, 0.14f);
        float pct = Math.max(0.0f, Math.min(saludAuto / 100.0f, 1.0f));
        float r = (pct < 0.5f) ? 1.0f : (1.0f - pct) * 2.0f;
        float g = (pct > 0.5f) ? 1.0f : pct * 2.0f;
        if (pct > 0.0f) {
            renderizador.dibujarRect2D(-0.65f - (0.52f * (1.0f - pct) * 0.5f), -0.88f, 0.52f * pct, 0.04f, r, g, 0.10f);
        }

        // 2. AVISO EN PANTALLA: CARTEL "ARRESTADO"
        if (gameOver) {
            // Marco exterior oscuro
            renderizador.dibujarRect2D(0.0f, 0.72f, 1.35f, 0.22f, 0.06f, 0.06f, 0.08f);

            // Franja roja central
            renderizador.dibujarRect2D(0.0f, 0.72f, 1.30f, 0.16f, 0.85f, 0.12f, 0.12f);

            // Balizas intermitentes en las esquinas del cartel
            float pulso = (float) Math.sin(tiempoGlobal * 10.0f);
            float emRojo = pulso > 0 ? 1.0f : 0.25f;
            float emAzul = pulso < 0 ? 1.0f : 0.25f;
            renderizador.dibujarRect2D(-0.58f, 0.72f, 0.06f, 0.12f, 1.0f, 0.1f, 0.1f);
            renderizador.dibujarRect2D( 0.58f, 0.72f, 0.06f, 0.12f, 0.1f, 0.3f, 1.0f);

            // TEXTO PIXEL ART REAL: "ARRESTADO"
            renderizador.dibujarTextoPixel("ARRESTADO", -0.46f, 0.76f, 0.022f, 1.0f, 1.0f, 1.0f);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void dibujarAutoConChataDetallado() {
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        float dX =  (float) Math.cos(angulo);
        float dZ = -(float) Math.sin(angulo);

        // ==============================================================
        // 1. AUTO TRACTOR
        // ==============================================================
        renderizador.cajaGirada(autoX, 0.45f, autoZ, 1.85f, 0.48f, 3.1f, 0.88f, 0.15f, 0.15f, 0.0f, angulo);
        renderizador.cajaGirada(autoX, 0.25f, autoZ, 1.88f, 0.12f, 3.15f, 0.15f, 0.15f, 0.15f, 0.0f, angulo);

        renderizador.cajaGirada(autoX - fX * 0.25f, 0.90f, autoZ - fZ * 0.25f, 1.45f, 0.48f, 1.75f, 0.18f, 0.28f, 0.55f, 0.0f, angulo);
        renderizador.cajaGirada(autoX - fX * 0.25f, 1.15f, autoZ - fZ * 0.25f, 1.40f, 0.06f, 1.65f, 0.88f, 0.15f, 0.15f, 0.0f, angulo);

        float lateral = 0.90f;
        float longitudinal = 0.98f;
        float[][] posRuedas = {
            {-lateral,  longitudinal}, { lateral,  longitudinal},
            {-lateral, -longitudinal}, { lateral, -longitudinal}
        };

        for (float[] p : posRuedas) {
            float rx = autoX + dX * p[0] + fX * p[1];
            float rz = autoZ + dZ * p[0] + fZ * p[1];
            renderizador.cajaGirada(rx, 0.26f, rz, 0.24f, 0.52f, 0.65f, 0.12f, 0.12f, 0.12f, 0.0f, angulo);
            renderizador.cajaGirada(rx, 0.26f, rz, 0.26f, 0.26f, 0.26f, 0.82f, 0.82f, 0.85f, 0.0f, angulo);
        }

        float emisionFaros = farosEncendidos ? 1.0f : 0.0f;
        float colLuz = farosEncendidos ? 1.0f : 0.4f;
        renderizador.cajaGirada(autoX + fX * 1.55f - dX * 0.55f, 0.50f, autoZ + fZ * 1.55f - dZ * 0.55f, 0.22f, 0.16f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);
        renderizador.cajaGirada(autoX + fX * 1.55f + dX * 0.55f, 0.50f, autoZ + fZ * 1.55f + dZ * 0.55f, 0.22f, 0.16f, 0.08f, colLuz, colLuz, colLuz * 0.7f, emisionFaros, angulo);

        renderizador.cajaGirada(autoX - fX * 1.55f - dX * 0.55f, 0.50f, autoZ - fZ * 1.55f - dZ * 0.55f, 0.20f, 0.16f, 0.08f, 0.95f, 0.08f, 0.08f, 0.8f, angulo);
        renderizador.cajaGirada(autoX - fX * 1.55f + dX * 0.55f, 0.50f, autoZ - fZ * 1.55f + dZ * 0.55f, 0.20f, 0.16f, 0.08f, 0.95f, 0.08f, 0.08f, 0.8f, angulo);

        // ==============================================================
        // 2. ENGANCHE DE REMOLQUE ARTICULADO
        // ==============================================================
        float hitchX = autoX - fX * 1.55f;
        float hitchZ = autoZ - fZ * 1.55f;
        renderizador.caja(hitchX, 0.35f, hitchZ, 0.18f, 0.18f, 0.18f, 0.15f, 0.15f, 0.15f, 0.0f);

        float midX = (hitchX + chataX) * 0.5f;
        float midZ = (hitchZ + chataZ) * 0.5f;
        float distReal = (float) Math.sqrt((chataX - hitchX) * (chataX - hitchX) + (chataZ - hitchZ) * (chataZ - hitchZ));
        renderizador.cajaGirada(midX, 0.35f, midZ, 0.12f, 0.10f, distReal, 0.25f, 0.25f, 0.28f, 0.0f, anguloChata);

        // ==============================================================
        // 3. CHATA CON CARGA Y REFLECTORES
        // ==============================================================
        float cfX = -(float) Math.sin(anguloChata);
        float cfZ = -(float) Math.cos(anguloChata);
        float cdX =  (float) Math.cos(anguloChata);
        float cdZ = -(float) Math.sin(anguloChata);

        renderizador.cajaGirada(chataX, 0.35f, chataZ, 1.9f, 0.12f, 2.6f, 0.35f, 0.35f, 0.38f, 0.0f, anguloChata);

        renderizador.cajaGirada(chataX - cdX * 0.90f, 0.62f, chataZ - cdZ * 0.90f, 0.10f, 0.45f, 2.6f, 0.85f, 0.20f, 0.18f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX + cdX * 0.90f, 0.62f, chataZ + cdZ * 0.90f, 0.10f, 0.45f, 2.6f, 0.85f, 0.20f, 0.18f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX + cfX * 1.25f, 0.62f, chataZ + cfZ * 1.25f, 1.9f, 0.45f, 0.10f, 0.85f, 0.20f, 0.18f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX - cfX * 1.25f, 0.62f, chataZ - cfZ * 1.25f, 1.9f, 0.45f, 0.10f, 0.85f, 0.20f, 0.18f, 0.0f, anguloChata);

        float[][] ruedasChata = { {-0.96f, 0.0f}, {0.96f, 0.0f} };
        for (float[] rc : ruedasChata) {
            float rx = chataX + cdX * rc[0];
            float rz = chataZ + cdZ * rc[0];
            renderizador.cajaGirada(rx, 0.26f, rz, 0.24f, 0.52f, 0.65f, 0.12f, 0.12f, 0.12f, 0.0f, anguloChata);
            renderizador.cajaGirada(rx, 0.26f, rz, 0.26f, 0.26f, 0.26f, 0.82f, 0.82f, 0.85f, 0.0f, anguloChata);
        }

        renderizador.cajaGirada(chataX, 0.70f, chataZ - cfX * 0.30f, 0.95f, 0.60f, 0.95f, 0.88f, 0.72f, 0.45f, 0.0f, anguloChata);
        renderizador.cajaGirada(chataX, 0.65f, chataZ + cfX * 0.40f, 0.85f, 0.50f, 0.85f, 0.78f, 0.62f, 0.38f, 0.0f, anguloChata);

        renderizador.cajaGirada(chataX - cfX * 1.31f - cdX * 0.70f, 0.42f, chataZ - cfZ * 1.31f - cdZ * 0.70f, 0.16f, 0.14f, 0.04f, 0.98f, 0.10f, 0.10f, 0.9f, anguloChata);
        renderizador.cajaGirada(chataX - cfX * 1.31f + cdX * 0.70f, 0.42f, chataZ - cfZ * 1.31f + cdZ * 0.70f, 0.16f, 0.14f, 0.04f, 0.98f, 0.10f, 0.10f, 0.9f, anguloChata);
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
            renderizador.vector("uFaro1Pos", autoX + fX * 1.55f - dX * 0.65f, 0.55f, autoZ + fZ * 1.55f - dZ * 0.65f);
            renderizador.vector("uFaro2Pos", autoX + fX * 1.55f + dX * 0.65f, 0.55f, autoZ + fZ * 1.55f + dZ * 0.65f);
        } else {
            renderizador.decimal("uFarosActivos", 0.0f);
        }
    }

    public static void main(String[] args) {
        new Vehiculo().run();
    }
}