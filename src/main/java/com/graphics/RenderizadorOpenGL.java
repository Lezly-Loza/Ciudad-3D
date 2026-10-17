package com.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class RenderizadorOpenGL {
    private int programa;
    private int vao;
    private int vbo;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public void inicializar() {
        crearPrograma();
        crearCubo();
    }

    public void usarPrograma() {
        GL20.glUseProgram(programa);
        GL30.glBindVertexArray(vao);
    }

    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz,
                           float r, float g, float b, float emision, float angulo) {
        vector("uPos", x, y, z);
        vector("uEscala", sx, sy, sz);
        vector("uColor", r, g, b);
        decimal("uEmision", emision);
        decimal("uGiro", angulo);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
    }

    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float emision) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, emision, 0.0f);
    }

    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0.0f, 0.0f);
    }

    public void vector(String nombre, float x, float y, float z) {
        GL20.glUniform3f(obtenerUniform(nombre), x, y, z);
    }

    public void decimal(String nombre, float valor) {
        GL20.glUniform1f(obtenerUniform(nombre), valor);
    }

    private int obtenerUniform(String nombre) {
        return uniforms.computeIfAbsent(nombre, k -> GL20.glGetUniformLocation(programa, k));
    }

    private void crearCubo() {
        float[] v = {
            -0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f, 0.5f, 0.5f,  0, 0, 1,
            -0.5f,-0.5f, 0.5f,  0, 0, 1,   0.5f, 0.5f, 0.5f,  0, 0, 1,  -0.5f, 0.5f, 0.5f,  0, 0, 1,
            -0.5f,-0.5f,-0.5f,  0, 0,-1,   0.5f, 0.5f,-0.5f,  0, 0,-1,   0.5f,-0.5f,-0.5f,  0, 0,-1,
            -0.5f,-0.5f,-0.5f,  0, 0,-1,  -0.5f, 0.5f,-0.5f,  0, 0,-1,   0.5f, 0.5f,-0.5f,  0, 0,-1,
            -0.5f,-0.5f,-0.5f, -1, 0, 0,  -0.5f,-0.5f, 0.5f, -1, 0, 0,  -0.5f, 0.5f, 0.5f, -1, 0, 0,
            -0.5f,-0.5f,-0.5f, -1, 0, 0,  -0.5f, 0.5f, 0.5f, -1, 0, 0,  -0.5f, 0.5f,-0.5f, -1, 0, 0,
             0.5f,-0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f, 0.5f,  1, 0, 0,   0.5f,-0.5f, 0.5f,  1, 0, 0,
             0.5f,-0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f,-0.5f,  1, 0, 0,   0.5f, 0.5f, 0.5f,  1, 0, 0,
            -0.5f, 0.5f,-0.5f,  0, 1, 0,  -0.5f, 0.5f, 0.5f,  0, 1, 0,   0.5f, 0.5f, 0.5f,  0, 1, 0,
            -0.5f, 0.5f,-0.5f,  0, 1, 0,   0.5f, 0.5f, 0.5f,  0, 1, 0,   0.5f, 0.5f,-0.5f,  0, 1, 0,
            -0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f, 0.5f,  0,-1, 0,  -0.5f,-0.5f, 0.5f,  0,-1, 0,
            -0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f,-0.5f,  0,-1, 0,   0.5f,-0.5f, 0.5f,  0,-1, 0
        };

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

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

            uniform vec3 uOjo;
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
                    FragColor = vec4(uColor * 1.5, 1.0);
                    return;
                }

                vec3 N = normalize(vNormal);
                float diffDir = max(dot(N, normalize(uLuzDir)), 0.0);
                vec3 iluminacion = uLuzAmbiente + (uLuzDirColor * diffDir);

                for (int i = 0; i < 9; i++) {
                    vec3 lDir = uFarolasPos[i] - vMundo;
                    float dist = length(lDir);
                    if (dist < 65.0) {
                        float att = 1.0 / (1.0 + 0.035 * dist + 0.0055 * dist * dist);
                        float diffPunto = max(dot(N, normalize(lDir)), 0.0);
                        vec3 colorFarolaVial = (uEsNoche > 0.5) ? vec3(1.0, 0.96, 0.82) * 5.8 : vec3(0.9, 0.9, 0.8) * 0.6;
                        iluminacion += colorFarolaVial * diffPunto * att;
                    }
                }

                if (uFarosActivos > 0.5) {
                    vec3 dirFaroNorm = normalize(uFaroDir);

                    vec3 d1 = uFaro1Pos - vMundo;
                    float dist1 = length(d1);
                    vec3 d1N = normalize(-d1);
                    float theta1 = dot(d1N, dirFaroNorm);
                    if (theta1 > 0.80 && dist1 < 45.0) {
                        float att1 = (theta1 - 0.80) / 0.20 * (1.0 / (1.0 + 0.04 * dist1 + 0.01 * dist1 * dist1));
                        float diff1 = max(dot(N, normalize(d1)), 0.0);
                        iluminacion += vec3(1.0, 1.0, 0.94) * diff1 * att1 * 4.0;
                    }

                    vec3 d2 = uFaro2Pos - vMundo;
                    float dist2 = length(d2);
                    vec3 d2N = normalize(-d2);
                    float theta2 = dot(d2N, dirFaroNorm);
                    if (theta2 > 0.80 && dist2 < 45.0) {
                        float att2 = (theta2 - 0.80) / 0.20 * (1.0 / (1.0 + 0.04 * dist2 + 0.01 * dist2 * dist2));
                        float diff2 = max(dot(N, normalize(d2)), 0.0);
                        iluminacion += vec3(1.0, 1.0, 0.94) * diff2 * att2 * 4.0;
                    }
                }

                vec3 colFinal = uColor * iluminacion;
                float dCam = length(vMundo - uOjo);
                float factorNiebla = clamp((dCam - 150.0) / 200.0, 0.0, 0.45);
                vec3 colCielo = (uEsNoche > 0.5) ? vec3(0.010, 0.010, 0.025) : vec3(0.55, 0.72, 0.92);
                colFinal = mix(colFinal, colCielo, factorNiebla);

                FragColor = vec4(colFinal, 1.0);
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

    public void limpiar() {
        if (vbo != 0) GL15.glDeleteBuffers(vbo);
        if (vao != 0) GL30.glDeleteVertexArrays(vao);
        if (programa != 0) GL20.glDeleteProgram(programa);
    }
}