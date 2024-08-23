package com.example.imagecontainer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Table("images")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ImageEntity {
    @Id
    private Long id;
    @Column("image_data")
    private byte[] imageData;
    @Column("created_at")
    private Instant createdAt;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ImageEntity that = (ImageEntity) object;
        return Arrays.equals(imageData, that.imageData) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(createdAt);
        result = 31 * result + Arrays.hashCode(imageData);
        return result;
    }
}