package com.hrms.common.entity;

import com.hrms.common.util.AesEncryptConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AesEncryptConverterTest {

    private AesEncryptConverter converter;

    @BeforeEach
    void setUp() {
        converter = new AesEncryptConverter("test-encryption-key-32-chars-ok!");
    }

    @Test
    void roundTrip_encryptThenDecrypt_returnsOriginal() {
        String original = "123456789012345678";
        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void roundTrip_withVietnameseText_returnsOriginal() {
        String original = "Nguyễn Văn Quản trị viên";
        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void nullInput_encryptReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void nullInput_decryptReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void ivRandomness_sameTextProducesDifferentCiphertext() {
        String plaintext = "same-cccd-number";
        String cipher1 = converter.convertToDatabaseColumn(plaintext);
        String cipher2 = converter.convertToDatabaseColumn(plaintext);
        assertThat(cipher1).isNotEqualTo(cipher2);
    }

    @Test
    void encrypted_isBase64() {
        String encrypted = converter.convertToDatabaseColumn("test");
        // Base64 chars only: A-Z, a-z, 0-9, +, /, =
        assertThat(encrypted).matches("^[A-Za-z0-9+/]+=*$");
    }

    @Test
    void wrongKey_throwsOnDecrypt() {
        String encrypted = converter.convertToDatabaseColumn("secret");
        AesEncryptConverter wrongKeyConverter = new AesEncryptConverter("wrong-key-32-characters-padding!!");
        assertThatThrownBy(() -> wrongKeyConverter.convertToEntityAttribute(encrypted))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Decryption failed");
    }
}
