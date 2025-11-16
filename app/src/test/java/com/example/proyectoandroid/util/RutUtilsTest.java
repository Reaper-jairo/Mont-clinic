package com.example.proyectoandroid.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitarios para RutUtils
 */
public class RutUtilsTest {

    @Test
    public void testNormalizarRut() {
        assertEquals("123456789", RutUtils.normalizarRut("12.345.678-9"));
        assertEquals("12345678K", RutUtils.normalizarRut("12.345.678-K"));
        assertEquals("123456789", RutUtils.normalizarRut("123456789"));
        assertEquals("", RutUtils.normalizarRut(null));
    }

    @Test
    public void testEsRutValidoSinGuion() {
        // RUTs válidos en formato
        assertTrue(RutUtils.esRutValidoSinGuion("123456789"));
        assertTrue(RutUtils.esRutValidoSinGuion("12345678K"));
        assertTrue(RutUtils.esRutValidoSinGuion("19875613K"));
        
        // RUTs inválidos
        assertFalse(RutUtils.esRutValidoSinGuion("1234567")); // Muy corto
        assertFalse(RutUtils.esRutValidoSinGuion("1234567890")); // Muy largo
        assertFalse(RutUtils.esRutValidoSinGuion("12345678X")); // DV inválido
        assertFalse(RutUtils.esRutValidoSinGuion(null));
        assertFalse(RutUtils.esRutValidoSinGuion(""));
    }
}

