package vn.tuhoc.ContentService.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.clients.MediaClient;
import vn.tuhoc.ContentService.clients.RelationshipClient;
import vn.tuhoc.ContentService.clients.UserClient;
import vn.tuhoc.ContentService.domains.entities.StoryEntity;
import vn.tuhoc.ContentService.domains.entities.StoryHighlightEntity;
import vn.tuhoc.ContentService.domains.entities.StoryHighlightStoryEntity;
import vn.tuhoc.ContentService.domains.entities.StorySettingEntity;
import vn.tuhoc.ContentService.domains.entities.StoryStatsEntity;
import vn.tuhoc.ContentService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.ContentService.domains.entities.StoryEntity_;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;
import vn.tuhoc.ContentService.domains.mappers.StoryMapper;
import vn.tuhoc.ContentService.dtos.clients.UserProfileVisibilityResClientDTO;
import vn.tuhoc.ContentService.dtos.clients.MediaValidReqClientDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StorySettingUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryHighlightRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryStatsDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StorySummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryHighlightResponseDTO;
import vn.tuhoc.ContentService.exceptions.StoryNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.StoryHighlightInvalidException;
import vn.tuhoc.ContentService.exceptions.StoryHighlightNotFoundException;
import vn.tuhoc.ContentService.exceptions.PostMediaInvalidException;
import vn.tuhoc.ContentService.repositories.StoryRepository;
import vn.tuhoc.ContentService.repositories.StoryHighlightRepository;
import vn.tuhoc.ContentService.repositories.criteria.StoryCriteria;
import vn.tuhoc.ContentService.repositories.specifications.StorySpecification;
import vn.tuhoc.ContentService.services.interfaces.StoryService;
import vn.tuhoc.ContentService.services.interfaces.ProducerService;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.PublicIdGeneratorUtil;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryServiceImplement implements StoryService {
    UserClient userClient;
    MediaClient mediaClient;
    RelationshipClient relationshipClient;
    ProducerService producerService;
    StoryRepository storyRepository;
    StoryHighlightRepository storyHighlightRepository;
    StoryMapper storyMapper;

    private void applyAuthorSearch(StoryCriteria storyCriteria) {
        if (ValidationUtil.isNull(storyCriteria.getUsername())) {
            return;
        }

        String username = storyCriteria.getUsername()
                .filter(ValidationUtil::hasText)
                .map(String::trim)
                .map(value -> value.startsWith("@") ? value.substring(1) : value)
                .orElse(null);

        if (ValidationUtil.isNull(username)) {
            return;
        }

        var profileResponse = this.userClient.getPublicProfile(username);
        Integer userId = ValidationUtil.isNull(profileResponse) || ValidationUtil.isNull(profileResponse.getData())
                ? null
                : profileResponse.getData().getUserId();

        storyCriteria.setUserId(Optional.of(ValidationUtil.isNull(userId) ? "0" : userId.toString()));
    }

    private Map<Integer, UserInfoResClientDTO> getUserInfoMap(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return this.userClient.getInfo(
                        UserInfoReqClientDTO.builder()
                                .userIds(userIds)
                                .build())
                .stream()
                .filter(user -> ValidationUtil.nonNull(user.getId()))
                .collect(Collectors.toMap(
                        UserInfoResClientDTO::getId,
                        Function.identity(),
                        (existing, replacement) -> existing));
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

    private void setDefaultStats(StoryDetailResponseDTO storyDetailResponseDTO) {
        if (ValidationUtil.isNull(storyDetailResponseDTO.getStats())) {
            storyDetailResponseDTO.setStats(new StoryStatsDetailResponseDTO(0, 0, 0));
        }
    }

    private void setDefaultStats(StorySummaryResponseDTO storySummaryResponseDTO) {
        if (ValidationUtil.isNull(storySummaryResponseDTO.getStats())) {
            storySummaryResponseDTO.setStats(new StoryStatsDetailResponseDTO(0, 0, 0));
        }
    }

    private StoryDetailResponseDTO enrichStoryDetail(StoryEntity storyEntity) {
        StoryDetailResponseDTO storyDetailResponseDTO = this.storyMapper.entityToDetailResponse(storyEntity);
        storyDetailResponseDTO.setUser(this.getUserInfoMap(List.of(storyEntity.getUserId()))
                .get(storyEntity.getUserId()));
        storyDetailResponseDTO.setMedia(this.getMediaInfoMap(List.of(storyEntity.getMediaId()))
                .get(storyEntity.getMediaId()));
        this.setDefaultStats(storyDetailResponseDTO);

        return storyDetailResponseDTO;
    }

    private StoryEntity getById(Integer id) {
        return this.storyRepository.findById(id)
                .orElseThrow(() -> new StoryNotFoundByIdException(id));
    }

    private Integer getCurrentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để thao tác với tin."));
    }

    private void validateStoryMedia(Integer userId, String mediaId) {
        boolean valid = Boolean.TRUE.equals(this.mediaClient.validateMedia(MediaValidReqClientDTO.builder()
                .userId(userId)
                .mediaPurpose(MediaPurposeEnum.STORY)
                .mediaIds(List.of(mediaId))
                .build()));
        if (!valid) {
            throw new PostMediaInvalidException();
        }

        MediaInfoResClientDTO media = this.getMediaInfoMap(List.of(mediaId)).get(mediaId);
        String mediaType = ValidationUtil.isNull(media) ? null : media.getType();
        String contentType = ValidationUtil.isNull(media) ? null : media.getContentType();
        boolean supportedType = ValidationUtil.nonNull(mediaType) && (mediaType.equalsIgnoreCase("Ảnh")
                || mediaType.equalsIgnoreCase("Image")
                || mediaType.equalsIgnoreCase("IMAGE")
                || mediaType.equalsIgnoreCase("Video")
                || mediaType.equalsIgnoreCase("VIDEO"));
        boolean supportedContentType = ValidationUtil.nonNull(contentType)
                && (contentType.toLowerCase(java.util.Locale.ROOT).startsWith("image/")
                        || contentType.toLowerCase(java.util.Locale.ROOT).startsWith("video/"));
        if (ValidationUtil.isNull(media) || (!supportedType && !supportedContentType)) {
            throw new PostMediaInvalidException();
        }
    }

    private List<StoryEntity> getVisibleStories(List<StoryEntity> stories, Integer viewerId) {
        Map<StoryVisibilityEnum, List<Integer>> ownersByVisibility = new java.util.EnumMap<>(StoryVisibilityEnum.class);
        for (StoryEntity story : stories) {
            StorySettingEntity setting = story.getSetting();
            if (ValidationUtil.nonNull(setting) && story.getUserId().equals(viewerId)) {
                continue;
            }

            StoryVisibilityEnum visibility = ValidationUtil.isNull(setting)
                    ? StoryVisibilityEnum.EVERYONE
                    : setting.getVisibility();
            if (visibility != StoryVisibilityEnum.EVERYONE && ValidationUtil.isNull(viewerId)) {
                continue;
            }
            if (ValidationUtil.nonNull(viewerId)) {
                ownersByVisibility.computeIfAbsent(visibility, ignored -> new ArrayList<>()).add(story.getUserId());
            }
        }

        Map<StoryVisibilityEnum, java.util.Set<Integer>> allowedOwners = new java.util.EnumMap<>(StoryVisibilityEnum.class);
        ownersByVisibility.forEach((visibility, ownerIds) -> {
            RestResponseDTO<List<Integer>> response = this.relationshipClient.getStoryAudience(
                    visibility.name(), ownerIds.stream().distinct().toList());
            if (ValidationUtil.nonNull(response) && ValidationUtil.nonNull(response.getData())) {
                allowedOwners.put(visibility, java.util.Set.copyOf(response.getData()));
            }
        });

        return stories.stream().filter(story -> {
            StorySettingEntity setting = story.getSetting();
            if (ValidationUtil.nonNull(viewerId) && story.getUserId().equals(viewerId)) {
                return true;
            }
            StoryVisibilityEnum visibility = ValidationUtil.isNull(setting)
                    ? StoryVisibilityEnum.EVERYONE
                    : setting.getVisibility();
            if (visibility == StoryVisibilityEnum.EVERYONE && ValidationUtil.isNull(viewerId)) {
                return true;
            }
            return allowedOwners.getOrDefault(visibility, java.util.Set.of()).contains(story.getUserId());
        }).toList();
    }

    private List<StorySummaryResponseDTO> toStorySummaries(List<StoryEntity> stories) {
        List<Integer> userIds = stories.stream().map(StoryEntity::getUserId).distinct().toList();
        List<String> mediaIds = stories.stream().map(StoryEntity::getMediaId).distinct().toList();
        Map<Integer, UserInfoResClientDTO> userMap = this.getUserInfoMap(userIds);
        Map<String, MediaInfoResClientDTO> mediaMap = this.getMediaInfoMap(mediaIds);

        return stories.stream().map(story -> {
            StorySummaryResponseDTO response = this.storyMapper.entityToSummaryResponse(story);
            response.setUser(userMap.get(story.getUserId()));
            response.setMedia(mediaMap.get(story.getMediaId()));
            this.setDefaultStats(response);
            return response;
        }).toList();
    }

    private Page<StoryEntity> getAll(StoryCriteria storyCriteria) {
        this.applyAuthorSearch(storyCriteria);

        Sort sort = Sort.by(StoryEntity_.CREATED_AT).descending();
        if (ValidationUtil.nonNull(storyCriteria.getSort())) {
            String sortString = storyCriteria.getSort()
                    .filter(ValidationUtil::hasText)
                    .orElse("");
            sort = switch (sortString) {
                case "Mã công khai tăng dần" -> Sort.by(StoryEntity_.PUBLIC_ID).ascending();
                case "Mã công khai giảm dần" -> Sort.by(StoryEntity_.PUBLIC_ID).descending();
                case "Ngày đăng tăng dần" -> Sort.by(StoryEntity_.CREATED_AT).ascending();
                case "Ngày đăng giảm dần" -> Sort.by(StoryEntity_.CREATED_AT).descending();
                case "Hết hạn lúc tăng dần" -> Sort.by(StoryEntity_.EXPIRED_AT).ascending();
                case "Hết hạn lúc giảm dần" -> Sort.by(StoryEntity_.EXPIRED_AT).descending();
                default -> sort;
            };
        }

        Pageable pageable = Pageable.unpaged(sort);
        if (ValidationUtil.nonNull(storyCriteria.getPage())
                && ValidationUtil.nonNull(storyCriteria.getSize())
                && storyCriteria.getPage() >= 0
                && storyCriteria.getSize() > 0) {
            pageable = PageRequest.of(
                    storyCriteria.getPage(),
                    storyCriteria.getSize(),
                    sort);
        }

        Specification<StoryEntity> specification = StorySpecification.filterStories(storyCriteria);

        return this.storyRepository.findAll(specification, pageable);
    }

    @Override
    public List<StorySummaryResponseDTO> handleGetFeed() {
        String now = TimeUtil.getCurrentDatetime();
        Page<StoryEntity> page = this.storyRepository
                .findAllByStatusAndModerationStatusAndExpiredAtGreaterThanOrderByCreatedAtDesc(
                        StoryStatusEnum.ACTIVE,
                        StoryModerationStatusEnum.NONE,
                        now,
                        PageRequest.of(0, 500));
        Integer viewerId = SecurityUtil.getCurrentUserId().orElse(null);

        return this.toStorySummaries(this.getVisibleStories(page.getContent(), viewerId));
    }

    @Override
    public List<StorySummaryResponseDTO> handleGetByUsername(String username) {
        Integer viewerId = this.getCurrentUserId();
        Integer ownerId = viewerId;
        if (ValidationUtil.nonNull(username) && ValidationUtil.hasText(username)) {
            var profileResponse = this.userClient.getPublicProfile(username);
            ownerId = ValidationUtil.isNull(profileResponse) || ValidationUtil.isNull(profileResponse.getData())
                    ? null
                    : profileResponse.getData().getUserId();
        }
        if (ValidationUtil.isNull(ownerId)) {
            return List.of();
        }

        final Integer targetOwnerId = ownerId;
        List<StoryEntity> stories = this.storyRepository
                .findAllByUserIdAndExpiredAtGreaterThanOrderByCreatedAtDesc(ownerId, TimeUtil.getCurrentDatetime(),
                        PageRequest.of(0, 100))
                .getContent().stream()
                .filter(story -> story.getStatus() == StoryStatusEnum.ACTIVE
                        || (targetOwnerId.equals(viewerId) && story.getStatus() == StoryStatusEnum.HIDDEN))
                .filter(story -> story.getModerationStatus() != StoryModerationStatusEnum.REJECTED)
                .toList();

        return this.toStorySummaries(this.getVisibleStories(stories, viewerId));
    }

    @Override
    public List<StoryHighlightResponseDTO> handleGetHighlights(String username) {
        var profileResponse = this.userClient.getPublicProfile(username);
        UserProfileVisibilityResClientDTO profile = profileResponse == null ? null : profileResponse.getData();
        if (profile == null || profile.getUserId() == null) {
            return List.of();
        }
        if (!Boolean.TRUE.equals(profile.getCanViewHighlights())) {
            throw new AccessDeniedException("Mục tin nổi bật của tài khoản này không được chia sẻ công khai.");
        }

        List<StoryHighlightEntity> highlights = this.storyHighlightRepository
                .findAllByUserIdOrderByIdAsc(profile.getUserId());
        Integer viewerId = SecurityUtil.getCurrentUserId().orElse(null);
        Map<Integer, StoryEntity> storiesById = highlights.stream()
                .flatMap(highlight -> highlight.getStories().stream())
                .map(StoryHighlightStoryEntity::getStory)
                .filter(story -> story.getStatus() != StoryStatusEnum.REMOVED
                        && (story.getStatus() != StoryStatusEnum.HIDDEN
                                || profile.getUserId().equals(viewerId))
                        && story.getModerationStatus() != StoryModerationStatusEnum.REJECTED)
                .collect(Collectors.toMap(StoryEntity::getId, Function.identity(), (first, second) -> first));
        Map<Integer, StorySummaryResponseDTO> storySummaries = this.toStorySummaries(
                        this.getVisibleStories(List.copyOf(storiesById.values()), viewerId)).stream()
                .collect(Collectors.toMap(StorySummaryResponseDTO::getId, Function.identity(), (first, second) -> first));

        return highlights.stream().map(highlight -> {
            List<StorySummaryResponseDTO> highlightStories = highlight.getStories().stream()
                    .sorted(java.util.Comparator.comparing(StoryHighlightStoryEntity::getPosition))
                    .map(item -> storySummaries.get(item.getStory().getId()))
                    .filter(ValidationUtil::nonNull).toList();
            return new StoryHighlightResponseDTO(highlight.getId(), highlight.getTitle(), highlightStories);
        }).filter(highlight -> !highlight.getStories().isEmpty()
                || profile.getUserId().equals(viewerId)).toList();
    }

    @Override
    public List<StorySummaryResponseDTO> handleGetHighlightableStories() {
        Integer userId = this.getCurrentUserId();
        List<StoryEntity> stories = this.storyRepository
                .findAllByUserIdAndStatusNotAndModerationStatusNotOrderByCreatedAtAsc(
                        userId, StoryStatusEnum.REMOVED, StoryModerationStatusEnum.REJECTED);
        return this.toStorySummaries(stories);
    }

    private List<StoryEntity> validateHighlightStoryIds(Integer userId, List<Integer> requestedIds) {
        if (requestedIds == null || requestedIds.isEmpty()) {
            throw new StoryHighlightInvalidException("Hãy chọn ít nhất một tin.");
        }
        List<Integer> ids = requestedIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.size() != requestedIds.size()) {
            throw new StoryHighlightInvalidException("Danh sách tin được chọn bị trùng hoặc không hợp lệ.");
        }
        Map<Integer, StoryEntity> stories = this.storyRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(StoryEntity::getId, Function.identity()));
        if (stories.size() != ids.size() || ids.stream().anyMatch(id -> {
            StoryEntity story = stories.get(id);
            return story == null || !userId.equals(story.getUserId())
                    || story.getStatus() == StoryStatusEnum.REMOVED
                    || story.getModerationStatus() == StoryModerationStatusEnum.REJECTED;
        })) {
            throw new AccessDeniedException("Chỉ có thể thêm tin của bạn chưa bị gỡ hoặc từ chối.");
        }
        return ids.stream().map(stories::get).toList();
    }

    private StoryHighlightResponseDTO toHighlightResponse(StoryHighlightEntity highlight) {
        List<StorySummaryResponseDTO> stories = this.toStorySummaries(highlight.getStories().stream()
                .sorted(java.util.Comparator.comparing(StoryHighlightStoryEntity::getPosition))
                .map(StoryHighlightStoryEntity::getStory).toList());
        return new StoryHighlightResponseDTO(highlight.getId(), highlight.getTitle(), stories);
    }

    @Override
    public StoryHighlightResponseDTO handleCreateHighlight(StoryHighlightRequestDTO request) {
        Integer userId = this.getCurrentUserId();
        List<StoryEntity> stories = this.validateHighlightStoryIds(userId, request.getStoryIds());
        StoryHighlightEntity highlight = StoryHighlightEntity.builder()
                .userId(userId)
                .title(request.getTitle().trim())
                .createdAt(TimeUtil.getCurrentDatetime())
                .stories(new ArrayList<>())
                .build();
        for (int index = 0; index < stories.size(); index++) {
            highlight.getStories().add(StoryHighlightStoryEntity.builder()
                    .highlight(highlight).story(stories.get(index)).position(index).build());
        }
        return this.toHighlightResponse(this.storyHighlightRepository.save(highlight));
    }

    @Override
    public StoryHighlightResponseDTO handleUpdateHighlight(Integer id, StoryHighlightRequestDTO request) {
        Integer userId = this.getCurrentUserId();
        StoryHighlightEntity highlight = this.storyHighlightRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new StoryHighlightNotFoundException(id));
        List<StoryEntity> stories = this.validateHighlightStoryIds(userId, request.getStoryIds());
        highlight.getStories().clear();
        this.storyHighlightRepository.flush();
        highlight.setTitle(request.getTitle().trim());
        for (int index = 0; index < stories.size(); index++) {
            highlight.getStories().add(StoryHighlightStoryEntity.builder()
                    .highlight(highlight).story(stories.get(index)).position(index).build());
        }
        return this.toHighlightResponse(this.storyHighlightRepository.save(highlight));
    }

    @Override
    public void handleRemoveHighlight(Integer id) {
        Integer userId = this.getCurrentUserId();
        StoryHighlightEntity highlight = this.storyHighlightRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new StoryHighlightNotFoundException(id));
        this.storyHighlightRepository.delete(highlight);
    }

    @Override
    public StoryDetailResponseDTO handleGetById(Integer id) {
        return this.enrichStoryDetail(this.getById(id));
    }

    @Override
    public PageResponseDTO<StorySummaryResponseDTO> handleGetSummary(StoryCriteria storyCriteria) {
        Page<StoryEntity> page = this.getAll(storyCriteria);

        List<Integer> userIds = page.getContent().stream()
                .map(StoryEntity::getUserId)
                .filter(ValidationUtil::nonNull)
                .distinct()
                .toList();
        List<String> mediaIds = page.getContent().stream()
                .map(StoryEntity::getMediaId)
                .filter(ValidationUtil::nonNull)
                .distinct()
                .toList();

        Map<Integer, UserInfoResClientDTO> userMap = this.getUserInfoMap(userIds);
        Map<String, MediaInfoResClientDTO> mediaMap = this.getMediaInfoMap(mediaIds);
        Page<StorySummaryResponseDTO> responsePage = page.map(story -> {
            StorySummaryResponseDTO storySummaryResponseDTO = this.storyMapper.entityToSummaryResponse(story);
            storySummaryResponseDTO.setUser(userMap.get(story.getUserId()));
            storySummaryResponseDTO.setMedia(mediaMap.get(story.getMediaId()));
            this.setDefaultStats(storySummaryResponseDTO);

            return storySummaryResponseDTO;
        });

        return PageResponseUtil.convert(responsePage);
    }

    @Override
    public StoryDetailResponseDTO handleCreate(StoryCreateRequestDTO request) {
        Integer userId = this.getCurrentUserId();
        this.validateStoryMedia(userId, request.getMediaId());
        LocalDateTime now = LocalDateTime.now();
        StoryEntity story = this.storyMapper.createEntityFromRequest(request);
        story.setUserId(userId);
        story.setCreatedAt(TimeUtil.getDatetime(now));
        story.setExpiredAt(TimeUtil.getDatetime(now.plusHours(24)));
        story.setStatus(StoryStatusEnum.ACTIVE);
        story.setModerationStatus(StoryModerationStatusEnum.NONE);
        String publicId = PublicIdGeneratorUtil.generate("STORY", 5);
        while (this.storyRepository.existsByPublicId(publicId)) {
            publicId = PublicIdGeneratorUtil.generate("STORY", 5);
        }
        story.setPublicId(publicId);
        story.setStats(StoryStatsEntity.builder()
                .viewCount(0).replyCount(0).shareCount(0).story(story).build());
        story.setSetting(StorySettingEntity.builder()
                .visibility(ValidationUtil.isNull(request.getVisibility())
                        ? StoryVisibilityEnum.EVERYONE
                        : request.getVisibility())
                .allowLike(ValidationUtil.isNull(request.getAllowLike()) || request.getAllowLike())
                .allowReply(ValidationUtil.isNull(request.getAllowReply()) || request.getAllowReply())
                .allowShare(ValidationUtil.isNull(request.getAllowShare()) || request.getAllowShare())
                .story(story).build());

        return this.enrichStoryDetail(this.storyRepository.save(story));
    }

    @Override
    public StoryDetailResponseDTO handleUpdateSetting(Integer id, StorySettingUpdateRequestDTO request) {
        StoryEntity story = this.getById(id);
        if (!story.getUserId().equals(this.getCurrentUserId())) {
            throw new AccessDeniedException("Bạn chỉ có thể sửa cài đặt tin của chính mình.");
        }
        StorySettingEntity setting = story.getSetting();
        if (ValidationUtil.isNull(setting)) {
            setting = StorySettingEntity.builder().story(story).build();
            story.setSetting(setting);
        }
        setting.setVisibility(request.getVisibility());
        setting.setAllowLike(request.getAllowLike());
        setting.setAllowReply(request.getAllowReply());
        setting.setAllowShare(request.getAllowShare());

        return this.enrichStoryDetail(this.storyRepository.save(story));
    }

    @Override
    public void handleRemove(Integer id) {
        StoryEntity story = this.getById(id);
        if (!story.getUserId().equals(this.getCurrentUserId())) {
            throw new AccessDeniedException("Bạn chỉ có thể xoá tin của chính mình.");
        }
        story.setStatus(StoryStatusEnum.REMOVED);

        this.storyRepository.save(story);
    }

    @Override
    public StoryDetailResponseDTO handleUpdateStatusById(
            Integer id,
            StoryUpdateStatusRequestDTO request) {
        StoryEntity storyEntity = this.getById(id);
        storyEntity.setStatus(request.getStatus());

        return this.enrichStoryDetail(this.storyRepository.save(storyEntity));
    }

    @Override
    public StoryDetailResponseDTO handleUpdateModerationStatusById(
            Integer id,
            StoryUpdateModerationStatusRequestDTO request) {
        StoryEntity storyEntity = this.getById(id);
        StoryModerationStatusEnum previousStatus = storyEntity.getModerationStatus();
        storyEntity.setModerationStatus(request.getModerationStatus());
        StoryEntity savedStory = this.storyRepository.save(storyEntity);
        if (previousStatus != request.getModerationStatus()) {
            if (request.getModerationStatus() == StoryModerationStatusEnum.REJECTED) {
                this.producerService.publish(
                        savedStory.getUserId(), "STORY_REJECTED", "STORY", savedStory.getId(),
                        "Tin của bạn bị từ chối", "Tin của bạn đã bị từ chối kiểm duyệt.",
                        Map.of("storyId", savedStory.getId()));
            } else if (request.getModerationStatus() == StoryModerationStatusEnum.FLAGGED) {
                this.producerService.publish(
                        savedStory.getUserId(), "STORY_FLAGGED", "STORY", savedStory.getId(),
                        "Tin của bạn đang được xem xét", "Tin của bạn đang được xem xét.",
                        Map.of("storyId", savedStory.getId()));
            }
        }

        return this.enrichStoryDetail(savedStory);
    }
}
