package com.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class AppCiudad {
    protected long ventana;
    protected int programa;
    protected int bao;
    protected int bbo;
    protected int ancho = 1024;
    protected int alto = 768;

    // Matriz 11x11 estricta
    protected int[][] mapa = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 2, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 2, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 2, 0, 1, 0, 1, 0, 2, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 2, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 2, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    protected float celda = 12.0f;
    protected float limite = (mapa.length * celda) / 2.0f; // 66.0f

    protected float orbita = 0.0f;
    protected boolean modoNoche = false;
    protected boolean verMinimapa = true;
    protected float tiempoGlobal = 0.0f;

    protected float destinoX = 0.0f;
    protected float destinoZ = 24.0f;
    protected int entregasRealizadas = 0;

    protected float[][] posFarolas = {
        {-29.0f, -29.0f}, { 7.0f, -29.0f}, { 43.0f, -29.0f},
        {-29.0f,   7.0f}, { 7.0f,   7.0f}, { 43.0f,   7.0f},
        {-29.0f,  43.0f}, { 7.0f,  43.0f}, { 43.0f,  43.0f}
    };

    protected static class AutoTrafico {
        float x, z, angulo, velBase, velActual;
        float r, g, b;
        float[][] ruta;
        int targetIdx = 0;

        AutoTrafico(float[][] ruta, float r, float g, float b, float vel) {
            this.ruta = ruta;
            this.x = ruta[0][0];
            this.z = ruta[0][1];
            this.r = r; this.g = g; this.b = b;
            this.velBase = vel;
            this.velActual = vel;
            this.angulo = 0.0f;
        }

        void actualizar(float dt, float jugadorX, float jugadorZ) {
            float djX = jugadorX - x;
            float djZ = jugadorZ - z;
            float distJugador = (float) Math.sqrt(djX * djX + djZ * djZ);

            if (distJugador < 5.0f) {
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
                x += fX * velActual * dt;
                z += fZ * velActual * dt;
            }
        }
    }

    protected AutoTrafico[] trafico;
    private Map<String, Integer> uniforms = new HashMap<>();

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();
        try {
            iniciar();
            loop();
        } finally {
            limpiar();
        }
    }

    protected void inicializarTrafico() {
        float c0 = centro(0);
        float c2 = centro(2);
        float c4 = centro(4);
        float c6 = centro(6);
        float c8 = centro(8);
        float c10 = centro(10);

        float[][] r1 = { {c2, c2}, {c8, c2}, {c8, c8}, {c2, c8} };
        float[][] r2 = { {c4, c4}, {c4, c6}, {c6, c6}, {c6, c4} };
        float[][] r3 = { {c0, c0}, {c10, c0}, {c10, c10}, {c0, c10} };

        trafico = new AutoTrafico[] {
            new AutoTrafico(r1, 0.95f, 0.85f, 0.20f, 9.5f),
            new AutoTrafico(r2, 0.20f, 0.70f, 0.95f, 8.5f),
            new AutoTrafico(r3, 0.85f, 0.25f, 0.85f, 10.5f)
        };
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

        ventana = GLFW.glfwCreateWindow(ancho, alto, "Ciudad Interactiva 3D - OpenGL", 0, 0);
        if (ventana == 0) throw new RuntimeException("Error al crear ventana GLFW");

        GLFW.glfwMakeContextCurrent(ventana);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(ventana);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GLFW.glfwSetKeyCallback(ventana, (w, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) tecla(key);
        });

        inicializarTrafico();
        destinoX = centro(6);
        destinoZ = centro(2);

        crearPrograma();
        crearCubo();
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
            GL11.glClearColor(0.02f, 0.02f, 0.05f, 1.0f);
        } else {
            GL11.glClearColor(0.50f, 0.68f, 0.90f, 1.0f);
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(programa);
        GL30.glBindVertexArray(bao);

        // Vista 3D
        GL11.glViewport(0, 0, ancho, alto);
        decimal("uModoMapa", 0.0f);
        decimal("uEsNoche", modoNoche ? 1.0f : 0.0f);
        configurarCamara();
        configurarLuces();
        escena();

        // Minimapa
        if (verMinimapa) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            int tamMini = Math.min(ancho, alto) / 4;
            int posX = ancho - tamMini - 20;
            int posY = alto - tamMini - 20;

            GL11.glViewport(posX, posY, tamMini, tamMini);
            decimal("uModoMapa", 1.0f);
            escena();
        }
    }

    protected void configurarCamara() {
        float camX = (float) Math.sin(orbita) * 95.0f;
        float camZ = (float) Math.cos(orbita) * 95.0f;
        vector("uOjo", camX, 60.0f, camZ);
        vector("uObjetivo", 0.0f, 0.0f, 0.0f);
        decimal("uAspecto", (float) ancho / (float) alto);
    }

    protected void configurarLuces() {
        if (modoNoche) {
            vector("uLuzDir", 0.2f, 0.9f, 0.2f);
            vector("uLuzDirColor", 0.08f, 0.08f, 0.16f);
            vector("uLuzAmbiente", 0.16f, 0.16f, 0.22f);
        } else {
            vector("uLuzDir", 0.6f, 0.8f, 0.4f);
            vector("uLuzDirColor", 0.85f, 0.85f, 0.80f);
            vector("uLuzAmbiente", 0.42f, 0.42f, 0.42f);
        }

        for (int i = 0; i < 9; i++) {
            vector("uFarolasPos[" + i + "]", posFarolas[i][0] + 0.8f, 4.3f, posFarolas[i][1]);
        }

        vector("uFaro1Pos", 0, -100, 0);
        vector("uFaro2Pos", 0, -100, 0);
        vector("uFaroDir", 0, 0, -1);
        decimal("uFarosActivos", 0.0f);
    }

    protected void escena() {
        // Suelo de asfalto
        caja(0, -0.2f, 0, limite * 2.0f, 0.4f, limite * 2.0f, 0.16f, 0.16f, 0.16f, 0.0f);

        dibujarVialidadYCebras();

        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                float px = centro(c);
                float pz = centro(f);
                int tipo = mapa[f][c];

                if (tipo == 1) { // Manzana de edificios
                    caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.45f, 0.45f, 0.47f, 0.0f);
                    float h = 6.0f + ((f * 5 + c * 7) % 10) * 1.4f;
                    float r = 0.30f + ((c * 0.11f) % 0.35f);
                    float g = 0.35f + ((f * 0.13f) % 0.30f);
                    float b = 0.45f + (((f + c) * 0.09f) % 0.30f);
                    caja(px, h / 2.0f + 0.24f, pz, celda * 0.72f, h, celda * 0.72f, r, g, b, 0.0f);
                    dibujarVentanasEdificio(px, pz, h, celda * 0.72f);
                } else if (tipo == 2) { // Parque
                    dibujarParqueVariado(px, pz, (f + c) % 4);
                } else {
                    // Semáforos dobles (uno mirando al eje Z y otro al eje X) en intersecciones
                    if (f % 2 == 0 && c % 2 == 0 && (f + c) % 4 == 0) {
                        dibujarInterseccionSemaforos(px, pz);
                    }
                }
            }
        }

        // 9 Farolas con brazo voladizo
        for (int i = 0; i < 9; i++) {
            float fx = posFarolas[i][0];
            float fz = posFarolas[i][1];
            caja(fx, 2.3f, fz, 0.14f, 4.6f, 0.14f, 0.25f, 0.25f, 0.28f, 0.0f);
            caja(fx + 0.6f, 4.6f, fz, 1.3f, 0.12f, 0.14f, 0.25f, 0.25f, 0.28f, 0.0f);
            float emision = modoNoche ? 1.0f : 0.0f;
            caja(fx + 1.2f, 4.45f, fz, 0.65f, 0.18f, 0.35f, 1.0f, 0.95f, 0.65f, emision);
        }

        // Tráfico autónomo
        if (trafico != null) {
            for (AutoTrafico auto : trafico) {
                cajaGirada(auto.x, 0.45f, auto.z, 1.6f, 0.5f, 2.8f, auto.r, auto.g, auto.b, 0.0f, auto.angulo);
                cajaGirada(auto.x, 0.85f, auto.z, 1.2f, 0.45f, 1.5f, 0.15f, 0.2f, 0.35f, 0.0f, auto.angulo);
            }
        }

        dibujarDestino();
    }

  // Renderizado realista de un banco urbano con patas de soporte, asiento y respaldo
    private void dibujarBanco(float x, float z, float rot) {
        float fX = -(float) Math.sin(rot);
        float fZ =  (float) Math.cos(rot);
        float dX =  (float) Math.cos(rot);
        float dZ =  (float) Math.sin(rot);

        // Color madera barnizada y metal oscuro
        float mr = 0.58f, mg = 0.32f, mb = 0.14f; // Tablones
        float pr = 0.15f, pg = 0.15f, pb = 0.18f; // Patas de hierro

        // 1. Patas de soporte en los dos extremos
        float[][] posPatas = { {-0.65f, 0.0f}, {0.65f, 0.0f} };
        for (float[] p : posPatas) {
            float px = x + dX * p[0];
            float pz = z + dZ * p[0];
            // Pata delantera y trasera
            cajaGirada(px, 0.22f, pz, 0.08f, 0.44f, 0.45f, pr, pg, pb, 0.0f, rot);
            // Soporte vertical para el respaldo
            cajaGirada(px - fX * 0.20f, 0.55f, pz - fZ * 0.20f, 0.08f, 0.40f, 0.08f, pr, pg, pb, 0.0f, rot);
        }

        // 2. Tablones del asiento (2 tablones paralelos con separación)
        cajaGirada(x - fX * 0.08f, 0.44f, z - fZ * 0.08f, 1.50f, 0.06f, 0.20f, mr, mg, mb, 0.0f, rot);
        cajaGirada(x + fX * 0.12f, 0.44f, z + fZ * 0.12f, 1.50f, 0.06f, 0.20f, mr, mg, mb, 0.0f, rot);

        // 3. Tablón del respaldo
        cajaGirada(x - fX * 0.22f, 0.68f, z - fZ * 0.22f, 1.50f, 0.24f, 0.06f, mr, mg, mb, 0.0f, rot);
    }

    // Parques y plazas con bancos detallados orientados hacia senderos y vegetación
    private void dibujarParqueVariado(float px, float pz, int variante) {
        // Base de césped verde de la plaza
        caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.18f, 0.52f, 0.22f, 0.0f);

        if (variante == 0) {
            // PLAZA CENTRAL: Sendero en cruz, gran árbol frondoso al centro y 4 bancos mirándose
            caja(px, 0.13f, pz, 2.2f, 0.02f, celda * 0.88f, 0.72f, 0.65f, 0.52f, 0.0f); // Sendero Norte-Sur
            caja(px, 0.13f, pz, celda * 0.88f, 0.02f, 2.2f, 0.72f, 0.65f, 0.52f, 0.0f); // Sendero Este-Oeste

            // Tronco y copa escalonada frondosa
            caja(px, 1.4f, pz, 0.5f, 2.6f, 0.5f, 0.36f, 0.20f, 0.08f, 0.0f);
            caja(px, 3.2f, pz, 2.8f, 1.4f, 2.8f, 0.12f, 0.58f, 0.16f, 0.0f);
            caja(px, 4.1f, pz, 1.8f, 1.0f, 1.8f, 0.16f, 0.64f, 0.20f, 0.0f);

            // 4 Bancos situados en las esquinas del sendero orientados hacia el centro
            dibujarBanco(px + 2.2f, pz, (float) (-Math.PI / 2.0)); // Mira al oeste
            dibujarBanco(px - 2.2f, pz, (float) (Math.PI / 2.0));  // Mira al este
            dibujarBanco(px, pz + 2.2f, (float) Math.PI);          // Mira al norte
            dibujarBanco(px, pz - 2.2f, 0.0f);                     // Mira al sur

        } else if (variante == 1) {
            // PARQUE DE PINOS: Explanada de descanso con 2 bancos de espaldas a los pinos
            caja(px, 0.13f, pz, 1.8f, 0.02f, celda * 0.88f, 0.75f, 0.68f, 0.55f, 0.0f); // Paseo adoquinado

            // Pino 1 (Norte)
            caja(px - 2.0f, 1.0f, pz - 1.8f, 0.35f, 1.8f, 0.35f, 0.35f, 0.18f, 0.08f, 0.0f);
            caja(px - 2.0f, 2.2f, pz - 1.8f, 1.9f, 0.9f, 1.9f, 0.10f, 0.45f, 0.14f, 0.0f);
            caja(px - 2.0f, 2.9f, pz - 1.8f, 1.3f, 0.8f, 1.3f, 0.12f, 0.52f, 0.16f, 0.0f);
            caja(px - 2.0f, 3.5f, pz - 1.8f, 0.7f, 0.7f, 0.7f, 0.15f, 0.60f, 0.20f, 0.0f);

            // Pino 2 (Sur)
            caja(px - 2.0f, 1.0f, pz + 1.8f, 0.35f, 1.8f, 0.35f, 0.35f, 0.18f, 0.08f, 0.0f);
            caja(px - 2.0f, 2.2f, pz + 1.8f, 1.9f, 0.9f, 1.9f, 0.10f, 0.45f, 0.14f, 0.0f);
            caja(px - 2.0f, 2.9f, pz + 1.8f, 1.3f, 0.8f, 1.3f, 0.12f, 0.52f, 0.16f, 0.0f);

            // 2 Bancos colocados junto al paseo peatonal
            dibujarBanco(px + 1.6f, pz - 1.8f, (float) (-Math.PI / 2.0));
            dibujarBanco(px + 1.6f, pz + 1.8f, (float) (-Math.PI / 2.0));

        } else if (variante == 2) {
            // PARQUE BOTÁNICO CON FUENTE/CANTERO Y BANCOS PERIMETRALES
            // Estructura de cantero central de adoquín
            caja(px, 0.30f, pz, 2.6f, 0.35f, 2.6f, 0.60f, 0.60f, 0.62f, 0.0f);
            // Agua o flores al centro del cantero
            caja(px, 0.40f, pz, 2.2f, 0.20f, 2.2f, 0.20f, 0.65f, 0.85f, 0.0f);

            // 2 Bancos enfrentados a los lados del cantero
            dibujarBanco(px, pz - 2.5f, 0.0f);
            dibujarBanco(px, pz + 2.5f, (float) Math.PI);

            // 4 Arbustos esféricos decorativos en las 4 esquinas de la plaza
            float[][] arbustos = { {-3.2f, -3.2f}, {3.2f, -3.2f}, {-3.2f, 3.2f}, {3.2f, 3.2f} };
            for (float[] arb : arbustos) {
                caja(px + arb[0], 0.55f, pz + arb[1], 1.1f, 0.9f, 1.1f, 0.14f, 0.58f, 0.18f, 0.0f);
            }

        } else {
            // PLAZA JARDÍN URBANA: 3 bancos distribuidos entre árboles de sombra
            dibujarBanco(px - 2.2f, pz, (float) (Math.PI / 2.0));
            dibujarBanco(px + 2.2f, pz, (float) (-Math.PI / 2.0));
            dibujarBanco(px, pz + 2.4f, (float) Math.PI);

            // Árboles de sombra asimétricos
            caja(px - 1.8f, 1.1f, pz - 2.2f, 0.35f, 2.0f, 0.35f, 0.36f, 0.22f, 0.10f, 0.0f);
            caja(px - 1.8f, 2.4f, pz - 2.2f, 1.9f, 1.3f, 1.9f, 0.12f, 0.55f, 0.16f, 0.0f);

            caja(px + 1.8f, 1.1f, pz - 2.2f, 0.35f, 2.0f, 0.35f, 0.36f, 0.22f, 0.10f, 0.0f);
            caja(px + 1.8f, 2.4f, pz - 2.2f, 1.9f, 1.3f, 1.9f, 0.12f, 0.55f, 0.16f, 0.0f);
        }
    }

private void dibujarVialidadYCebras() {
        for (int i = 0; i < mapa.length; i++) {
            if (i % 2 == 0) {
                float posCalle = centro(i);

                // Líneas divisorias discontinuas por el eje central de las calles
                for (float k = -limite + 6.0f; k < limite - 6.0f; k += 6.0f) {
                    caja(k, 0.01f, posCalle, 2.5f, 0.02f, 0.22f, 0.92f, 0.92f, 0.92f, 0.0f);
                    caja(posCalle, 0.01f, k, 0.22f, 0.02f, 2.5f, 0.92f, 0.92f, 0.92f, 0.0f);
                }

                // Pasos de cebra retraídos hacia las aceras (alineados con el peatón)
                for (int j = 1; j < mapa.length; j += 2) {
                    float posManzana = centro(j);
                    // Borde de acera en 5.28m; ubicamos el paso de cebra a 4.6m (antes de la bocacalle)
                    float distCebra = (celda * 0.88f) / 2.0f - 0.4f;

                    for (float f = -2.8f; f <= 2.8f; f += 0.8f) {
                        // Cebras en calles horizontales
                        caja(posManzana + distCebra, 0.015f, posCalle + f, 1.3f, 0.02f, 0.35f, 0.96f, 0.96f, 0.96f, 0.0f);
                        caja(posManzana - distCebra, 0.015f, posCalle + f, 1.3f, 0.02f, 0.35f, 0.96f, 0.96f, 0.96f, 0.0f);
                        // Cebras en calles verticales
                        caja(posCalle + f, 0.015f, posManzana + distCebra, 0.35f, 0.02f, 1.3f, 0.96f, 0.96f, 0.96f, 0.0f);
                        caja(posCalle + f, 0.015f, posManzana - distCebra, 0.35f, 0.02f, 1.3f, 0.96f, 0.96f, 0.96f, 0.0f);
                    }
                }
            }
        }
    }

// Dibuja los semáforos en las 4 esquinas del cruce (uno para cada una de las 4 direcciones)
    private void dibujarInterseccionSemaforos(float px, float pz) {
        float margenAcera = (celda * 0.88f) / 2.0f - 0.45f; // ~4.8m sobre la acera

        // =========================================================================
        // EJE NORTE - SUR (Avenida Z): Ciclo Principal
        // =========================================================================
        // 1. Esquina Noroeste: Regula el tráfico que baja hacia el Sur (mira a -Z)
        float s1X = px + margenAcera;
        float s1Z = pz - margenAcera;
        caja(s1X, 2.3f, s1Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        caja(s1X - 0.6f, 4.4f, s1Z, 1.2f, 0.14f, 0.12f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(s1X - 1.2f, 4.2f, s1Z, 0.0f, tiempoGlobal);

        // 2. Esquina Sureste: Regula el tráfico que sube hacia el Norte (mira a +Z)
        float s2X = px - margenAcera;
        float s2Z = pz + margenAcera;
        caja(s2X, 2.3f, s2Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        caja(s2X + 0.6f, 4.4f, s2Z, 1.2f, 0.14f, 0.12f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(s2X + 1.2f, 4.2f, s2Z, (float) Math.PI, tiempoGlobal);

        // =========================================================================
        // EJE ESTE - OESTE (Calle transversal X): Ciclo Desfasado (+4 segundos)
        // =========================================================================
        // 3. Esquina Suroeste: Regula el tráfico que va de Oeste a Este (mira a -X)
        float s3X = px - margenAcera;
        float s3Z = pz - margenAcera;
        caja(s3X, 2.3f, s3Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        caja(s3X, 4.4f, s3Z + 0.6f, 0.12f, 0.14f, 1.2f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(s3X, 4.2f, s3Z + 1.2f, (float) (Math.PI / 2.0), tiempoGlobal + 4.0f);

        // 4. Esquina Noreste: Regula el tráfico que va de Este a Oeste (mira a +X)
        float s4X = px + margenAcera;
        float s4Z = pz + margenAcera;
        caja(s4X, 2.3f, s4Z, 0.16f, 4.6f, 0.16f, 0.22f, 0.22f, 0.25f, 0.0f);
        caja(s4X, 4.4f, s4Z - 0.6f, 0.12f, 0.14f, 1.2f, 0.22f, 0.22f, 0.25f, 0.0f);
        dibujarCabinaLuces(s4X, 4.2f, s4Z - 1.2f, (float) (-Math.PI / 2.0), tiempoGlobal + 4.0f);
    }

    private void dibujarCabinaLuces(float x, float y, float z, float rot, float tiempo) {
        cajaGirada(x, y, z, 0.35f, 1.15f, 0.35f, 0.10f, 0.10f, 0.10f, 0.0f, rot);

        float ciclo = tiempo % 8.0f;
        float vr = 0.08f, vg = 0.08f, vb = 0.08f, emV = 0.0f;
        float ar = 0.08f, ag = 0.08f, ab = 0.08f, emA = 0.0f;
        float rr = 0.08f, rg = 0.08f, rb = 0.08f, emR = 0.0f;

        // Secuencia sincronizada: 0 - 3.2s Verde | 3.2 - 4.4s Amarillo | 4.4 - 8.0s Rojo
        if (ciclo < 3.2f) {
            vg = 1.0f; emV = 1.0f;
        } else if (ciclo < 4.4f) {
            ar = 1.0f; ag = 0.85f; emA = 1.0f;
        } else {
            rr = 1.0f; emR = 1.0f;
        }

        // Vector normal frontal según la rotación angular exacta de cada semáforo
        float dirCaraX = (float) Math.sin(rot);
        float dirCaraZ = -(float) Math.cos(rot);

        float distCara = 0.19f;
        float posX = x + dirCaraX * distCara;
        float posZ = z + dirCaraZ * distCara;

        // Focos visibles en la cara delantera hacia el carril respectivo
        cajaGirada(posX, y + 0.34f, posZ, 0.18f, 0.18f, 0.05f, rr, rg, rb, emR, rot);
        cajaGirada(posX, y,         posZ, 0.18f, 0.18f, 0.05f, ar, ag, ab, emA, rot);
        cajaGirada(posX, y - 0.34f, posZ, 0.18f, 0.18f, 0.05f, vr, vg, vb, emV, rot);
    }

    private void dibujarVentanasEdificio(float px, float pz, float h, float anchoE) {
        float emision = modoNoche ? 1.0f : 0.0f;
        float r = modoNoche ? 0.98f : 0.35f;
        float g = modoNoche ? 0.85f : 0.48f;
        float b = modoNoche ? 0.30f : 0.65f;

        for (float y = 1.6f; y < h - 0.8f; y += 1.8f) {
            caja(px, y, pz + anchoE / 2.0f + 0.02f, anchoE * 0.75f, 0.7f, 0.04f, r, g, b, emision);
            caja(px, y, pz - anchoE / 2.0f - 0.02f, anchoE * 0.75f, 0.7f, 0.04f, r, g, b, emision);
            caja(px + anchoE / 2.0f + 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.75f, r, g, b, emision);
            caja(px - anchoE / 2.0f - 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.75f, r, g, b, emision);
        }
    }

    private void dibujarDestino() {
        float rot = tiempoGlobal * 2.0f;
        float bob = (float) Math.sin(tiempoGlobal * 3.0f) * 0.3f;
        cajaGirada(destinoX, 2.2f + bob, destinoZ, 1.2f, 1.2f, 1.2f, 1.0f, 0.85f, 0.1f, 1.0f, rot);
        caja(destinoX, 5.0f, destinoZ, 0.2f, 6.0f, 0.2f, 1.0f, 0.9f, 0.3f, 1.0f);
    }
    public float centro(int indice) {
        return -limite + (celda * indice) + (celda / 2.0f);
    }

    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float emision) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, emision, 0.0f);
    }

    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0.0f, 0.0f);
    }

    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float angulo) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0.0f, angulo);
    }

    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float emision, float angulo) {
        vector("uPos", x, y, z);
        vector("uEscala", sx, sy, sz);
        vector("uColor", r, g, b);
        decimal("uEmision", emision);
        decimal("uGiro", angulo);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
    }

    public void vector(String nombre, float x, float y, float z) {
        GL20.glUniform3f(obtenerUniform(nombre), x, y, z);
    }

    public void decimal(String nombre, float valor) {
        GL20.glUniform1f(obtenerUniform(nombre), valor);
    }

    private int obtenerUniform(String nombre) {
        if (!uniforms.containsKey(nombre)) {
            int loc = GL20.glGetUniformLocation(programa, nombre);
            uniforms.put(nombre, loc);
        }
        return uniforms.get(nombre);
    }

    private void crearCubo() {
        float[] v = {
            // Frontal (Z+)
            -0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f, 0.5f, 0.5f,  0, 0, 1,
            -0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f, 0.5f, 0.5f,  0, 0, 1,  -0.5f, 0.5f, 0.5f,  0, 0, 1,
            // Trasera (Z-)
            -0.5f,-0.5f,-0.5f,  0, 0,-1,   0.5f, 0.5f,-0.5f,  0, 0,-1,   0.5f,-0.5f,-0.5f,  0, 0,-1,
            -0.5f,-0.5f,-0.5f,  0, 0,-1,  -0.5f, 0.5f,-0.5f,  0, 0,-1,   0.5f, 0.5f,-0.5f,  0, 0,-1,
            // Izquierda (X-)
            -0.5f,-0.5f,-0.5f, -1, 0, 0,  -0.5f,-0.5f, 0.5f, -1, 0, 0,  -0.5f, 0.5f, 0.5f, -1, 0, 0,
            -0.5f,-0.5f,-0.5f, -1, 0, 0,  -0.5f, 0.5f, 0.5f, -1, 0, 0,  -0.5f, 0.5f,-0.5f, -1, 0, 0,
            // Derecha (X+)
             0.5f,-0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f, 0.5f,  1, 0, 0,   0.5f,-0.5f, 0.5f,  1, 0, 0,
             0.5f,-0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f, 0.5f,  1, 0, 0,
            // Superior (Y+)
            -0.5f, 0.5f,-0.5f,  0, 1, 0,  -0.5f, 0.5f, 0.5f,  0, 1, 0,   0.5f, 0.5f, 0.5f,  0, 1, 0,
            -0.5f, 0.5f,-0.5f,  0, 1, 0,   0.5f, 0.5f, 0.5f,  0, 1, 0,   0.5f, 0.5f,-0.5f,  0, 1, 0,
            // Inferior (Y-)
            -0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f, 0.5f,  0,-1, 0,  -0.5f,-0.5f, 0.5f,  0,-1, 0,
            -0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f, 0.5f,  0,-1, 0
        };

        bao = GL30.glGenVertexArrays();
        bbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(bao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bbo);

        FloatBuffer fb = BufferUtils.createFloatBuffer(v.length);
        fb.put(v).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);

        int stride = 6 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    private void crearPrograma() {
        String vs = """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            layout (location = 1) in vec3 aNormal;

            uniform vec3 uPos;
            uniform vec3 uEscala;
            uniform float uGiro;
            uniform vec3 uOjo;
            uniform vec3 uObjetivo;
            uniform float uAspecto;
            uniform float uModoMapa;

            out vec3 vMundo;
            out vec3 vNormal;

            void main() {
                float c = cos(uGiro);
                float s = sin(uGiro);
                mat3 rotY = mat3(
                    c, 0.0, -s,
                    0.0, 1.0, 0.0,
                    s, 0.0, c
                );

                vec3 posLocal = aPos * uEscala;
                vec3 posRotada = rotY * posLocal;
                vec3 mundo = posRotada + uPos;

                vMundo = mundo;
                vNormal = normalize(rotY * (aNormal / uEscala));

                if (uModoMapa > 0.5) {
                    gl_Position = vec4(mundo.x / 68.0, -mundo.z / 68.0, -mundo.y / 200.0, 1.0);
                } else {
                    vec3 f = normalize(uObjetivo - uOjo);
                    vec3 r = normalize(cross(f, vec3(0.0, 1.0, 0.0)));
                    vec3 u = cross(r, f);

                    vec3 d = mundo - uOjo;
                    float vx = dot(d, r);
                    float vy = dot(d, u);
                    float vz = -dot(d, f);

                    float fovRad = radians(55.0);
                    float factor = 1.0 / tan(fovRad * 0.5);
                    float cerca = 0.1;
                    float lejos = 350.0;

                    float cx = (vx * factor) / uAspecto;
                    float cy = vy * factor;
                    float cz = ((lejos + cerca) / (cerca - lejos)) * vz + ((2.0 * lejos * cerca) / (cerca - lejos));

                    gl_Position = vec4(cx, cy, cz, -vz);
                }
            }
        """;

        String fs = """
            #version 330 core
            in vec3 vMundo;
            in vec3 vNormal;
            out vec4 FragColor;

            uniform vec3 uColor;
            uniform float uEmision;
            uniform float uEsNoche;
            uniform vec3 uLuzDir;
            uniform vec3 uLuzDirColor;
            uniform vec3 uLuzAmbiente;

            uniform vec3 uFarolasPos[9];
            uniform vec3 uFaro1Pos;
            uniform vec3 uFaro2Pos;
            uniform vec3 uFaroDir;
            uniform float uFarosActivos;
            uniform float uModoMapa;

            void main() {
                if (uModoMapa > 0.5) {
                    FragColor = vec4(uColor, 1.0);
                    return;
                }

                if (uEmision > 0.5) {
                    FragColor = vec4(uColor * 1.35, 1.0);
                    return;
                }

                vec3 N = normalize(vNormal);
                float diffDir = max(dot(N, normalize(uLuzDir)), 0.0);
                vec3 iluminacion = uLuzAmbiente + (uLuzDirColor * diffDir);

                for (int i = 0; i < 9; i++) {
                    vec3 lDir = uFarolasPos[i] - vMundo;
                    float dist = length(lDir);
                    if (dist < 32.0) {
                        float att = 1.0 / (1.0 + 0.14 * dist + 0.04 * dist * dist);
                        float diffPunto = max(dot(N, normalize(lDir)), 0.0);
                        vec3 colorFarola = uEsNoche > 0.5 ? vec3(1.0, 0.92, 0.65) * 2.6 : vec3(0.8, 0.8, 0.7) * 0.5;
                        iluminacion += colorFarola * diffPunto * att;
                    }
                }

                if (uFarosActivos > 0.5) {
                    vec3 dirFaroNorm = normalize(uFaroDir);

                    vec3 d1 = uFaro1Pos - vMundo;
                    float dist1 = length(d1);
                    vec3 d1N = normalize(-d1);
                    float theta1 = dot(d1N, dirFaroNorm);
                    if (theta1 > 0.84 && dist1 < 30.0) {
                        float att1 = (theta1 - 0.84) / 0.16 * (1.0 / (1.0 + 0.08 * dist1 + 0.03 * dist1 * dist1));
                        float diff1 = max(dot(N, normalize(d1)), 0.0);
                        iluminacion += vec3(1.0, 1.0, 0.88) * diff1 * att1 * 2.8;
                    }

                    vec3 d2 = uFaro2Pos - vMundo;
                    float dist2 = length(d2);
                    vec3 d2N = normalize(-d2);
                    float theta2 = dot(d2N, dirFaroNorm);
                    if (theta2 > 0.84 && dist2 < 30.0) {
                        float att2 = (theta2 - 0.84) / 0.16 * (1.0 / (1.0 + 0.08 * dist2 + 0.03 * dist2 * dist2));
                        float diff2 = max(dot(N, normalize(d2)), 0.0);
                        iluminacion += vec3(1.0, 1.0, 0.88) * diff2 * att2 * 2.8;
                    }
                }

                FragColor = vec4(uColor * iluminacion, 1.0);
            }
        """;

        int vid = compilarShader(GL20.GL_VERTEX_SHADER, vs);
        int fid = compilarShader(GL20.GL_FRAGMENT_SHADER, fs);

        programa = GL20.glCreateProgram();
        GL20.glAttachShader(programa, vid);
        GL20.glAttachShader(programa, fid);
        GL20.glLinkProgram(programa);

        if (GL20.glGetProgrami(programa, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error enlazando shaders: " + GL20.glGetProgramInfoLog(programa));
        }

        GL20.glDeleteShader(vid);
        GL20.glDeleteShader(fid);
    }

    private int compilarShader(int tipo, String src) {
        int id = GL20.glCreateShader(tipo);
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error compilando shader (" + tipo + "): " + GL20.glGetShaderInfoLog(id));
        }
        return id;
    }

    protected void limpiar() {
        if (bbo != 0) GL15.glDeleteBuffers(bbo);
        if (bao != 0) GL30.glDeleteVertexArrays(bao);
        if (programa != 0) GL20.glDeleteProgram(programa);
        if (ventana != 0) GLFW.glfwDestroyWindow(ventana);
        GLFW.glfwTerminate();
    }
}