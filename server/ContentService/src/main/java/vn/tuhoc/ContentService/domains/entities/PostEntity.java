package vn.tuhoc.ContentService.domains.entities;

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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostModerationStatusConverter;
import vn.tuhoc.ContentService.domains.converters.PostStatusConverter;
import vn.tuhoc.ContentService.domains.converters.PostTypeConverter;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "PostEntity.addAll", attributeNodes = {
        @NamedAttributeNode("stats"),
        @NamedAttributeNode("setting"),
        @NamedAttributeNode("medias"),
})
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(10)", unique = true, nullable = false)
    String publicId;

    @Column(columnDefinition = "INT", nullable = false)
    Integer userId;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(columnDefinition = "VARCHAR(7)", nullable = false)
    @Convert(converter = PostTypeConverter.class)
    PostTypeEnum type;

    @Column(columnDefinition = "TEXT", nullable = false)
    String caption;

    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    @Convert(converter = PostModerationStatusConverter.class)
    PostModerationStatusEnum moderationStatus;

    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    @Convert(converter = PostStatusConverter.class)
    PostStatusEnum status;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    PostStatsEntity stats;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    PostSettingEntity setting;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<PostMediaEntity> medias;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<PostTaggedUserEntity> taggedUsers;
}
