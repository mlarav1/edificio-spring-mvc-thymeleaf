package co.edu.unicartagena.edificios.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTokenTest {
    @Test
    void elHashEsSha256DeSesentaYCuatroCaracteresYNoRevelaElToken() {
        String token = AuthService.generarToken();
        String hash = AuthService.hash(token);
        assertEquals(64, hash.length());
        assertNotEquals(token, hash);
        assertEquals(hash, AuthService.hash(token));
    }

    @Test
    void cadaTokenEsDistinto() {
        assertNotEquals(AuthService.generarToken(), AuthService.generarToken());
    }
}