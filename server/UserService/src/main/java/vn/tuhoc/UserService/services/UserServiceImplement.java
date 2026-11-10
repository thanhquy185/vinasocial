package vn.tuhoc.UserService.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.clients.MediaClient;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.domains.entities.UserEntity_;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;
import vn.tuhoc.UserService.domains.mappers.UserMapper;
import vn.tuhoc.UserService.dtos.events.UserCreatedEventDTO;
import vn.tuhoc.UserService.dtos.requests.UserChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserDeleteRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserUpdateStatusRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UsernameCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserSummaryResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePublicResponseDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePersonalInformationUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.UserNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByPublicIdException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByUsernameAndPasswordException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByUsernameException;
import vn.tuhoc.UserService.repositories.UserRepository;
import vn.tuhoc.UserService.repositories.criteria.UserCriteria;
import vn.tuhoc.UserService.repositories.specifications.UserSpecification;
import vn.tuhoc.UserService.services.interfaces.ProfileService;
import vn.tuhoc.UserService.services.interfaces.ProducerService;
import vn.tuhoc.UserService.services.interfaces.UserService;
import vn.tuhoc.UserService.services.interfaces.UsernameService;
import vn.tuhoc.UserService.utils.PasswordUtil;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.PublicIdGeneratorUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplement implements UserService {
    MediaClient mediaClient;
    ProducerService producerService;
    ProfileService profileService;
    UsernameService usernameService;
    UserRepository userRepository;
    UserMapper userMapper;

    private UserDetailResponseDTO getUserDetail(UserEntity userEntity) {
        UserDetailResponseDTO userDetailResponseDTO = this.userMapper.entityToDetailResponse(userEntity);

        if (ValidationUtil.nonNull(userEntity.getProfile())) {
            userDetailResponseDTO.setProfile(this.profileService.handleGetByUserId(userEntity.getId()));
        }

        return userDetailResponseDTO;
    }

    private UserEntity getById(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));
    }

    private UserEntity getByPublicId(String publicId) {
        return this.userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new UserNotFoundByPublicIdException(publicId));
    }

    private UserEntity getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundByUsernameException(username));
    }

    private UserEntity getByUsernameToLogin(String username) {
        return this.userRepository.findByUsername(username).orElse(null);
    }

    private UserEntity getByUsernameAndPassword(String username, String password) {
        return this.userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new UserNotFoundByUsernameAndPasswordException());
    }

    private Page<UserEntity> getAll(UserCriteria userCriteria) {
        Sort sort = Sort.unsorted();
        if (ValidationUtil.nonNull(userCriteria.getSort())) {
            String sortString = userCriteria.getSort().filter(ValidationUtil::hasText).get();
            switch (sortString) {
                case "Mã tăng dần" -> sort = Sort.by(UserEntity_.ID).ascending();
                case "Mã giảm dần" -> sort = Sort.by(UserEntity_.ID).descending();
                case "Mã công khai tăng dần" -> sort = Sort.by(UserEntity_.PUBLIC_ID).ascending();
                case "Mã công khai giảm dần" -> sort = Sort.by(UserEntity_.PUBLIC_ID).descending();
                case "Ngày tạo tăng dần" -> sort = Sort.by(UserEntity_.CREATED_AT).ascending();
                case "Ngày tạo giảm dần" -> sort = Sort.by(UserEntity_.CREATED_AT).descending();
                case "Ngày đăng nhập cuối cùng tăng dần" -> sort = Sort.by(UserEntity_.LAST_LOGIN_AT).ascending();
                case "Ngày đăng nhập cuối cùng giảm dần" -> sort = Sort.by(UserEntity_.LAST_LOGIN_AT).descending();
                case "Quyền tăng dần" -> sort = Sort.by(UserEntity_.ROLE).ascending();
                case "Quyền giảm dần" -> sort = Sort.by(UserEntity_.ROLE).descending();
            }
        }

        Pageable pageable = Pageable.unpaged(sort);
        if (ValidationUtil.nonNull(userCriteria.getPage())
                && ValidationUtil.nonNull(userCriteria.getSize())) {
            pageable = PageRequest.of(
                    userCriteria.getPage(),
                    userCriteria.getSize(),
                    sort);
        }

        Specification<UserEntity> specification = UserSpecification.filterUsers(userCriteria);

        return this.userRepository.findAll(specification, pageable);
    }

    @Override
    public ProfilePublicResponseDTO handleGetPublicProfileByUsername(String username) {
        return this.profileService.handleGetPublicByUsername(username);
    }

    @Override
    public List<ProfilePublicResponseDTO> handleSearchPublicProfiles(String query) {
        String username = query == null ? "" : query.trim().replaceFirst("^@", "");
        if (username.length() < 2) {
            return List.of();
        }

        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(Optional.of(username));
        criteria.setRole(Optional.of(UserRoleEnum.MEMBER.getDescription()));
        criteria.setStatus(Optional.of(UserStatusEnum.ACTIVE.getDescription()));
        criteria.setPage(0);
        criteria.setSize(12);
        criteria.setSort(Optional.of("Mã tăng dần"));

        return this.getAll(criteria).getContent().stream()
                .filter(user -> ValidationUtil.nonNull(user.getProfile()))
                .map(user -> this.profileService.handleGetPublicByUsername(user.getProfile().getUsername()))
                .filter(profile -> profile.getUsername() != null
                        && profile.getUsername().toLowerCase().contains(username.toLowerCase()))
                .toList();
    }

    @Override
    public Object handleGetProfileForViewerByUsername(String username) {
        ProfilePublicResponseDTO publicProfile = this.profileService.handleGetPublicByUsername(username);
        if (Boolean.TRUE.equals(publicProfile.getIsOwnProfile())) {
            return this.profileService.handleGetByUsername(username);
        }
        return publicProfile;
    }

    @Override
    public ProfileDetailResponseDTO handleUpdateCurrentProfile(ProfileUpdateRequestDTO request) {
        return this.profileService.handleUpdateCurrentProfile(request);
    }

    @Override
    public ProfileDetailResponseDTO handleUpdateCurrentPersonalInformation(
            ProfilePersonalInformationUpdateRequestDTO request) {
        return this.profileService.handleUpdateCurrentPersonalInformation(request);
    }

    @Override
    public ProfilePrivacySettingDetailResponseDTO handleUpdateCurrentPrivacy(
            ProfilePrivacySettingUpdateRequestDTO request) {
        return this.profileService.handleUpdateCurrentPrivacy(request);
    }

    @Override
    public ProfileNotificationSettingDetailResponseDTO handleUpdateCurrentNotifications(
            ProfileNotificationSettingUpdateRequestDTO request) {
        return this.profileService.handleUpdateCurrentNotifications(request);
    }

    @Override
    public List<UserInfoResClientDTO> handleGetInfo(UserInfoReqClientDTO userInfoReqClientDTO) {
        if (ValidationUtil.isNull(userInfoReqClientDTO)
                || ValidationUtil.isNull(userInfoReqClientDTO.getUserIds())
                || userInfoReqClientDTO.getUserIds().isEmpty()) {
            return List.of();
        }

        List<UserInfoResClientDTO> userInfoResClientDTOs = this.userRepository
                .findUserInfoByIds(
                        userInfoReqClientDTO.getUserIds(),
                        SecurityUtil.getCurrentUserId().orElse(null),
                        ProfilePrivacySettingPermissionEnum.EVERYONE);

        List<String> avatarMediaIds = userInfoResClientDTOs.stream()
                .map(UserInfoResClientDTO::getAvatarMediaId)
                .filter(ValidationUtil::nonNull)
                .distinct()
                .toList();
        if (avatarMediaIds.isEmpty()) {
            return userInfoResClientDTOs;
        }

        List<MediaInfoResClientDTO> mediaInfoResClientDTOs = mediaClient
                .getInfo(MediaInfoReqClientDTO.builder()
                        .mediaIds(avatarMediaIds)
                        .build());

        Map<String, MediaInfoResClientDTO> mediaMap = mediaInfoResClientDTOs.stream()
                .collect(Collectors.toMap(
                        MediaInfoResClientDTO::getId,
                        Function.identity()));

        return userInfoResClientDTOs.stream()
                .map(userInfo -> {
                    userInfo.setAvatar(mediaMap.get(userInfo.getAvatarMediaId()));

                    return userInfo;
                })
                .toList();
    }

    @Override
    public UserDetailResponseDTO handleGetById(Integer id) {
        return this.getUserDetail(this.getById(id));
    }

    @Override
    public UserDetailResponseDTO handleGetByPublicId(String publicId) {
        return this.getUserDetail(this.getByPublicId(publicId));
    }

    @Override
    public UserDetailResponseDTO handleGetByUsername(String username) {
        return this.getUserDetail(this.getByUsername(username));
    }

    @Override
    public UserEntity handleGetByUsernameToLogin(String username) {
        return this.getByUsernameToLogin(username);
    }

    @Override
    public UserEntity handleGetByUsernameAndPassword(String username, String password) {
        return this.getByUsernameAndPassword(username, password);
    }

    @Override
    public PageResponseDTO<UserSummaryResponseDTO> handleGetSummary(UserCriteria userCriteria) {
        Page<UserSummaryResponseDTO> page = this.getAll(userCriteria).map(this.userMapper::entityToSummaryResponse);

        return PageResponseUtil.convert(page);
    }

    @Override
    public UserDetailResponseDTO handleCreate(UserCreateRequestDTO userCreateRequestDTO) {
        String usernameRequest = userCreateRequestDTO.getUsername();
        String passwordRequest = userCreateRequestDTO.getPassword();

        this.usernameService.handleExistsByValue(usernameRequest);

        // This endpoint is the admin user-management flow and only creates administrators.
        userCreateRequestDTO.setRole(UserRoleEnum.ADMIN);
        UserEntity userEntity = this.userMapper.createEntityFromRequest(userCreateRequestDTO);
        userEntity.setCreatedAt(TimeUtil.getCurrentDatetime());
        // Handle Public Id
        String publicId = PublicIdGeneratorUtil.generate("USER", 5);
        while (this.userRepository.existsByPublicId(publicId)) {
            publicId = PublicIdGeneratorUtil.generate("USER", 5);
        }
        userEntity.setPublicId(publicId);
        // Hash Password
        String hashPassword = PasswordUtil.getPasswordEncoder().encode(passwordRequest);
        userEntity.setPassword(hashPassword);

        UserEntity userCreated = this.userRepository.save(userEntity);
        UsernameDetailResponseDTO usernameCreated = this.usernameService.handleCreate(
                UsernameCreateRequestDTO.builder()
                .userId(userCreated.getId())
                .type(UsernameTypeEnum.TEXT)
                .value(usernameRequest)
                .isVerify(false)
                .isActive(true)
                .build());

        UserDetailResponseDTO userDetail = this.getUserDetail(this.getById(userCreated.getId()));
        userDetail.setUsernames(List.of(usernameCreated));

        return userDetail;
    }

    @Override
    public UserDetailResponseDTO handleCreateMember(UserCreateRequestDTO userCreateRequestDTO) {
        String identifier = userCreateRequestDTO.getUsername();
        String password = userCreateRequestDTO.getPassword();

        this.usernameService.handleExistsByValue(identifier);

        // Registration stores the email/phone username in AuthService after the user is created.
        userCreateRequestDTO.setRole(UserRoleEnum.MEMBER);
        UserEntity userEntity = this.userMapper.createEntityFromRequest(userCreateRequestDTO);
        userEntity.setCreatedAt(TimeUtil.getCurrentDatetime());

        String publicId = PublicIdGeneratorUtil.generate("USER", 5);
        while (this.userRepository.existsByPublicId(publicId)) {
            publicId = PublicIdGeneratorUtil.generate("USER", 5);
        }
        userEntity.setPublicId(publicId);
        userEntity.setPassword(PasswordUtil.getPasswordEncoder().encode(password));

        UserEntity userCreated = this.userRepository.save(userEntity);
        return this.getUserDetail(this.getById(userCreated.getId()));
    }

    @Override
    public UserDetailResponseDTO handleUpdateStatusById(
            Integer id,
            UserUpdateStatusRequestDTO request) {
        UserEntity userEntity = this.getById(id);
        userEntity.setStatus(request.getStatus());

        return this.getUserDetail(userEntity);
    }

    @Override
    public UserDetailResponseDTO handleChangePassword(
            Integer id,
            UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
        UserEntity userEntity = this.getById(id);

        String newPasswordRequest = userChangePasswordRequestDTO.getNewPassword();
        String newPassword2Request = userChangePasswordRequestDTO.getNewPassword2();
        PasswordUtil.handlePasswordIsNotMatch(newPasswordRequest, newPassword2Request);
        PasswordUtil.handlePasswordIsUsing(userEntity.getPassword(), newPasswordRequest);

        String newPassword = PasswordUtil.getPasswordEncoder().encode(newPasswordRequest);
        userEntity.setPassword(newPassword);

        return this.getUserDetail(userEntity);
    }

    @Override
    public UserDetailResponseDTO handleDelete(Integer id, UserDeleteRequestDTO userDeleteRequestDTO) {
        UserEntity userEntity = this.getById(id);
        this.userMapper.deleteEntityFromRequest(userDeleteRequestDTO, userEntity);

        return this.getUserDetail(userEntity);
    }

    @Override
    public String userTestConnect(String fullname, String username) {
        if (fullname.equalsIgnoreCase("tranthanhquy")
                && username.equalsIgnoreCase("thanhquyfu")) {
            UserCreatedEventDTO userCreatedTopicDTO = new UserCreatedEventDTO();
            userCreatedTopicDTO.setFullname(fullname);
            userCreatedTopicDTO.setUsername(username);

            this.producerService.sendUserCreatedTopic(userCreatedTopicDTO);

            return "Đăng ký thành công!";
        }

        return "Đăng ký thất bại!";
    }
}
