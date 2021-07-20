package com.shopwise.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {

    @Test
    public void testEncodingPassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "12345678";

        String encodedPassword = encoder.encode(rawPassword);
        boolean matches = encoder.matches(rawPassword,encodedPassword);

        assertThat(matches).isTrue();
    }
}
