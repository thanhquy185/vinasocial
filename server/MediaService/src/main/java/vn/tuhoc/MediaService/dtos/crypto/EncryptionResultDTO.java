package vn.tuhoc.MediaService.dtos.crypto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EncryptionResultDTO {
    /**
     * File sau khi AES-GCM encrypt
     */
    byte[] ciphertext;

    /**
     * AES-256 key
     *
     * Đây là plaintext AES key,
     * chỉ tồn tại trong memory trong quá trình xử lý.
     */
    byte[] key;

    /**
     * AES-GCM IV
     */
    byte[] iv;
}
