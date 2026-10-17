package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

public class AppCiudad {
    protected long ventana;
    protected RenderizadorOpenGL renderizador = new RenderizadorOpenGL();
    protected int ancho = 1024;
    protected int alto = 768;

    protected float orbita = 0.0f;
    protected boolean modoNoche = false;
    protected boolean verMinimapa = true;
    protected float tiempoGlobal = 0.0f;

    protected float destinoX = 0.0f;
    protected float destinoZ = 24.0f;
    protected int entregasRealizadas = 0;

    public List<float[]> postesSemaforos = new ArrayList<>();
    protected TraficoManager.AutoTrafico[] trafico;

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();
        try {
            iniciar();
            loop();
        } finally {
            limpiar();
        }
    }

    protected void iniciar() {
        if (!GLFW.glfwInit()) throw new IllegalStateException("No se pudo iniciar GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        ventana = GLFW.glfwCreateWindow(ancho, alto, "Ciudad Interactiva 3D - LWJGL", 0, 0);
        if (ventana == 0) throw new RuntimeException("Error al crear ventana GLFW");

        GLFW.glfwMakeContextCurrent(ventana);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(ventana);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GLFW.glfwSetKeyCallback(ventana, (w, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) tecla(key);
        });

        trafico = TraficoManager.crearTrafico();
        recolectarPostesSemaforos();
        destinoX = ConstantesCiudad.centro(6);
        destinoZ = ConstantesCiudad.centro(2);

        renderizador.inicializar();
    }

    protected void recolectarPostesSemaforos() {
        postesSemaforos.clear();
        float margenAcera = (ConstantesCiudad.CELDA * 0.88f) / 2.0f - 0.45f;
        int[][] mapa = ConstantesCiudad.MAPA;

        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (mapa[f][c] == 0 && f % 2 == 0 && c % 2 == 0 && (f + c) % 4 == 0) {
                    float px = ConstantesCiudad.centro(c);
                    float pz = ConstantesCiudad.centro(f);
                    postesSemaforos.add(new float[]{px + margenAcera, pz - margenAcera});
                    postesSemaforos.add(new float[]{px - margenAcera, pz + margenAcera});
                    postesSemaforos.add(new float[]{px - margenAcera, pz - margenAcera});
                    postesSemaforos.add(new float[]{px + margenAcera, pz + margenAcera});
                }
            }
        }
    }

    protected void tecla(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE) GLFW.glfwSetWindowShouldClose(ventana, true);
        else if (key == GLFW.GLFW_KEY_N) modoNoche = !modoNoche;
        else if (key == GLFW.GLFW_KEY_M) verMinimapa = !verMinimapa;
    }

    protected void actualizar(float dt) {
        tiempoGlobal += dt;
        if (GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) orbita -= dt * 1.2f;
        if (GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) orbita += dt * 1.2f;
    }

    private void loop() {
        double tAnt = GLFW.glfwGetTime();
        int[] w = new int[1];
        int[] h = new int[1];

        while (!GLFW.glfwWindowShouldClose(ventana)) {
            GLFW.glfwPollEvents();
            double tAct = GLFW.glfwGetTime();
            float dt = (float) Math.min(tAct - tAnt, 0.05);
            tAnt = tAct;

            actualizar(dt);

            GLFW.glfwGetFramebufferSize(ventana, w, h);
            ancho = w[0];
            alto = h[0];

            if (ancho > 0 && alto > 0) {
                dibujarFrame();
                GLFW.glfwSwapBuffers(ventana);
            }
        }
    }

    private void dibujarFrame() {
        if (modoNoche) {
            GL11.glClearColor(0.010f, 0.010f, 0.025f, 1.0f);
        } else {
            GL11.glClearColor(0.55f, 0.72f, 0.92f, 1.0f);
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        renderizador.usarPrograma();

        GL11.glViewport(0, 0, ancho, alto);
        renderizador.decimal("uModoMapa", 0.0f);
        renderizador.decimal("uEsNoche", modoNoche ? 1.0f : 0.0f);
        configurarCamara();
        configurarLuces();
        escena();

        if (verMinimapa) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            int tamMini = Math.min(ancho, alto) / 4;
            int posX = ancho - tamMini - 20;
            int posY = alto - tamMini - 20;

            GL11.glViewport(posX, posY, tamMini, tamMini);
            renderizador.decimal("uModoMapa", 1.0f);
            escena();
        }
    }

    protected void configurarCamara() {
        float camX = (float) Math.sin(orbita) * 95.0f;
        float camZ = (float) Math.cos(orbita) * 95.0f;
        renderizador.vector("uOjo", camX, 60.0f, camZ);
        renderizador.vector("uObjetivo", 0.0f, 0.0f, 0.0f);
        renderizador.decimal("uAspecto", (float) ancho / (float) alto);
    }

    protected void configurarLuces() {
        if (modoNoche) {
            renderizador.vector("uLuzDir", 0.2f, 0.95f, 0.2f);
            renderizador.vector("uLuzDirColor", 0.10f, 0.12f, 0.20f);
            renderizador.vector("uLuzAmbiente", 0.20f, 0.22f, 0.30f);
        } else {
            renderizador.vector("uLuzDir", 0.6f, 0.85f, 0.4f);
            renderizador.vector("uLuzDirColor", 0.88f, 0.88f, 0.82f);
            renderizador.vector("uLuzAmbiente", 0.48f, 0.48f, 0.50f);
        }

        for (int i = 0; i < 9; i++) {
            renderizador.vector("uFarolasPos[" + i + "]", ConstantesCiudad.POS_FAROLAS[i][0] + 3.0f, 6.6f, ConstantesCiudad.POS_FAROLAS[i][1]);
        }

        renderizador.vector("uFaro1Pos", 0, -100, 0);
        renderizador.vector("uFaro2Pos", 0, -100, 0);
        renderizador.vector("uFaroDir", 0, 0, -1);
        renderizador.decimal("uFarosActivos", 0.0f);
    }

    protected void escena() {
        float limite = ConstantesCiudad.LIMITE;
        int[][] mapa = ConstantesCiudad.MAPA;

        // Asfalto
        renderizador.caja(0, -0.2f, 0, limite * 2.0f, 0.4f, limite * 2.0f, 0.14f, 0.14f, 0.15f, 0.0f);
        dibujarVialidadYCebras();

        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                float px = ConstantesCiudad.centro(c);
                float pz = ConstantesCiudad.centro(f);
                int tipo = mapa[f][c];

                switch (tipo) {
                    case 1 -> EdificiosCiudad.dibujarManzanaSubdividida(renderizador, px, pz, f, c, modoNoche);
                    case 2 -> EdificiosCiudad.dibujarParque(renderizador, px, pz, (f + c) % 4);
                    case 3 -> EdificiosCiudad.dibujarHospital(renderizador, px, pz, modoNoche);
                    case 4 -> EdificiosCiudad.dibujarPolicia(renderizador, px, pz, tiempoGlobal, modoNoche);
                    case 5 -> EdificiosCiudad.dibujarMall(renderizador, px, pz, modoNoche);
                    case 6 -> EdificiosCiudad.dibujarComida(renderizador, px, pz, modoNoche);
                    case 7 -> EdificiosCiudad.dibujarTaller(renderizador, px, pz);
                    case 8 -> EdificiosCiudad.dibujarGasolinera(renderizador, px, pz, modoNoche);
                    case 9 -> EdificiosCiudad.dibujarColegio(renderizador, px, pz, modoNoche);
                    default -> {
                        if (f % 2 == 0 && c % 2 == 0 && (f + c) % 4 == 0) {
                            EdificiosCiudad.dibujarInterseccionSemaforos(renderizador, px, pz, tiempoGlobal);
                        }
                    }
                }
            }
        }

        // Farolas
        for (int i = 0; i < 9; i++) {
            float fx = ConstantesCiudad.POS_FAROLAS[i][0];
            float fz = ConstantesCiudad.POS_FAROLAS[i][1];
            renderizador.caja(fx, 3.4f, fz, 0.24f, 6.8f, 0.24f, 0.20f, 0.20f, 0.24f, 0.0f);
            renderizador.caja(fx + 1.5f, 6.5f, fz, 3.0f, 0.16f, 0.20f, 0.20f, 0.20f, 0.24f, 0.0f);

            float emision = modoNoche ? 1.0f : 0.0f;
            renderizador.caja(fx + 3.0f, 6.35f, fz, 1.40f, 0.24f, 0.70f, 1.0f, 0.98f, 0.85f, emision);
            renderizador.caja(fx + 3.0f, 6.20f, fz, 1.20f, 0.06f, 0.55f, 1.0f, 1.0f, 0.95f, emision);
        }

        TraficoManager.dibujar(renderizador, trafico, tiempoGlobal, modoNoche);
        dibujarDestino();
    }

    private void dibujarVialidadYCebras() {
        int[][] mapa = ConstantesCiudad.MAPA;
        float limite = ConstantesCiudad.LIMITE;
        float celda = ConstantesCiudad.CELDA;

        for (int i = 0; i < mapa.length; i++) {
            if (i % 2 == 0) {
                float posCalle = ConstantesCiudad.centro(i);

                for (float k = -limite + 6.0f; k < limite - 6.0f; k += 6.0f) {
                    renderizador.caja(k, 0.01f, posCalle, 2.5f, 0.02f, 0.22f, 0.92f, 0.92f, 0.92f, 0.0f);
                    renderizador.caja(posCalle, 0.01f, k, 0.22f, 0.02f, 2.5f, 0.92f, 0.92f, 0.92f, 0.0f);
                }

                for (int j = 1; j < mapa.length; j += 2) {
                    float posManzana = ConstantesCiudad.centro(j);
                    float distCebra = (celda * 0.88f) / 2.0f - 0.4f;

                    for (float f = -2.8f; f <= 2.8f; f += 0.8f) {
                        renderizador.caja(posManzana + distCebra, 0.015f, posCalle + f, 1.3f, 0.02f, 0.35f, 0.96f, 0.96f, 0.96f, 0.0f);
                        renderizador.caja(posManzana - distCebra, 0.015f, posCalle + f, 1.3f, 0.02f, 0.35f, 0.96f, 0.96f, 0.96f, 0.0f);
                        renderizador.caja(posCalle + f, 0.015f, posManzana + distCebra, 0.35f, 0.02f, 1.3f, 0.96f, 0.96f, 0.96f, 0.0f);
                        renderizador.caja(posCalle + f, 0.015f, posManzana - distCebra, 0.35f, 0.02f, 1.3f, 0.96f, 0.96f, 0.96f, 0.0f);
                    }
                }
            }
        }
    }

    private void dibujarDestino() {
        float rot = tiempoGlobal * 2.0f;
        float bob = (float) Math.sin(tiempoGlobal * 3.0f) * 0.3f;
        renderizador.cajaGirada(destinoX, 2.2f + bob, destinoZ, 1.2f, 1.2f, 1.2f, 1.0f, 0.85f, 0.1f, 1.0f, rot);
        renderizador.caja(destinoX, 5.0f, destinoZ, 0.2f, 6.0f, 0.2f, 1.0f, 0.9f, 0.3f, 1.0f);
    }

    protected void limpiar() {
        renderizador.limpiar();
        if (ventana != 0) GLFW.glfwDestroyWindow(ventana);
        GLFW.glfwTerminate();
    }
}