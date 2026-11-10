package vn.tuhoc.UserService.domains.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UsernameTypeConverter;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;

@Entity
@Table(name = "usernames")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsernameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(5)", nullable = false)
    @Convert(converter = UsernameTypeConverter.class)
    UsernameTypeEnum type;

    @Column(columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    String value;

    @Column(columnDefinition = "BIT(1)", nullable = false)
    Boolean isVerify;

    @Column(columnDefinition = "BIT(1)", nullable = false)
    Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @OneToMany(mappedBy = "username", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<AuthOtpEntity> authOtps;
}
