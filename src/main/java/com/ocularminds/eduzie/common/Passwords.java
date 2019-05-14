package com.ocularminds.eduzie.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class Passwords {

    String password;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public Passwords(BCryptPasswordEncoder bCryptPasswordEncoder, String password) {
        this.password = password;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String hash() {
        return bCryptPasswordEncoder.encode(this.password);
    }

    public boolean verify(String hash) {
        if (hash == null) {
            return false;
        }
        return bCryptPasswordEncoder.matches(this.password, hash);
    }
}
