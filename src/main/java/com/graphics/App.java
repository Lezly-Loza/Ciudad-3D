package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class App {
    public static void main(String[] args) {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }

        long window = GLFW.glfwCreateWindow(800, 600, "Ciudad Interactiva 3D - Setup OK", 0, 0);
        if (window == 0) {
            GLFW.glfwTerminate();
            throw new RuntimeException("Error al crear la ventana GLFW");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Bucle básico
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}