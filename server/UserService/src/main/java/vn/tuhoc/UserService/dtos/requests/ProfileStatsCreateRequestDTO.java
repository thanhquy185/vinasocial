package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
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
public class ProfileStatsCreateRequestDTO {
    @NotNull(message = "Mã thông tin không được để trống!")
    Integer profileId;

    @NotNull(message = "Số lượng media không được để trống!")
    Integer mediaCount;

    @NotNull(message = "Số lượng bạn bè không được để trống!")
    Integer friendCount;

    @NotNull(message = "Số lượng bạn thân không được để trống!")
    Integer bestFriendCount;

    @NotNull(message = "Số lượng người theo dõi không được để trống!")
    Integer followerCount;

    @NotNull(message = "Số lượng đang theo dõi không được để trống!")
    Integer followingCount;

    @NotNull(message = "Số lượng cập nhật không được để trống!")
    Integer updatedCount;
}
