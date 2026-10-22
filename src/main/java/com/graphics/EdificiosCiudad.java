package com.graphics;

public class EdificiosCiudad {

    // ==============================================================
    // 1. HAMBURGUESERÍA ("BURGER DINER" con Hamburguesa Gigante 3D)
    // ==============================================================
    public static void dibujarHamburgueseria(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        // Acera base elevada
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);

        // Estructura del restaurante (Blanco y friso rojo)
        r.caja(px - 1.2f, 1.8f, pz - 0.8f, 5.5f, 3.2f, 5.2f, 0.94f, 0.94f, 0.94f, 0.0f);
        r.caja(px - 1.2f, 3.5f, pz - 0.8f, 5.8f, 0.4f, 5.5f, 0.90f, 0.20f, 0.15f, 0.0f); // Cornisa roja

        // Marquesina / Toldos frontales a rayas
        r.caja(px - 1.2f, 2.2f, pz + 2.0f, 4.6f, 0.2f, 1.4f, 0.92f, 0.85f, 0.15f, 0.0f);

        // HAMBURGUESA GIGANTE EN EL TECHO
        float by = 4.0f;
        float bx = px - 1.2f;
        float bz = pz - 0.8f;
        // Pan inferior
        r.caja(bx, by + 0.25f, bz, 3.2f, 0.5f, 3.2f, 0.85f, 0.55f, 0.22f, 0.0f);
        // Carne
        r.caja(bx, by + 0.65f, bz, 3.4f, 0.35f, 3.4f, 0.38f, 0.18f, 0.10f, 0.0f);
        // Queso amarillo derretido
        r.cajaGirada(bx, by + 0.88f, bz, 3.2f, 0.12f, 3.2f, 0.98f, 0.82f, 0.10f, 0.0f, 0.35f);
        // Lechuga verde ondulada
        r.cajaGirada(bx, by + 1.02f, bz, 3.5f, 0.15f, 3.5f, 0.22f, 0.75f, 0.20f, 0.0f, -0.40f);
        // Pan superior redondeado
        r.caja(bx, by + 1.45f, bz, 3.0f, 0.70f, 3.0f, 0.88f, 0.58f, 0.24f, 0.0f);
        r.caja(bx, by + 1.85f, bz, 2.2f, 0.25f, 2.2f, 0.88f, 0.58f, 0.24f, 0.0f);

        // Ventanales y zona de mesas
        float emVentana = modoNoche ? 1.0f : 0.0f;
        r.caja(bx, 1.6f, bz + 1.85f, 4.0f, 1.4f, 0.10f, 0.25f, 0.65f, 0.85f, emVentana);

        // Vegetación / Maceteros exteriores
        dibujarArbolDiorama(r, px + 3.2f, pz + 2.8f);
        dibujarMaceteroConFlores(r, px - 3.8f, pz + 2.5f);
    }

    // ==============================================================
    // 2. CANCHA DE FÚTBOL CON TRIBUNAS (Football Field con Gradas)
    // ==============================================================
    public static void dibujarCanchaFutbol(RenderizadorOpenGL r, float px, float pz) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);

        // Césped sintético verde brillante
        r.caja(px, 0.25f, pz + 0.8f, 8.8f, 0.04f, 5.8f, 0.20f, 0.72f, 0.28f, 0.0f);

        // Líneas blancas de la cancha
        float cy = 0.28f;
        // Perímetro
        r.caja(px, cy, pz + 0.8f, 8.2f, 0.02f, 0.12f, 0.98f, 0.98f, 0.98f, 0.0f);
        r.caja(px, cy, pz - 1.9f, 8.2f, 0.02f, 0.12f, 0.98f, 0.98f, 0.98f, 0.0f);
        r.caja(px, cy, pz + 3.5f, 8.2f, 0.02f, 0.12f, 0.98f, 0.98f, 0.98f, 0.0f);
        r.caja(px - 4.0f, cy, pz + 0.8f, 0.12f, 0.02f, 5.5f, 0.98f, 0.98f, 0.98f, 0.0f);
        r.caja(px + 4.0f, cy, pz + 0.8f, 0.12f, 0.02f, 5.5f, 0.98f, 0.98f, 0.98f, 0.0f);
        // Círculo central (aproximación cuadrada low-poly)
        r.cajaGirada(px, cy, pz + 0.8f, 1.4f, 0.02f, 1.4f, 0.98f, 0.98f, 0.98f, 0.0f, 0.785f);

        // Arcos de fútbol
        r.caja(px - 3.9f, 0.7f, pz + 0.8f, 0.10f, 0.8f, 1.6f, 0.95f, 0.95f, 0.95f, 0.0f);
        r.caja(px + 3.9f, 0.7f, pz + 0.8f, 0.10f, 0.8f, 1.6f, 0.95f, 0.95f, 0.95f, 0.0f);

        // Tribunas escalonadas con butacas blancas
        float ty = 0.4f;
        float tz = pz - 2.8f;
        for (int i = 0; i < 3; i++) {
            r.caja(px, ty + i * 0.35f, tz - i * 0.5f, 7.8f, 0.35f, 0.60f, 0.88f, 0.88f, 0.90f, 0.0f);
            // Sillas blancas
            for (float sx = -3.2f; sx <= 3.2f; sx += 0.8f) {
                r.caja(px + sx, ty + (i + 1) * 0.35f + 0.08f, tz - i * 0.5f, 0.45f, 0.12f, 0.40f, 0.98f, 0.98f, 0.98f, 0.0f);
            }
        }

        // Iluminación deportiva (Torres de focos)
        r.caja(px - 4.4f, 2.5f, pz - 2.5f, 0.18f, 5.0f, 0.18f, 0.3f, 0.3f, 0.35f, 0.0f);
        r.caja(px + 4.4f, 2.5f, pz - 2.5f, 0.18f, 5.0f, 0.18f, 0.3f, 0.3f, 0.35f, 0.0f);
        r.caja(px - 4.4f, 5.0f, pz - 2.5f, 0.8f, 0.3f, 0.4f, 1.0f, 1.0f, 0.9f, 0.8f);
        r.caja(px + 4.4f, 5.0f, pz - 2.5f, 0.8f, 0.3f, 0.4f, 1.0f, 1.0f, 0.9f, 0.8f);
    }

    // ==============================================================
    // 3. EDIFICIO CORPORATIVO ("OFFICE" Cristal Vidriado y Antena)
    // ==============================================================
    public static void dibujarOficinasVidrio(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);

        // Bloque de recepción (Piso 1)
        r.caja(px - 1.0f, 1.6f, pz + 1.2f, 4.8f, 2.8f, 3.6f, 0.95f, 0.95f, 0.95f, 0.0f);
        r.caja(px - 1.0f, 3.1f, pz + 1.2f, 5.0f, 0.25f, 3.8f, 0.90f, 0.18f, 0.18f, 0.0f); // Franja roja

        // Cartel "Office"
        r.caja(px - 1.0f, 2.8f, pz + 3.05f, 1.8f, 0.45f, 0.10f, 0.90f, 0.15f, 0.15f, 1.0f);

        // Torre acristalada de dos pisos con marcos rojos
        float emCristal = modoNoche ? 1.0f : 0.0f;
        r.caja(px + 1.2f, 3.2f, pz - 0.8f, 4.4f, 6.0f, 4.8f, 0.25f, 0.70f, 0.90f, emCristal);
        // Losas estructurales
        r.caja(px + 1.2f, 0.3f, pz - 0.8f, 4.6f, 0.30f, 5.0f, 0.90f, 0.18f, 0.18f, 0.0f);
        r.caja(px + 1.2f, 3.2f, pz - 0.8f, 4.6f, 0.25f, 5.0f, 0.90f, 0.18f, 0.18f, 0.0f);
        r.caja(px + 1.2f, 6.3f, pz - 0.8f, 4.6f, 0.30f, 5.0f, 0.90f, 0.18f, 0.18f, 0.0f);

        // Equipamiento en el techo: Climatizadores y Antena de telecomunicaciones
        float ay = 6.6f;
        r.caja(px + 0.5f, ay + 0.4f, pz - 1.8f, 1.0f, 0.6f, 0.8f, 0.8f, 0.8f, 0.82f, 0.0f);
        r.caja(px + 2.0f, ay + 0.4f, pz - 1.8f, 1.0f, 0.6f, 0.8f, 0.8f, 0.8f, 0.82f, 0.0f);
        // Antena
        r.caja(px + 2.4f, ay + 1.8f, pz + 0.6f, 0.12f, 3.2f, 0.12f, 0.25f, 0.25f, 0.28f, 0.0f);
        r.caja(px + 2.4f, ay + 3.4f, pz + 0.6f, 0.30f, 0.30f, 0.30f, 1.0f, 0.1f, 0.1f, 1.0f); // Luz baliza roja

        // Banderas / Letreros exteriores
        dibujarArbolDiorama(r, px - 3.6f, pz - 3.0f);
        dibujarContenedoresBasura(r, px + 3.8f, pz + 3.0f);
    }

    // ==============================================================
    // 4. GASOLINERA CON PANELES SOLARES (Gas Station moderna)
    // ==============================================================
    public static void dibujarGasolinera(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        // Pista de hormigón a nivel
        r.caja(px, 0.015f, pz, celda * 0.88f, 0.03f, celda * 0.88f, 0.35f, 0.36f, 0.38f, 0.0f);

        // Marquesina con techo cubierto de paneles solares azules
        r.caja(px, 5.5f, pz, 10.2f, 0.45f, 8.8f, 0.94f, 0.94f, 0.95f, 0.0f);
        // Paneles solares fotovoltaicos en el techo
        r.caja(px, 5.75f, pz, 9.6f, 0.05f, 8.2f, 0.12f, 0.22f, 0.45f, 0.0f);
        // Franja perimetral roja/naranja
        r.caja(px, 5.4f, pz + 4.45f, 10.2f, 0.30f, 0.12f, 0.95f, 0.25f, 0.10f, 0.0f);
        r.caja(px, 5.4f, pz - 4.45f, 10.2f, 0.30f, 0.12f, 0.95f, 0.25f, 0.10f, 0.0f);

        // Columnas cilíndricas blancas
        float[][] colsGas = { {-4.6f, -3.6f}, {4.6f, -3.6f}, {-4.6f, 3.6f}, {4.6f, 3.6f} };
        for (float[] cg : colsGas) {
            r.caja(px + cg[0], 2.75f, pz + cg[1], 0.35f, 5.5f, 0.35f, 0.95f, 0.95f, 0.95f, 0.0f);
        }

        // Iluminación LED diurna/nocturna
        float emPlafon = modoNoche ? 1.0f : 0.0f;
        r.caja(px, 5.25f, pz, 4.5f, 0.05f, 7.0f, 1.0f, 1.0f, 0.96f, emPlafon);

        // Islas de surtidores
        float[] islasX = {-3.4f, 3.4f};
        for (float ix : islasX) {
            r.caja(px + ix, 0.15f, pz, 0.75f, 0.25f, 4.4f, 0.85f, 0.85f, 0.88f, 0.0f);
            r.caja(px + ix, 0.95f, pz - 1.2f, 0.50f, 1.4f, 0.75f, 0.90f, 0.20f, 0.15f, 0.0f);
            r.caja(px + ix, 0.95f, pz + 1.2f, 0.50f, 1.4f, 0.75f, 0.95f, 0.80f, 0.10f, 0.0f);
        }

        // Mini tienda de conveniencia al fondo
        r.caja(px + 3.0f, 1.6f, pz - 3.2f, 3.6f, 3.0f, 2.2f, 0.95f, 0.95f, 0.95f, 0.0f);
    }

    // ==============================================================
    // 5. CENTRO COMERCIAL ("FASION STORE" con Techo Abovedado de Vidrio)
    // ==============================================================
    public static void dibujarMall(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);

        // Bloque central
        r.caja(px, 2.5f, pz, 8.8f, 4.8f, 8.6f, 0.95f, 0.95f, 0.95f, 0.0f);
        // Friso rojo/magenta
        r.caja(px, 4.8f, pz, 9.0f, 0.40f, 8.8f, 0.88f, 0.18f, 0.28f, 0.0f);

        // Claraboya abovedada de vidrio en la azotea
        float emClara = modoNoche ? 0.9f : 0.0f;
        r.caja(px, 5.3f, pz, 4.5f, 0.8f, 5.0f, 0.20f, 0.70f, 0.88f, emClara);

        // Cartel luminoso "Fasion Store"
        r.caja(px, 5.3f, pz + 4.35f, 5.2f, 0.8f, 0.15f, 0.95f, 0.20f, 0.25f, 1.0f);

        // Toldo de entrada con diseño playero/diorama
        r.caja(px, 1.6f, pz + 4.45f, 4.0f, 0.18f, 1.2f, 0.95f, 0.35f, 0.20f, 0.0f);

        dibujarVentanasDiorama(r, px, pz, 4.8f, 8.6f, modoNoche);
        dibujarMaceteroConFlores(r, px - 3.8f, pz + 4.0f);
        dibujarMaceteroConFlores(r, px + 3.8f, pz + 4.0f);
    }

    // ==============================================================
    // 6. TIENDA DE DONAS ("DONUT SHOP" con Rueda Escultural)
    // ==============================================================
    public static void dibujarTiendaDonas(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);

        // Edificio de cafetería
        r.caja(px, 1.8f, pz, 6.0f, 3.2f, 5.5f, 0.92f, 0.92f, 0.94f, 0.0f);
        r.caja(px, 3.3f, pz, 6.2f, 0.3f, 5.7f, 0.95f, 0.60f, 0.15f, 0.0f); // Friso naranja

        // DONA GIGANTE EN EL TECHO (Torbellino volumétrico)
        float dy = 4.8f;
        r.caja(px, dy, pz, 2.6f, 2.6f, 0.6f, 0.88f, 0.60f, 0.30f, 0.0f); // Masa de dona
        r.caja(px, dy, pz, 2.8f, 2.8f, 0.3f, 0.95f, 0.40f, 0.65f, 0.0f); // Glaseado rosa
        r.caja(px, dy, pz, 0.9f, 0.9f, 0.7f, 0.85f, 0.84f, 0.82f, 0.0f); // Agujero central

        // Chispas de colores en la dona
        r.caja(px - 0.6f, dy + 0.6f, pz + 0.35f, 0.20f, 0.08f, 0.05f, 0.98f, 0.95f, 0.1f, 0.0f);
        r.caja(px + 0.6f, dy - 0.5f, pz + 0.35f, 0.08f, 0.20f, 0.05f, 0.2f, 0.85f, 0.9f, 0.0f);

        dibujarArbolDiorama(r, px - 3.2f, pz + 2.8f);
    }

    // ==============================================================
    // 7. HOSPITAL MODERNO ("CLINIC / HOSPITAL" Curvo Diorama)
    // ==============================================================
    public static void dibujarHospital(RenderizadorOpenGL r, float px, float pz, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz - 2.0f, celda * 0.88f, 0.24f, 6.0f, 0.85f, 0.84f, 0.82f, 0.0f);
        r.caja(px, 0.01f, pz + 2.5f, celda * 0.88f, 0.02f, 4.5f, 0.28f, 0.28f, 0.30f, 0.0f);

        // Estacionamiento marcado
        for (float ox = -3.5f; ox <= 3.5f; ox += 2.2f) {
            r.caja(px + ox, 0.02f, pz + 2.5f, 0.12f, 0.02f, 3.8f, 0.95f, 0.95f, 0.95f, 0.0f);
        }

        // Edificio hospitalario blanco
        r.caja(px, 4.5f, pz - 2.0f, 8.6f, 8.5f, 5.5f, 0.96f, 0.96f, 0.98f, 0.0f);

        // Bloque de emergencia rojo frontal
        r.caja(px - 2.2f, 7.8f, pz + 0.85f, 2.8f, 1.8f, 0.3f, 0.90f, 0.20f, 0.20f, 0.0f);

        // Cruz de hospital
        float emCruz = modoNoche ? 1.0f : 0.4f;
        r.caja(px + 1.8f, 6.5f, pz + 0.85f, 1.8f, 0.55f, 0.15f, 0.92f, 0.15f, 0.15f, emCruz);
        r.caja(px + 1.8f, 6.5f, pz + 0.85f, 0.55f, 1.8f, 0.15f, 0.92f, 0.15f, 0.15f, emCruz);

        // Marquesina de ambulancias
        r.caja(px, 2.2f, pz + 1.2f, 4.2f, 0.2f, 2.2f, 0.90f, 0.20f, 0.20f, 0.0f);

        dibujarVentanasDiorama(r, px, pz - 2.0f, 8.5f, 5.5f, modoNoche);
    }

    // ==============================================================
    // 8. POLICÍA Y BOMBEROS
    // ==============================================================
    public static void dibujarPolicia(RenderizadorOpenGL r, float px, float pz, float tiempoGlobal, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz - 2.0f, celda * 0.88f, 0.24f, 6.0f, 0.85f, 0.84f, 0.82f, 0.0f);
        r.caja(px, 0.01f, pz + 2.5f, celda * 0.88f, 0.02f, 4.5f, 0.28f, 0.28f, 0.30f, 0.0f);

        r.caja(px, 4.0f, pz - 2.0f, 8.8f, 7.8f, 5.5f, 0.95f, 0.95f, 0.96f, 0.0f);
        r.caja(px, 4.2f, pz + 0.8f, 8.9f, 0.8f, 0.2f, 0.15f, 0.45f, 0.85f, 0.0f); // Franja azul

        // Sirenas intermitentes
        float parpadeo = (float) Math.sin(tiempoGlobal * 12.0f);
        float em1 = parpadeo > 0 ? 1.0f : 0.2f;
        float em2 = parpadeo < 0 ? 1.0f : 0.2f;
        r.caja(px - 3.6f, 8.1f, pz + 0.6f, 0.5f, 0.5f, 0.5f, 1.0f, 0.1f, 0.1f, em1);
        r.caja(px + 3.6f, 8.1f, pz + 0.6f, 0.5f, 0.5f, 0.5f, 0.1f, 0.4f, 1.0f, em2);

        dibujarVentanasDiorama(r, px, pz - 2.0f, 7.8f, 5.5f, modoNoche);
        dibujarHidranteRojo(r, px + 4.2f, pz + 3.0f);
    }

    // ==============================================================
    // 9. PARQUES Y PLAZAS CON ARBOLEDA DIORAMA
    // ==============================================================
    public static void dibujarParque(RenderizadorOpenGL r, float px, float pz, int variante) {
        float celda = ConstantesCiudad.CELDA;
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.28f, 0.75f, 0.32f, 0.0f); // Césped suave
        r.caja(px, 0.05f, pz, celda * 0.90f, 0.10f, celda * 0.90f, 0.85f, 0.84f, 0.82f, 0.0f); // Acera

        // Senderos curvos de arena/cemento
        r.caja(px, 0.13f, pz, 1.8f, 0.02f, celda * 0.88f, 0.90f, 0.88f, 0.80f, 0.0f);
        r.caja(px, 0.13f, pz, celda * 0.88f, 0.02f, 1.8f, 0.90f, 0.88f, 0.80f, 0.0f);

        // Agrupación de árboles low-poly escalonados
        dibujarArbolDiorama(r, px - 2.8f, pz - 2.8f);
        dibujarArbolDiorama(r, px + 2.8f, pz - 2.8f);
        dibujarArbolDiorama(r, px - 2.8f, pz + 2.8f);
        dibujarArbolDiorama(r, px + 2.8f, pz + 2.8f);

        // Bancos de plaza
        dibujarBanco(r, px + 1.8f, pz, (float) (-Math.PI / 2.0));
        dibujarBanco(r, px - 1.8f, pz, (float) (Math.PI / 2.0));
    }

    // ==============================================================
    // 10. ELEMENTOS DECORATIVOS MODULARES (PACK LOW POLY)
    // ==============================================================

    // Árboles con copa en 2 o 3 esferas/cubos escalonados
    public static void dibujarArbolDiorama(RenderizadorOpenGL r, float x, float z) {
        // Tronco
        r.caja(x, 0.8f, z, 0.25f, 1.6f, 0.25f, 0.50f, 0.32f, 0.18f, 0.0f);
        // Copa de follaje poligonal en 2 pisos
        r.caja(x, 2.0f, z, 1.8f, 1.2f, 1.8f, 0.25f, 0.78f, 0.30f, 0.0f);
        r.cajaGirada(x, 2.0f, z, 1.6f, 1.0f, 1.6f, 0.20f, 0.72f, 0.26f, 0.0f, 0.785f);
        r.caja(x, 2.9f, z, 1.2f, 0.9f, 1.2f, 0.32f, 0.85f, 0.35f, 0.0f);
    }

    // Canecas de basura (Trashcans: Verde y Amarillo)
    public static void dibujarContenedoresBasura(RenderizadorOpenGL r, float x, float z) {
        r.caja(x - 0.3f, 0.45f, z, 0.45f, 0.90f, 0.45f, 0.15f, 0.65f, 0.25f, 0.0f); // Verde
        r.caja(x - 0.3f, 0.92f, z, 0.48f, 0.10f, 0.48f, 0.12f, 0.55f, 0.20f, 0.0f);

        r.caja(x + 0.3f, 0.45f, z, 0.45f, 0.90f, 0.45f, 0.95f, 0.75f, 0.10f, 0.0f); // Amarillo
        r.caja(x + 0.3f, 0.92f, z, 0.48f, 0.10f, 0.48f, 0.85f, 0.65f, 0.08f, 0.0f);
    }

    // Maceteros de flores rectangulares (Flowerbeds)
    public static void dibujarMaceteroConFlores(RenderizadorOpenGL r, float x, float z) {
        r.caja(x, 0.25f, z, 1.4f, 0.45f, 0.6f, 0.65f, 0.45f, 0.28f, 0.0f); // Jardinera de madera
        r.caja(x, 0.55f, z, 1.2f, 0.30f, 0.45f, 0.20f, 0.75f, 0.25f, 0.0f); // Arbusto
        // Flores
        r.caja(x - 0.35f, 0.72f, z, 0.2f, 0.12f, 0.2f, 0.95f, 0.2f, 0.4f, 0.0f);
        r.caja(x + 0.35f, 0.72f, z, 0.2f, 0.12f, 0.2f, 0.98f, 0.85f, 0.1f, 0.0f);
    }

    // Hidrante de incendios rojo (Fire Hydrant)
    public static void dibujarHidranteRojo(RenderizadorOpenGL r, float x, float z) {
        r.caja(x, 0.30f, z, 0.25f, 0.60f, 0.25f, 0.90f, 0.15f, 0.15f, 0.0f);
        r.caja(x, 0.45f, z, 0.42f, 0.15f, 0.18f, 0.90f, 0.15f, 0.15f, 0.0f);
    }

    // Bancos de plaza minimalistas
    private static void dibujarBanco(RenderizadorOpenGL r, float x, float z, float rot) {
        float fX = -(float) Math.sin(rot);
        float fZ =  (float) Math.cos(rot);
        float dX =  (float) Math.cos(rot);
        float dZ =  (float) Math.sin(rot);

        float[][] posPatas = { {-0.65f, 0.0f}, {0.65f, 0.0f} };
        for (float[] p : posPatas) {
            float px = x + dX * p[0];
            float pz = z + dZ * p[0];
            r.cajaGirada(px, 0.22f, pz, 0.08f, 0.44f, 0.45f, 0.25f, 0.25f, 0.28f, 0.0f, rot);
        }
        r.cajaGirada(x - fX * 0.08f, 0.44f, z - fZ * 0.08f, 1.50f, 0.06f, 0.22f, 0.88f, 0.65f, 0.35f, 0.0f, rot);
        r.cajaGirada(x - fX * 0.22f, 0.68f, z - fZ * 0.22f, 1.50f, 0.24f, 0.06f, 0.88f, 0.65f, 0.35f, 0.0f, rot);
    }

    // Semáforos viales del diorama
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

        if (ciclo < 3.2f) { vg = 1.0f; emV = 1.0f; }
        else if (ciclo < 4.4f) { ar = 1.0f; ag = 0.85f; emA = 1.0f; }
        else { rr = 1.0f; emR = 1.0f; }

        float dirCaraX = (float) Math.sin(rot);
        float dirCaraZ = -(float) Math.cos(rot);
        float distCara = 0.19f;
        float posX = x + dirCaraX * distCara;
        float posZ = z + dirCaraZ * distCara;

        r.cajaGirada(posX, y + 0.34f, posZ, 0.18f, 0.18f, 0.05f, rr, rg, rb, emR, rot);
        r.cajaGirada(posX, y,         posZ, 0.18f, 0.18f, 0.05f, ar, ag, ab, emA, rot);
        r.cajaGirada(posX, y - 0.34f, posZ, 0.18f, 0.18f, 0.05f, vr, vg, vb, emV, rot);
    }

    // REEMPLAZA EL MÉTODO dibujarManzanaSubdividida EN EdificiosCiudad.java:
    public static void dibujarManzanaSubdividida(RenderizadorOpenGL r, float px, float pz, int f, int c, boolean modoNoche) {
        float celda = ConstantesCiudad.CELDA;
        // Acera base perimetral
        r.caja(px, 0.12f, pz, celda * 0.88f, 0.24f, celda * 0.88f, 0.85f, 0.84f, 0.82f, 0.0f);
        r.caja(px, 0.05f, pz, celda * 0.90f, 0.10f, celda * 0.90f, 0.35f, 0.35f, 0.38f, 0.0f);

        // 4 Casas residenciales estilizadas con profundidad, tejas y porches
        float[][] posCasas = { {-2.3f, -2.3f}, {2.3f, -2.3f}, {-2.3f, 2.3f}, {2.3f, 2.3f} };
        float[][] colTechos = {
            {0.88f, 0.28f, 0.20f}, // Teja roja
            {0.22f, 0.45f, 0.65f}, // Azul pizarra
            {0.85f, 0.55f, 0.18f}, // Naranja terracota
            {0.32f, 0.55f, 0.35f}  // Verde bosque
        };

        for (int k = 0; k < 4; k++) {
            float cx = px + posCasas[k][0];
            float cz = pz + posCasas[k][1];
            float[] ct = colTechos[k];

            // 1. Cuerpo principal de la casa (blanco hueso cálido)
            r.caja(cx, 1.8f, cz, 3.4f, 3.2f, 3.4f, 0.96f, 0.95f, 0.94f, 0.0f);

            // 2. Tejado escalonado con alero voladizo
            r.caja(cx, 3.55f, cz, 3.8f, 0.35f, 3.8f, ct[0], ct[1], ct[2], 0.0f);
            r.caja(cx, 3.90f, cz, 3.2f, 0.40f, 3.2f, ct[0] * 0.9f, ct[1] * 0.9f, ct[2] * 0.9f, 0.0f);
            r.caja(cx, 4.25f, cz, 2.2f, 0.35f, 2.2f, ct[0] * 0.8f, ct[1] * 0.8f, ct[2] * 0.8f, 0.0f);

            // 3. Chimenea de ladrillo
            r.caja(cx + 0.8f, 4.2f, cz - 0.7f, 0.45f, 1.2f, 0.45f, 0.75f, 0.30f, 0.20f, 0.0f);

            // 4. Puerta frontal y porche
            float pzCara = cz + 1.72f;
            r.caja(cx, 0.9f, pzCara, 0.8f, 1.6f, 0.08f, 0.45f, 0.25f, 0.12f, 0.0f); // Puerta madera
            r.caja(cx, 1.85f, pzCara + 0.25f, 1.4f, 0.12f, 0.6f, ct[0], ct[1], ct[2], 0.0f); // Alero de puerta

            // 5. Ventanas con marco y relieve
            dibujarVentanasDiorama(r, cx, cz, 3.2f, 3.4f, modoNoche);
        }

        // Árbol central de manzana
        dibujarArbolDiorama(r, px, pz);
    }

    public static void dibujarVentanasDiorama(RenderizadorOpenGL r, float px, float pz, float h, float anchoE, boolean modoNoche) {
        float emision = modoNoche ? 1.0f : 0.0f;
        float cr = modoNoche ? 1.0f : 0.25f;
        float cg = modoNoche ? 0.90f : 0.55f;
        float cb = modoNoche ? 0.40f : 0.75f;

        for (float y = 2.0f; y < h - 0.8f; y += 1.8f) {
            r.caja(px, y, pz + anchoE / 2.0f + 0.02f, anchoE * 0.65f, 0.7f, 0.04f, cr, cg, cb, emision);
            r.caja(px, y, pz - anchoE / 2.0f - 0.02f, anchoE * 0.65f, 0.7f, 0.04f, cr, cg, cb, emision);
            r.caja(px + anchoE / 2.0f + 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.65f, cr, cg, cb, emision);
            r.caja(px - anchoE / 2.0f - 0.02f, y, pz, 0.04f, 0.7f, anchoE * 0.65f, cr, cg, cb, emision);
        }
    }
}