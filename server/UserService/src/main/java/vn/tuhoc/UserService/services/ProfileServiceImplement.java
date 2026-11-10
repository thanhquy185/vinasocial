package vn.tuhoc.UserService.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.clients.MediaClient;
import vn.tuhoc.UserService.domains.entities.ProfileEntity;
import vn.tuhoc.UserService.domains.entities.ProfileLinkEntity;
import vn.tuhoc.UserService.domains.entities.ProfilePrivacySettingEntity;
import vn.tuhoc.UserService.domains.entities.ProfileNotificationSettingEntity;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;
import vn.tuhoc.UserService.domains.mappers.ProfileMapper;
import vn.tuhoc.UserService.dtos.requests.ProfileCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePersonalInformationUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileStatsCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePublicResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileStatsDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByEmailException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByPhoneException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByUserIdException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByUsernameException;
import vn.tuhoc.UserService.exceptions.ProfileUsernameIsExistsException;
import vn.tuhoc.UserService.exceptions.ProfilePropertyAlreadyExistsException;
import vn.tuhoc.UserService.exceptions.ProfileAvatarInvalidException;
import vn.tuhoc.UserService.exceptions.ProfileSettingUpdateInvalidException;
import vn.tuhoc.UserService.repositories.ProfilePrivacySettingRepository;
import vn.tuhoc.UserService.repositories.ProfileNotificationSettingRepository;
import vn.tuhoc.UserService.repositories.ProfileRepository;
import vn.tuhoc.UserService.services.interfaces.ProfileNotificationSettingService;
import vn.tuhoc.UserService.services.interfaces.ProfilePrivacySettingService;
import vn.tuhoc.UserService.services.interfaces.ProfileService;
import vn.tuhoc.UserService.services.interfaces.ProfileStatsService;
import vn.tuhoc.UserService.services.interfaces.ProducerService;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImplement implements ProfileService {
    MediaClient mediaClient;
    ProfileStatsService profileStatsService;
    ProducerService producerService;
    ProfileNotificationSettingService profileNotificationSettingService;
    ProfilePrivacySettingService profilePrivacySettingService;
    ProfileRepository profileRepository;
    ProfilePrivacySettingRepository profilePrivacySettingRepository;
    ProfileNotificationSettingRepository profileNotificationSettingRepository;
    ProfileMapper profileMapper;

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

    private ProfileDetailResponseDTO enrichProfileDetail(ProfileEntity profileEntity) {
        ProfileDetailResponseDTO profileDetailResponseDTO = this.profileMapper
                .entityToDetailResponse(profileEntity);
        String avatarMediaId = profileEntity.getAvatar();

        if (ValidationUtil.nonNull(avatarMediaId)) {
            profileDetailResponseDTO.setAvatar(this.getMediaInfoMap(List.of(avatarMediaId)).get(avatarMediaId));
        }

        return profileDetailResponseDTO;
    }

    private ProfileEntity getById(Integer id) {
        return this.profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundByIdException(id));
    }

    private ProfileEntity getByUserId(Integer userId) {
        return this.profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundByUserIdException(userId));
    }

    private ProfileEntity getByPhone(String phone) {
        return this.profileRepository.findByPhone(phone)
                .orElseThrow(() -> new ProfileNotFoundByPhoneException(phone));
    }

    private ProfileEntity getByEmail(String email) {
        return this.profileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundByEmailException(email));
    }

    private ProfileEntity getByUsername(String username) {
        return this.profileRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundByUsernameException(username));
    }

    private void handleExitsByUsername(String username) {
        if (this.profileRepository.existsByUsername(username)) {
            throw new ProfileUsernameIsExistsException(username);
        }
    }

    private Integer getCurrentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để chỉnh sửa hồ sơ."));
    }

    private void updateWebsite(ProfileEntity profile, String website) {
        if (ValidationUtil.isNull(website)) return;
        List<ProfileLinkEntity> links = profile.getLinks();
        if (ValidationUtil.isNull(links)) {
            links = new java.util.ArrayList<>();
            profile.setLinks(links);
        }

        ProfileLinkEntity websiteLink = links.stream()
                .filter(link -> "Website".equalsIgnoreCase(link.getTitle()))
                .findFirst()
                .orElse(null);
        if (!ValidationUtil.hasText(website)) {
            if (ValidationUtil.nonNull(websiteLink)) links.remove(websiteLink);
            return;
        }

        if (ValidationUtil.isNull(websiteLink)) {
            websiteLink = new ProfileLinkEntity();
            websiteLink.setProfile(profile);
            websiteLink.setTitle("Website");
            links.add(websiteLink);
        }
        websiteLink.setUrl(website.trim());
    }

    private JsonNode getSettingUpdateValue(Object request, Object type, JsonNode value) {
        if (ValidationUtil.isNull(request)
                || ValidationUtil.isNull(type)
                || ValidationUtil.isNull(value)
                || value.isNull()) {
            throw new ProfileSettingUpdateInvalidException();
        }
        return value;
    }

    private ProfilePrivacySettingPermissionEnum getPermissionValue(JsonNode value) {
        if (!value.isTextual()) throw new ProfileSettingUpdateInvalidException();
        try {
            return ProfilePrivacySettingPermissionEnum.fromDescription(value.asText());
        } catch (IllegalArgumentException exception) {
            throw new ProfileSettingUpdateInvalidException();
        }
    }

    @Override
    public ProfileDetailResponseDTO handleGetById(Integer id) {
        return this.enrichProfileDetail(this.getById(id));
    }

    @Override
    public ProfileDetailResponseDTO handleGetByUserId(Integer userId) {
        return this.enrichProfileDetail(this.getByUserId(userId));
    }

    @Override
    public ProfileDetailResponseDTO handleGetByPhone(String phone) {
        return this.enrichProfileDetail(this.getByPhone(phone));
    }

    @Override
    public ProfileDetailResponseDTO handleGetByEmail(String email) {
        return this.enrichProfileDetail(this.getByEmail(email));
    }

    @Override
    public ProfileDetailResponseDTO handleGetByUsername(String username) {
        return this.enrichProfileDetail(this.getByUsername(username));
    }

    @Override
    public ProfilePublicResponseDTO handleGetPublicByUsername(String username) {
        ProfileEntity profile = this.getByUsername(username);
        Integer userId = profile.getUser().getId();
        boolean isOwner = SecurityUtil.getCurrentUserId().map(userId::equals).orElse(false);
        ProfilePrivacySettingDetailResponseDTO privacy = this.profilePrivacySettingService
                .handleGetByProfileId(profile.getId());
        boolean canViewProfile = isOwner || (Boolean.FALSE.equals(privacy.getIsPrivate())
                && privacy.getShowProfilePermission() == ProfilePrivacySettingPermissionEnum.EVERYONE);
        boolean canViewPosts = canViewProfile && (isOwner
                || privacy.getShowPostsPermission() == ProfilePrivacySettingPermissionEnum.EVERYONE);
        boolean canViewReposts = canViewProfile && (isOwner
                || privacy.getShowRepostsPermission() == ProfilePrivacySettingPermissionEnum.EVERYONE);
        boolean canViewHighlights = canViewProfile && (isOwner
                || privacy.getShowHighlightStoriesPermission() == ProfilePrivacySettingPermissionEnum.EVERYONE);

        ProfilePublicResponseDTO response = new ProfilePublicResponseDTO();
        response.setUserId(userId);
        response.setUsername(profile.getUsername());
        response.setIsOwnProfile(isOwner);
        response.setIsPrivate(privacy.getIsPrivate());
        response.setCanViewProfile(canViewProfile);
        response.setCanViewPosts(canViewPosts);
        response.setCanViewReposts(canViewReposts);
        response.setCanViewHighlights(canViewHighlights);

        if (canViewProfile) {
            response.setFirstName(profile.getFirstName());
            response.setLastName(profile.getLastName());
            response.setAlias(profile.getAlias());
            response.setBio(profile.getBio());
            response.setAvatar(this.enrichProfileDetail(profile).getAvatar());
            response.setStats(this.profileStatsService.handleGetByProfileId(profile.getId()));
            response.setLinks((ValidationUtil.isNull(profile.getLinks()) ? List.<vn.tuhoc.UserService.domains.entities.ProfileLinkEntity>of() : profile.getLinks()).stream()
                    .map(link -> new vn.tuhoc.UserService.dtos.responses.ProfileLinkDetailResponseDTO(
                            link.getId(), link.getTitle(), link.getUrl()))
                    .toList());
            response.setIsVerified((ValidationUtil.isNull(profile.getUser().getUsernames())
                    ? List.<vn.tuhoc.UserService.domains.entities.UsernameEntity>of()
                    : profile.getUser().getUsernames()).stream()
                    .filter(item -> item.getValue().equalsIgnoreCase(username))
                    .anyMatch(item -> Boolean.TRUE.equals(item.getIsVerify())));
        }

        return response;
    }

    @Override
    public ProfileDetailResponseDTO handleUpdateCurrentProfile(ProfileUpdateRequestDTO request) {
        if (ValidationUtil.isNull(request)) throw new ProfileSettingUpdateInvalidException();
        Integer userId = this.getCurrentUserId();
        ProfileEntity profile = this.getByUserId(userId);

        String requestedUsername = ValidationUtil.hasText(request.getUsername())
                ? request.getUsername().trim()
                : null;
        if (ValidationUtil.nonNull(requestedUsername)
                && !requestedUsername.equalsIgnoreCase(profile.getUsername())
                && this.profileRepository.existsByUsernameAndIdNot(requestedUsername, profile.getId())) {
            throw new ProfileUsernameIsExistsException(requestedUsername);
        }
        if (ValidationUtil.nonNull(request.getAvatarId())) {
            Boolean validAvatar = this.mediaClient.validateMedia(
                    vn.tuhoc.UserService.dtos.clients.MediaValidReqClientDTO.builder()
                            .userId(userId)
                            .mediaPurpose("Ảnh đại diện")
                            .mediaIds(List.of(request.getAvatarId()))
                            .build());
            if (!Boolean.TRUE.equals(validAvatar)) {
                throw new ProfileAvatarInvalidException();
            }
            profile.setAvatar(request.getAvatarId());
        }

        if (ValidationUtil.nonNull(requestedUsername)) profile.setUsername(requestedUsername);
        if (ValidationUtil.nonNull(request.getAlias())) profile.setAlias(request.getAlias().trim());
        if (ValidationUtil.nonNull(request.getBio())) profile.setBio(request.getBio().trim());
        this.updateWebsite(profile, request.getWebsite());

        return this.enrichProfileDetail(this.profileRepository.save(profile));
    }

    @Override
    public ProfileDetailResponseDTO handleUpdateCurrentPersonalInformation(
            ProfilePersonalInformationUpdateRequestDTO request) {
        if (ValidationUtil.isNull(request)) throw new ProfileSettingUpdateInvalidException();
        ProfileEntity profile = this.getByUserId(this.getCurrentUserId());
        if (ValidationUtil.hasText(request.getPhone())) {
            this.profileRepository.findByPhone(request.getPhone())
                    .filter(existing -> !existing.getId().equals(profile.getId()))
                    .ifPresent(existing -> { throw new ProfilePropertyAlreadyExistsException("Số điện thoại"); });
        }
        if (ValidationUtil.hasText(request.getEmail())) {
            this.profileRepository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(profile.getId()))
                    .ifPresent(existing -> { throw new ProfilePropertyAlreadyExistsException("Email"); });
        }
        if (ValidationUtil.nonNull(request.getFirstName())) profile.setFirstName(request.getFirstName().trim());
        if (ValidationUtil.nonNull(request.getLastName())) profile.setLastName(request.getLastName().trim());
        if (ValidationUtil.nonNull(request.getBirthdate())) profile.setBirthdate(request.getBirthdate());
        if (ValidationUtil.nonNull(request.getGender())) profile.setGender(request.getGender());
        if (ValidationUtil.nonNull(request.getPhone())) profile.setPhone(
                ValidationUtil.hasText(request.getPhone()) ? request.getPhone().trim() : null);
        if (ValidationUtil.nonNull(request.getEmail())) profile.setEmail(
                ValidationUtil.hasText(request.getEmail()) ? request.getEmail().trim() : null);

        return this.enrichProfileDetail(this.profileRepository.save(profile));
    }

    @Override
    public ProfilePrivacySettingDetailResponseDTO handleUpdateCurrentPrivacy(
            ProfilePrivacySettingUpdateRequestDTO request) {
        if (ValidationUtil.isNull(request)) throw new ProfileSettingUpdateInvalidException();
        ProfileEntity profile = this.getByUserId(this.getCurrentUserId());
        ProfilePrivacySettingEntity setting = this.profilePrivacySettingRepository.findById(profile.getId())
                .orElseThrow(() -> new vn.tuhoc.UserService.exceptions.ProfilePrivacySettingNotFoundByProfileIdException(profile.getId()));
        JsonNode value = this.getSettingUpdateValue(request, request.getType(), request.getValue());
        switch (request.getType()) {
            case IS_PRIVATE -> {
                if (!value.isBoolean()) throw new ProfileSettingUpdateInvalidException();
                setting.setIsPrivate(value.booleanValue());
            }
            case SHOW_PROFILE_PERMISSION -> setting.setShowProfilePermission(this.getPermissionValue(value));
            case SHOW_FOLLOWINGS_PERMISSION -> setting.setShowFollowingsPermission(this.getPermissionValue(value));
            case SHOW_FOLLOWERS_PERMISSION -> setting.setShowFollowersPermission(this.getPermissionValue(value));
            case SHOW_HIGHLIGHT_STORIES_PERMISSION -> setting.setShowHighlightStoriesPermission(this.getPermissionValue(value));
            case SHOW_POSTS_PERMISSION -> setting.setShowPostsPermission(this.getPermissionValue(value));
            case SHOW_REPOSTS_PERMISSION -> setting.setShowRepostsPermission(this.getPermissionValue(value));
            case SHOW_SAVES_PERMISSION -> setting.setShowSavesPermission(this.getPermissionValue(value));
            case SEND_MESSAGE_PERMISSION -> setting.setSendMessagePermission(this.getPermissionValue(value));
        }
        return this.profilePrivacySettingService.handleGetByProfileId(
                this.profilePrivacySettingRepository.save(setting).getProfileId());
    }

    @Override
    public ProfileNotificationSettingDetailResponseDTO handleUpdateCurrentNotifications(
            ProfileNotificationSettingUpdateRequestDTO request) {
        if (ValidationUtil.isNull(request)) throw new ProfileSettingUpdateInvalidException();
        ProfileEntity profile = this.getByUserId(this.getCurrentUserId());
        ProfileNotificationSettingEntity setting = this.profileNotificationSettingRepository.findById(profile.getId())
                .orElseThrow(() -> new vn.tuhoc.UserService.exceptions.ProfileNotificationSettingNotFoundByProfileIdException(profile.getId()));
        JsonNode value = this.getSettingUpdateValue(request, request.getType(), request.getValue());
        if (!value.isBoolean()) throw new ProfileSettingUpdateInvalidException();
        boolean enabled = value.booleanValue();
        switch (request.getType()) {
            case POST_ENABLED -> setting.setPostEnabled(enabled);
            case STORY_ENABLED -> setting.setStoryEnabled(enabled);
            case COLLABORATOR_ENABLED -> setting.setCollaboratorEnabled(enabled);
            case TAG_ENABLED -> setting.setTagEnabled(enabled);
            case MESSAGE_ENABLED -> setting.setMessageEnabled(enabled);
            case FOLLOW_ENABLED -> setting.setFollowEnabled(enabled);
            case FRIEND_ENABLED -> setting.setFriendEnabled(enabled);
            case BEST_FRIEND_ENABLED -> setting.setBestFriendEnabled(enabled);
            case PAGE_FOLLOWING_ENABLED -> setting.setPageFollowingEnabled(enabled);
            case REPLY_COMMENT_ENABLED -> setting.setReplyCommentEnabled(enabled);
        }
        ProfileNotificationSettingEntity savedSetting = this.profileNotificationSettingRepository.save(setting);
        this.producerService.sendNotificationPreferencesUpdated(NotificationPreferencesUpdatedEventDTO.builder()
                .userId(profile.getUser().getId())
                .postEnabled(savedSetting.getPostEnabled())
                .storyEnabled(savedSetting.getStoryEnabled())
                .collaboratorEnabled(savedSetting.getCollaboratorEnabled())
                .tagEnabled(savedSetting.getTagEnabled())
                .messageEnabled(savedSetting.getMessageEnabled())
                .followEnabled(savedSetting.getFollowEnabled())
                .replyCommentEnabled(savedSetting.getReplyCommentEnabled())
                .build());
        return this.profileNotificationSettingService.handleGetByProfileId(savedSetting.getProfileId());
    }

    @Override
    public ProfileDetailResponseDTO handleCreate(ProfileCreateRequestDTO profileCreateRequestDTO) {
        this.handleExitsByUsername(profileCreateRequestDTO.getUsername());

        ProfileEntity profileEntity = this.profileMapper.createEntityFromRequest(profileCreateRequestDTO);
        ProfileEntity profileCreated = this.profileRepository.save(profileEntity);

        // Create Stats
        ProfileStatsCreateRequestDTO profileStatsCreateRequestDTO = ProfileStatsCreateRequestDTO.builder()
                .profileId(profileCreated.getId())
                .mediaCount(0)
                .friendCount(0)
                .bestFriendCount(0)
                .followerCount(0)
                .followingCount(0)
                .updatedCount(0)
                .build();
        ProfileStatsDetailResponseDTO profileStatsDetailResponseDTO = this.profileStatsService
                .handleCreate(profileStatsCreateRequestDTO);
        // Create Notification Setting
        ProfileNotificationSettingCreateRequestDTO profileNotificationSettingCreateRequestDTO = ProfileNotificationSettingCreateRequestDTO
                .builder()
                .profileId(profileCreated.getId())
                .postEnabled(true)
                .storyEnabled(true)
                .collaboratorEnabled(true)
                .tagEnabled(true)
                .messageEnabled(true)
                .followEnabled(true)
                .friendEnabled(true)
                .bestFriendEnabled(true)
                .pageFollowingEnabled(true)
                .replyCommentEnabled(true)
                .build();
        ProfileNotificationSettingDetailResponseDTO profileNotificationSettingDetailResponseDTO = this.profileNotificationSettingService
                .handleCreate(profileNotificationSettingCreateRequestDTO);
        // Create Privacy Setting
        ProfilePrivacySettingCreateRequestDTO profilePrivacySettingCreateRequestDTO = ProfilePrivacySettingCreateRequestDTO
                .builder()
                .profileId(profileCreated.getId())
                .isPrivate(false)
                .showProfilePermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showFollowingsPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showFollowersPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showHighlightStoriesPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showPostsPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showRepostsPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .showSavesPermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .sendMessagePermission(ProfilePrivacySettingPermissionEnum.EVERYONE)
                .build();
        ProfilePrivacySettingDetailResponseDTO profilePrivacySettingDetailResponseDTO = this.profilePrivacySettingService
                .handleCreate(profilePrivacySettingCreateRequestDTO);

        ProfileDetailResponseDTO profileDetailResponseDTO = this.enrichProfileDetail(profileCreated);
        profileDetailResponseDTO.setStats(profileStatsDetailResponseDTO);
        profileDetailResponseDTO.setNotificationSetting(profileNotificationSettingDetailResponseDTO);
        profileDetailResponseDTO.setPrivacySetting(profilePrivacySettingDetailResponseDTO);

        return profileDetailResponseDTO;
    }

}
