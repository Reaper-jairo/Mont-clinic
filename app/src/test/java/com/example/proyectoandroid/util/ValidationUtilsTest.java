package com.example.proyectoandroid.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitarios para ValidationUtils
 */
public class ValidationUtilsTest {

    @Test
    public void testEsEmailValido_Correcto() {
        assertTrue(ValidationUtils.esEmailValido("test@example.com"));
        assertTrue(ValidationUtils.esEmailValido("usuario.nombre@dominio.cl"));
    }

    @Test
    public void testEsEmailValido_Incorrecto() {
        assertFalse(ValidationUtils.esEmailValido("email-invalido"));
        assertFalse(ValidationUtils.esEmailValido("@example.com"));
        assertFalse(ValidationUtils.esEmailValido("test@"));
        assertFalse(ValidationUtils.esEmailValido(null));
        assertFalse(ValidationUtils.esEmailValido(""));
    }

    @Test
    public void testEsPasswordValida() {
        assertTrue(ValidationUtils.esPasswordValida("123456", 6));
        assertTrue(ValidationUtils.esPasswordValida("password123", 6));
        assertFalse(ValidationUtils.esPasswordValida("12345", 6));
        assertFalse(ValidationUtils.esPasswordValida(null, 6));
    }

    @Test
    public void testEsNombreValido() {
        assertTrue(ValidationUtils.esNombreValido("Juan Pérez"));
        assertTrue(ValidationUtils.esNombreValido("María José"));
        assertFalse(ValidationUtils.esNombreValido("A"));
        assertFalse(ValidationUtils.esNombreValido("123"));
        assertFalse(ValidationUtils.esNombreValido(null));
        assertFalse(ValidationUtils.esNombreValido(""));
    }
}

