package vn.tuhoc.ContentService.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.clients.MediaClient;
import vn.tuhoc.ContentService.clients.UserClient;
import vn.tuhoc.ContentService.domains.entities.PostEntity;
import vn.tuhoc.ContentService.domains.entities.PostEntity_;
import vn.tuhoc.ContentService.domains.entities.PostMediaEntity;
import vn.tuhoc.ContentService.domains.entities.PostTaggedUserEntity;
import vn.tuhoc.ContentService.domains.entities.PostSettingEntity;
import vn.tuhoc.ContentService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;
import vn.tuhoc.ContentService.domains.mappers.PostMapper;
import vn.tuhoc.ContentService.dtos.clients.MediaValidReqClientDTO;
import vn.tuhoc.ContentService.dtos.requests.PostCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostMediaCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostSettingCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostStatsCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostMediaDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSettingDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostStatsDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostTaggedUserResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.ProfilePostResponseDTO;
import vn.tuhoc.ContentService.exceptions.PostMediaInvalidException;
import vn.tuhoc.ContentService.exceptions.PostTagInvalidException;
import vn.tuhoc.ContentService.exceptions.PostNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.PostNotFoundByPublicIdException;
import vn.tuhoc.ContentService.repositories.PostRepository;
import vn.tuhoc.ContentService.repositories.criteria.PostCriteria;
import vn.tuhoc.ContentService.repositories.specifications.PostSpecification;
import vn.tuhoc.ContentService.services.interfaces.PostMediaService;
import vn.tuhoc.ContentService.services.interfaces.PostService;
import vn.tuhoc.ContentService.services.interfaces.PostSettingService;
import vn.tuhoc.ContentService.services.interfaces.PostStatsService;
import vn.tuhoc.ContentService.services.interfaces.ProducerService;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.ContentService.dtos.clients.UserProfileVisibilityResClientDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.PublicIdGeneratorUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImplement implements PostService {
        UserClient userClient;
        MediaClient mediaClient;
        PostStatsService postStatsService;
        PostSettingService postSettingService;
        PostMediaService postMediaService;
        ProducerService producerService;
        PostRepository postRepository;
        PostMapper postMapper;

        private void applyAuthorSearch(PostCriteria postCriteria) {
                if (ValidationUtil.isNull(postCriteria.getUsername())) {
                        return;
                }

                String username = postCriteria.getUsername()
                        .filter(ValidationUtil::hasText)
                        .map(String::trim)
                        .map(value -> value.startsWith("@") ? value.substring(1) : value)
                        .orElse(null);

                if (username == null) {
                        return;
                }

                var profileResponse = this.userClient.getPublicProfile(username);
                Integer userId = profileResponse == null || profileResponse.getData() == null
                        ? null
                        : profileResponse.getData().getUserId();

                postCriteria.setUserId(Optional.of(userId == null ? "0" : userId.toString()));
        }

        private Map<String, MediaInfoResClientDTO> getMediaInfoMap(List<String> mediaIds) {
                if (mediaIds.isEmpty()) {
                        return Map.of();
                }

                return this.mediaClient.getInfo(
                                MediaInfoReqClientDTO.builder()
                                                .mediaIds(mediaIds)
                                                .build())
                                .stream()
                                .filter(media -> ValidationUtil.nonNull(media.getId()))
                                .collect(Collectors.toMap(
                                                MediaInfoResClientDTO::getId,
                                                Function.identity(),
                                                (existing, replacement) -> existing));
        }

        private MediaPurposeEnum getMediaPurpose(PostTypeEnum postType) {
                return postType == PostTypeEnum.REEL
                                ? MediaPurposeEnum.REEL
                                : MediaPurposeEnum.POST;
        }

        private void validatePostMedia(
                        Integer userId,
                        PostTypeEnum postType,
                        List<String> mediaIds) {
                if (ValidationUtil.isNull(mediaIds) || mediaIds.isEmpty()) {
                        throw new PostMediaInvalidException();
                }

                if (postType == PostTypeEnum.REEL && mediaIds.size() != 1) {
                        throw new PostMediaInvalidException();
                }

                MediaValidReqClientDTO mediaValidReqClientDTO = MediaValidReqClientDTO.builder()
                                .userId(userId)
                                .mediaPurpose(this.getMediaPurpose(postType))
                                .mediaIds(mediaIds)
                                .build();
                if (!Boolean.TRUE.equals(this.mediaClient.validateMedia(mediaValidReqClientDTO))) {
                        throw new PostMediaInvalidException();
                }

                if (postType == PostTypeEnum.REEL) {
                        MediaInfoResClientDTO media = this.getMediaInfoMap(mediaIds).get(mediaIds.get(0));
                        if (media == null || !"Video".equals(media.getType())) {
                                throw new PostMediaInvalidException();
                        }
                }
        }

        private void enrichPostMedias(
                        PostEntity postEntity,
                        PostDetailResponseDTO postDetailResponseDTO,
                        Map<String, MediaInfoResClientDTO> mediaMap) {
                postDetailResponseDTO.getMedias().forEach(postMedia -> {
                        PostMediaEntity entity = postEntity.getMedias().stream()
                                        .filter(item -> item.getId().equals(postMedia.getId()))
                                        .findFirst()
                                        .orElse(null);

                        if (entity != null) {
                                postMedia.setMedia(mediaMap.get(entity.getMediaId()));
                        }
                });
        }

        private Map<Integer, UserInfoResClientDTO> getUserInfoMap(List<Integer> userIds) {
                if (userIds.isEmpty()) {
                        return Map.of();
                }
                return this.userClient.getInfo(UserInfoReqClientDTO.builder().userIds(userIds).build()).stream()
                                .filter(user -> user.getId() != null)
                                .collect(Collectors.toMap(UserInfoResClientDTO::getId, Function.identity(), (a, b) -> a));
        }

        private List<PostTaggedUserResponseDTO> enrichTaggedUsers(
                        List<PostTaggedUserEntity> taggedUsers,
                        Map<Integer, UserInfoResClientDTO> users) {
                if (taggedUsers == null || taggedUsers.isEmpty()) {
                        return List.of();
                }
                return taggedUsers.stream().map(tag -> new PostTaggedUserResponseDTO(
                                tag.getUserId(), users.get(tag.getUserId())))
                                .filter(tag -> tag.getUser() != null)
                                .toList();
        }

        private List<PostDetailResponseDTO> enrichPostDetails(List<PostEntity> postEntities) {
                if (postEntities.isEmpty()) {
                        return List.of();
                }
                List<Integer> userIds = new ArrayList<>();
                List<String> mediaIds = new ArrayList<>();
                for (PostEntity post : postEntities) {
                        if (post.getUserId() != null) userIds.add(post.getUserId());
                        if (post.getTaggedUsers() != null) {
                                post.getTaggedUsers().stream().map(PostTaggedUserEntity::getUserId)
                                                .filter(Objects::nonNull).forEach(userIds::add);
                        }
                        if (post.getMedias() != null) {
                                post.getMedias().stream().map(PostMediaEntity::getMediaId)
                                                .filter(ValidationUtil::nonNull).forEach(mediaIds::add);
                        }
                }
                Map<Integer, UserInfoResClientDTO> users = this.getUserInfoMap(userIds.stream().distinct().toList());
                Map<String, MediaInfoResClientDTO> media = this.getMediaInfoMap(mediaIds.stream().distinct().toList());
                return postEntities.stream().map(post -> {
                        PostDetailResponseDTO response = this.postMapper.entityToDetailResponse(post);
                        response.setUser(users.get(post.getUserId()));
                        this.enrichPostMedias(post, response, media);
                        response.setTaggedUsers(this.enrichTaggedUsers(post.getTaggedUsers(), users));
                        return response;
                }).toList();
        }

        private PostDetailResponseDTO enrichPostDetail(PostEntity postEntity) {
                return this.enrichPostDetails(List.of(postEntity)).get(0);
        }

        private List<PostTaggedUserEntity> createTaggedUsers(PostEntity post, List<Integer> requestedUserIds) {
                if (requestedUserIds == null || requestedUserIds.isEmpty()) {
                        return new ArrayList<>();
                }
                if (requestedUserIds.stream().anyMatch(Objects::isNull)) {
                        throw new PostTagInvalidException("Danh sách tài khoản được gắn thẻ không hợp lệ.");
                }
                List<Integer> userIds = requestedUserIds.stream().distinct().toList();
                if (userIds.size() != requestedUserIds.size()) {
                        throw new PostTagInvalidException("Danh sách tài khoản được gắn thẻ không hợp lệ.");
                }
                List<Integer> foundUserIds = this.userClient.getInfo(
                                UserInfoReqClientDTO.builder().userIds(userIds).build()).stream()
                                .map(UserInfoResClientDTO::getId)
                                .filter(Objects::nonNull).distinct().toList();
                if (foundUserIds.size() != userIds.size()) {
                        throw new PostTagInvalidException("Không tìm thấy một hoặc nhiều tài khoản được gắn thẻ.");
                }
                return userIds.stream().map(userId -> PostTaggedUserEntity.builder()
                                .post(post).userId(userId).build()).toList();
        }

        private PostEntity getById(Integer id) {
                return this.postRepository.findById(id)
                                .orElseThrow(() -> new PostNotFoundByIdException(id));
        }

        private PostEntity getByPublicId(String publicId) {
                return this.postRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new PostNotFoundByPublicIdException(publicId));
        }

        private Page<PostEntity> getAll(PostCriteria postCriteria) {
                this.applyAuthorSearch(postCriteria);

                Sort sort = Sort.unsorted();
                if (ValidationUtil.nonNull(postCriteria.getSort())) {
                        String sortString = postCriteria.getSort().filter(ValidationUtil::hasText).get();
                        switch (sortString) {
                                case "Mã tăng dần" -> sort = Sort.by(PostEntity_.ID).ascending();
                                case "Mã giảm dần" -> sort = Sort.by(PostEntity_.ID).descending();
                                case "Mã công khai tăng dần" -> sort = Sort.by(PostEntity_.PUBLIC_ID).ascending();
                                case "Mã công khai giảm dần" -> sort = Sort.by(PostEntity_.PUBLIC_ID).descending();
                                case "Ngày tạo tăng dần" -> sort = Sort.by(PostEntity_.CREATED_AT).ascending();
                                case "Ngày tạo giảm dần" -> sort = Sort.by(PostEntity_.CREATED_AT).descending();
                        }
                }

                Pageable pageable = Pageable.unpaged(sort);
                if (ValidationUtil.nonNull(postCriteria.getPage())
                                && ValidationUtil.nonNull(postCriteria.getSize())) {
                        pageable = PageRequest.of(
                                        postCriteria.getPage(),
                                        postCriteria.getSize(),
                                        sort);
                }

                Specification<PostEntity> specification = PostSpecification.filterPosts(postCriteria);

                return this.postRepository.findAll(specification, pageable);
        }

        private boolean hasPostChanges(PostEntity post, PostUpdateRequestDTO request, String caption) {
                PostSettingEntity setting = post.getSetting();
                List<String> currentMediaIds = post.getMedias() == null
                                ? List.of()
                                : post.getMedias().stream().map(PostMediaEntity::getMediaId).sorted().toList();
                List<String> requestedMediaIds = request.getPostMediaIds().stream().sorted().toList();

                return !Objects.equals(post.getCaption(), caption)
                                || !Objects.equals(setting.getPrivacy(), request.getPostSettingPrivacy())
                                || !Objects.equals(setting.getIsPinned(), request.getPostSettingIsPinned())
                                || !Objects.equals(setting.getAllowComment(), request.getPostSettingAllowComment())
                                || !Objects.equals(setting.getAllowReup(), request.getPostSettingAllowReup())
                                || !Objects.equals(setting.getAllowShare(), request.getPostSettingAllowShare())
                                || !Objects.equals(setting.getAllowSave(), request.getPostSettingAllowSave())
                                || !Objects.equals(setting.getShowViewCount(), request.getPostSettingShowViewCount())
                                || !Objects.equals(setting.getShowLikeCount(), request.getPostSettingShowLikeCount())
                                || !Objects.equals(setting.getShowCommentCount(), request.getPostSettingShowCommentCount())
                                || !Objects.equals(setting.getShowReupCount(), request.getPostSettingShowReupCount())
                                || !Objects.equals(setting.getShowShareCount(), request.getPostSettingShowShareCount())
                                || !Objects.equals(setting.getShowSaveCount(), request.getPostSettingShowSaveCount())
                                || !Objects.equals(currentMediaIds, requestedMediaIds);
        }

        private void publishPostStatusChanged(PostEntity post) {
                String message = switch (post.getStatus()) {
                        case PUBLICLY -> "Bài viết của bạn đã được hiển thị công khai.";
                        case HIDDEN -> "Bài viết của bạn đã bị ẩn.";
                        case REMOVED -> "Bài viết của bạn đã bị gỡ khỏi hệ thống.";
                };
                this.producerService.publish(
                                post.getUserId(),
                                "POST_STATUS_CHANGED",
                                "POST",
                                post.getId(),
                                "Trạng thái bài viết đã thay đổi",
                                message,
                                Map.of(
                                                "postId", post.getId(),
                                                "status", post.getStatus().name(),
                                                "route", "/posts/" + post.getId()));
        }

        @Override
        public PostDetailResponseDTO handleGetById(Integer id) {
                PostEntity post = this.getById(id);
                Integer viewerId = SecurityUtil.getCurrentUserId().orElse(null);
                boolean isOwner = viewerId != null && viewerId.equals(post.getUserId());
                boolean isPublicPost = post.getStatus() == PostStatusEnum.PUBLICLY
                                && post.getModerationStatus() == PostModerationStatusEnum.NONE
                                && post.getSetting() != null
                                && post.getSetting().getPrivacy() == PostSettingPrivacyEnum.EVERYONE;
                if (!isOwner && !isPublicPost) {
                        throw new PostNotFoundByIdException(id);
                }

                PostDetailResponseDTO response = this.enrichPostDetail(post);
                if (!isOwner && (response.getUser() == null
                                || !Boolean.TRUE.equals(response.getUser().getCanViewPosts()))) {
                        throw new PostNotFoundByIdException(id);
                }
                if (!isOwner) {
                        this.applyPublicStatsVisibility(response);
                }
                return response;
        }

        @Override
        public PostDetailResponseDTO handleGetByPublicId(String publicId) {
                return this.enrichPostDetail(this.getByPublicId(publicId));
        }

        @Override
        public PageResponseDTO<PostSummaryResponseDTO> handleGetSummary(PostCriteria postCriteria) {
                Page<PostEntity> page = this.getAll(postCriteria);

                List<Integer> userIds = page.getContent().stream()
                                .map(PostEntity::getUserId)
                                .filter(ValidationUtil::nonNull)
                                .distinct()
                                .toList();

                Map<Integer, UserInfoResClientDTO> userMap = userIds.isEmpty()
                                ? Map.of()
                                : this.userClient.getInfo(
                                                UserInfoReqClientDTO.builder()
                                                                .userIds(userIds)
                                                                .build())
                                                .stream()
                                                .filter(user -> ValidationUtil.nonNull(user.getId()))
                                                .collect(Collectors.toMap(
                                                                UserInfoResClientDTO::getId,
                                                                Function.identity()));

                Page<PostSummaryResponseDTO> responsePage = page.map(post -> {
                        PostSummaryResponseDTO postSummaryResponseDTO = this.postMapper.entityToSummaryResponse(post);
                        postSummaryResponseDTO.setUser(userMap.get(post.getUserId()));

                        return postSummaryResponseDTO;
                });

                return PageResponseUtil.convert(responsePage);
        }

        private void applyPublicStatsVisibility(PostDetailResponseDTO post) {
                if (post.getStats() == null || post.getSetting() == null) {
                        return;
                }
                var stats = post.getStats();
                var setting = post.getSetting();
                if (!Boolean.TRUE.equals(setting.getShowViewCount())) stats.setViewCount(0);
                if (!Boolean.TRUE.equals(setting.getShowLikeCount())) stats.setLikeCount(0);
                if (!Boolean.TRUE.equals(setting.getShowCommentCount())) stats.setCommentCount(0);
                if (!Boolean.TRUE.equals(setting.getShowReupCount())) stats.setReupCount(0);
                if (!Boolean.TRUE.equals(setting.getShowShareCount())) stats.setShareCount(0);
                if (!Boolean.TRUE.equals(setting.getShowSaveCount())) stats.setSaveCount(0);
        }

        @Override
        public PageResponseDTO<PostDetailResponseDTO> handleGetExplorePosts(
                        int page, int size, String query, Boolean reelsOnly) {
                int safePage = Math.max(page, 0);
                int safeSize = Math.max(1, Math.min(size, 60));
                String safeQuery = query == null || query.isBlank() ? null : query.trim();
                Pageable pageable = PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(
                                                Sort.Order.desc(PostEntity_.CREATED_AT),
                                                Sort.Order.desc(PostEntity_.ID)));
                Page<Integer> postIdsPage = this.postRepository.findExplorePostIds(
                                PostStatusEnum.PUBLICLY,
                                PostModerationStatusEnum.NONE,
                                PostSettingPrivacyEnum.EVERYONE,
                                safeQuery,
                                reelsOnly,
                                PostTypeEnum.REEL,
                                pageable);
                List<Integer> postIds = postIdsPage.getContent();
                Map<Integer, PostEntity> postsById = postIds.isEmpty()
                                ? Map.of()
                                : this.postRepository.findAllByIdIn(postIds).stream()
                                                .collect(Collectors.toMap(PostEntity::getId, Function.identity()));
                List<PostEntity> orderedPosts = postIds.stream()
                                .map(postsById::get)
                                .filter(Objects::nonNull)
                                .toList();
                List<PostDetailResponseDTO> posts = this.enrichPostDetails(orderedPosts).stream()
                                .filter(post -> post.getUser() != null
                                                && Boolean.TRUE.equals(post.getUser().getCanViewPosts()))
                                .toList();
                posts.forEach(this::applyPublicStatsVisibility);
                Page<PostDetailResponseDTO> responsePage = new PageImpl<>(
                                posts,
                                pageable,
                                postIdsPage.getTotalElements());

                return PageResponseUtil.convert(responsePage);
        }

        @Override
        public List<ProfilePostResponseDTO> handleGetProfilePosts(String username) {
                var profileResponse = this.userClient.getPublicProfile(username);
                UserProfileVisibilityResClientDTO profile = profileResponse == null ? null : profileResponse.getData();
                if (profile == null || profile.getUserId() == null) {
                        return List.of();
                }

                Integer viewerId = SecurityUtil.getCurrentUserId().orElse(null);
                boolean isOwner = profile.getUserId().equals(viewerId);
                if (!isOwner && !Boolean.TRUE.equals(profile.getCanViewPosts())) {
                        return List.of();
                }

                Specification<PostEntity> specification = (root, query, builder) -> {
                        var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                        predicates.add(builder.equal(root.get("userId"), profile.getUserId()));
                        predicates.add(builder.notEqual(root.get("moderationStatus"), PostModerationStatusEnum.REJECTED));
                        predicates.add(builder.notEqual(root.get("status"), PostStatusEnum.REMOVED));
                        if (!isOwner) {
                                predicates.add(builder.equal(root.get("status"), PostStatusEnum.PUBLICLY));
                                predicates.add(builder.equal(root.get("moderationStatus"), PostModerationStatusEnum.NONE));

                                Join<Object, Object> setting = root.join("setting", JoinType.INNER);
                                predicates.add(builder.equal(setting.get("privacy"), PostSettingPrivacyEnum.EVERYONE));
                        }

                        return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                };

                Page<PostEntity> posts = this.postRepository.findAll(
                                specification,
                                PageRequest.of(0, 60, Sort.by(PostEntity_.CREATED_AT).descending()));

                List<String> mediaIds = posts.getContent().stream()
                                .flatMap(post -> post.getMedias().stream())
                                .map(PostMediaEntity::getMediaId)
                                .filter(ValidationUtil::nonNull)
                                .distinct()
                                .toList();
                Map<String, MediaInfoResClientDTO> mediaMap = this.getMediaInfoMap(mediaIds);

                return posts.getContent().stream().map(post -> {
                        PostMediaEntity firstMedia = post.getMedias().stream().findFirst().orElse(null);
                        MediaInfoResClientDTO media = firstMedia == null ? null : mediaMap.get(firstMedia.getMediaId());

                        var stats = post.getStats();
                        var setting = post.getSetting();

                        return new ProfilePostResponseDTO(
                                        post.getId(),
                                        media == null ? null : media.getObjectKey(),
                                        post.getCaption(),
                                        post.getType() == null ? PostTypeEnum.MEDIA : post.getType(),
                                        stats == null ? 0 : stats.getLikeCount(),
                                        stats == null ? 0 : stats.getCommentCount(),
                                        stats == null ? 0 : stats.getViewCount(),
                                        setting != null && Boolean.TRUE.equals(setting.getIsPinned()),
                                        post.getMedias().size() > 1);
                }).toList();
        }

        @Override
        public List<PostDetailResponseDTO> handleGetTaggedPosts(String username) {
                var profileResponse = this.userClient.getPublicProfile(username);
                UserProfileVisibilityResClientDTO profile = profileResponse == null ? null : profileResponse.getData();
                if (profile == null || profile.getUserId() == null) {
                        return List.of();
                }
                Integer viewerId = SecurityUtil.getCurrentUserId().orElse(null);
                boolean isOwner = profile.getUserId().equals(viewerId);
                if (!isOwner && !Boolean.TRUE.equals(profile.getCanViewPosts())) {
                        return List.of();
                }

                Specification<PostEntity> specification = (root, query, builder) -> {
                        query.distinct(true);
                        Join<Object, Object> taggedUser = root.join("taggedUsers", JoinType.INNER);
                        var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                        predicates.add(builder.equal(taggedUser.get("userId"), profile.getUserId()));
                        predicates.add(builder.notEqual(root.get("moderationStatus"), PostModerationStatusEnum.REJECTED));
                        predicates.add(builder.notEqual(root.get("status"), PostStatusEnum.REMOVED));
                        Join<Object, Object> setting = root.join("setting", JoinType.INNER);
                        var publicPost = builder.and(
                                        builder.equal(root.get("status"), PostStatusEnum.PUBLICLY),
                                        builder.equal(root.get("moderationStatus"), PostModerationStatusEnum.NONE),
                                        builder.equal(setting.get("privacy"), PostSettingPrivacyEnum.EVERYONE));
                        if (viewerId == null) {
                                predicates.add(publicPost);
                        } else {
                                predicates.add(builder.or(publicPost, builder.equal(root.get("userId"), viewerId)));
                        }
                        return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                };
                return this.enrichPostDetails(this.postRepository.findAll(specification,
                                PageRequest.of(0, 60, Sort.by(PostEntity_.CREATED_AT).descending())).getContent());
        }

        @Override
        public PostDetailResponseDTO handleCreate(PostCreateRequestDTO postCreateRequestDTO) {
                Integer currentUserLoginId = SecurityUtil.getCurrentUserId().get();

                this.validatePostMedia(
                                currentUserLoginId,
                                postCreateRequestDTO.getType(),
                                postCreateRequestDTO.getPostMediaIds());

                PostEntity postEntity = this.postMapper.createEntityFromRequest(postCreateRequestDTO);
                postEntity.setCreatedAt(TimeUtil.getCurrentDatetime());
                postEntity.setUserId(currentUserLoginId);
                postEntity.setModerationStatus(PostModerationStatusEnum.NONE);
                postEntity.setStatus(PostStatusEnum.PUBLICLY);
                // Handle Public Id
                String publicId = PublicIdGeneratorUtil.generate("POST", 5);
                while (this.postRepository.existsByPublicId(publicId)) {
                        publicId = PublicIdGeneratorUtil.generate("POST", 5);
                }
                postEntity.setPublicId(publicId);
                // Handle Create
                PostEntity postCreated = this.postRepository.save(postEntity);

                postCreated.setTaggedUsers(this.createTaggedUsers(postCreated, postCreateRequestDTO.getTaggedUserIds()));
                postCreated = this.postRepository.save(postCreated);
                Integer createdPostId = postCreated.getId();

                // Create Stats
                PostStatsCreateRequestDTO postStatsCreateRequestDTO = PostStatsCreateRequestDTO.builder()
                                .postId(postCreated.getId())
                                .viewCount(0)
                                .likeCount(0)
                                .commentCount(0)
                                .reupCount(0)
                                .shareCount(0)
                                .saveCount(0)
                                .build();
                PostStatsDetailResponseDTO postStatsDetailResponseDTO = this.postStatsService
                                .handleCreate(postStatsCreateRequestDTO);
                // Create Setting
                PostSettingCreateRequestDTO postSettingCreateRequestDTO = PostSettingCreateRequestDTO.builder()
                                .postId(postCreated.getId())
                                .privacy(postCreateRequestDTO.getPostSettingPrivacy())
                                .isPinned(postCreateRequestDTO.getPostSettingIsPinned())
                                .allowComment(postCreateRequestDTO.getPostSettingAllowComment())
                                .allowReup(postCreateRequestDTO.getPostSettingAllowReup())
                                .allowShare(postCreateRequestDTO.getPostSettingAllowShare())
                                .allowSave(postCreateRequestDTO.getPostSettingAllowSave())
                                .showViewCount(postCreateRequestDTO.getPostSettingShowViewCount())
                                .showLikeCount(postCreateRequestDTO.getPostSettingShowLikeCount())
                                .showCommentCount(postCreateRequestDTO.getPostSettingShowCommentCount())
                                .showReupCount(postCreateRequestDTO.getPostSettingShowReupCount())
                                .showShareCount(postCreateRequestDTO.getPostSettingShowShareCount())
                                .showSaveCount(postCreateRequestDTO.getPostSettingShowSaveCount())
                                .build();
                PostSettingDetailResponseDTO postSettingDetailResponseDTO = this.postSettingService
                                .handleCreate(postSettingCreateRequestDTO);
                // Create Medias
                List<PostMediaCreateRequestDTO> postMediaCreateRequestDTOs = postCreateRequestDTO.getPostMediaIds()
                                .stream()
                                .map(postMediaId -> PostMediaCreateRequestDTO.builder()
                                                .postId(createdPostId)
                                                .mediaId(postMediaId)
                                                .build())
                                .toList();
                List<PostMediaDetailResponseDTO> postMediaDetailResponseDTOs = this.postMediaService
                                .handleCreateAll(postMediaCreateRequestDTOs);

                this.producerService.publishPostCreated(
                                postCreated.getUserId(),
                                postCreated.getId(),
                                postCreateRequestDTO.getPostSettingPrivacy().name());

                PostDetailResponseDTO postDetailResponseDTO = this.postMapper.entityToDetailResponse(postCreated);
                postDetailResponseDTO.setStats(postStatsDetailResponseDTO);
                postDetailResponseDTO.setSetting(postSettingDetailResponseDTO);
                postDetailResponseDTO.setMedias(postMediaDetailResponseDTOs);
                List<PostTaggedUserEntity> taggedUsers = postCreated.getTaggedUsers();
                Map<Integer, UserInfoResClientDTO> taggedUserMap = this.getUserInfoMap(taggedUsers == null
                                ? List.of()
                                : taggedUsers.stream()
                                                .map(PostTaggedUserEntity::getUserId)
                                                .filter(Objects::nonNull)
                                                .distinct()
                                                .toList());
                postDetailResponseDTO.setTaggedUsers(this.enrichTaggedUsers(taggedUsers, taggedUserMap));

                return postDetailResponseDTO;
        }

        @Override
        public PostDetailResponseDTO handleUpdateById(Integer id, PostUpdateRequestDTO request) {
                Integer currentUserId = SecurityUtil.getCurrentUserId()
                                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để sửa bài đăng."));
                PostEntity postEntity = this.getById(id);
                if (!currentUserId.equals(postEntity.getUserId())) {
                        throw new AccessDeniedException("Bạn chỉ có thể sửa bài đăng của chính mình.");
                }

                this.validatePostMedia(
                                currentUserId,
                                postEntity.getType(),
                                request.getPostMediaIds());

                String caption = request.getCaption() == null ? "" : request.getCaption();
                boolean hasChanges = this.hasPostChanges(postEntity, request, caption);
                postEntity.setCaption(caption);

                PostSettingEntity setting = postEntity.getSetting();
                setting.setPrivacy(request.getPostSettingPrivacy());
                setting.setIsPinned(request.getPostSettingIsPinned());
                setting.setAllowComment(request.getPostSettingAllowComment());
                setting.setAllowReup(request.getPostSettingAllowReup());
                setting.setAllowShare(request.getPostSettingAllowShare());
                setting.setAllowSave(request.getPostSettingAllowSave());
                setting.setShowViewCount(request.getPostSettingShowViewCount());
                setting.setShowLikeCount(request.getPostSettingShowLikeCount());
                setting.setShowCommentCount(request.getPostSettingShowCommentCount());
                setting.setShowReupCount(request.getPostSettingShowReupCount());
                setting.setShowShareCount(request.getPostSettingShowShareCount());
                setting.setShowSaveCount(request.getPostSettingShowSaveCount());

                List<PostMediaEntity> medias = postEntity.getMedias();
                if (medias == null) {
                        medias = new ArrayList<>();
                        postEntity.setMedias(medias);
                } else {
                        medias.clear();
                }
                for (String mediaId : request.getPostMediaIds()) {
                        medias.add(PostMediaEntity.builder()
                                        .post(postEntity)
                                        .mediaId(mediaId)
                                        .build());
                }

                if (request.getTaggedUserIds() != null) {
                        List<PostTaggedUserEntity> taggedUsers = postEntity.getTaggedUsers();
                        if (taggedUsers == null) {
                                taggedUsers = new ArrayList<>();
                                postEntity.setTaggedUsers(taggedUsers);
                        } else {
                                taggedUsers.clear();
                        }
                        taggedUsers.addAll(this.createTaggedUsers(postEntity, request.getTaggedUserIds()));
                }

                PostEntity savedPost = this.postRepository.save(postEntity);
                if (hasChanges
                                && savedPost.getStatus() == PostStatusEnum.PUBLICLY
                                && savedPost.getModerationStatus() != PostModerationStatusEnum.REJECTED) {
                        this.producerService.publishPostUpdated(
                                        savedPost.getUserId(),
                                        savedPost.getId(),
                                        setting.getPrivacy().name());
                }
                return this.enrichPostDetail(savedPost);
        }

        @Override
        public void handleRemove(Integer id) {
                Integer currentUserId = SecurityUtil.getCurrentUserId()
                                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để xóa bài đăng."));
                PostEntity postEntity = this.getById(id);
                if (!currentUserId.equals(postEntity.getUserId())) {
                        throw new AccessDeniedException("Bạn chỉ có thể xóa bài đăng của chính mình.");
                }

                postEntity.setStatus(PostStatusEnum.REMOVED);
                this.postRepository.save(postEntity);
        }

        @Override
        public PostDetailResponseDTO handleUpdateStatusById(
                        Integer id,
                        PostUpdateStatusRequestDTO request) {
                PostEntity postEntity = this.getById(id);
                PostStatusEnum previousStatus = postEntity.getStatus();
                postEntity.setStatus(request.getStatus());
                PostEntity savedPost = this.postRepository.save(postEntity);

                if (previousStatus != savedPost.getStatus()) {
                        this.publishPostStatusChanged(savedPost);
                }

                return this.enrichPostDetail(savedPost);
        }

        @Override
        public PostDetailResponseDTO handleUpdateModerationStatusById(
                        Integer id,
                        PostUpdateModerationStatusRequestDTO request) {
                PostEntity postEntity = this.getById(id);
                PostModerationStatusEnum previousStatus = postEntity.getModerationStatus();
                postEntity.setModerationStatus(request.getModerationStatus());
                PostEntity savedPost = this.postRepository.save(postEntity);
                if (previousStatus != request.getModerationStatus()) {
                        if (request.getModerationStatus() == PostModerationStatusEnum.REJECTED) {
                                this.producerService.publish(
                                                savedPost.getUserId(), "POST_REJECTED", "POST", savedPost.getId(),
                                                "Bài viết bị từ chối", "Bài viết của bạn đã bị từ chối kiểm duyệt.",
                                                Map.of("postId", savedPost.getId()));
                        } else if (request.getModerationStatus() == PostModerationStatusEnum.FLAGGED) {
                                this.producerService.publish(
                                                savedPost.getUserId(), "POST_FLAGGED", "POST", savedPost.getId(),
                                                "Bài viết đang được xem xét", "Bài viết của bạn đang được xem xét.",
                                                Map.of("postId", savedPost.getId()));
                        }
                }
                return this.enrichPostDetail(savedPost);
        }

}
