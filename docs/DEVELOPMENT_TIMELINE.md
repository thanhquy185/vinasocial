# Timeline phát triển VinaSocial

> Tài liệu được dựng lại từ lịch sử hội thoại của các task Codex có cùng thư mục dự án và đối chiếu với mã nguồn. Lịch sử cho phép xác định ngày và thứ tự trao đổi; giờ ghi theo giờ Việt Nam (UTC+7). Thư mục dự án gốc hiện không có Git history nên không thể xác định chính xác thời điểm từng thay đổi được ghi vào file hoặc commit.

## Các giai đoạn

### 14/10/2026 — Khảo sát dự án, chạy website và chỉnh trang hồ sơ

- Xem cấu trúc VinaSocial: website Next.js ở `client/website`, các microservice Spring ở `server`; mở `package.json` và tóm tắt thư mục.
- Khởi động frontend trên `http://127.0.0.1:3000`. Backend chưa được khởi động trong lượt đó.
- Sửa `ProfileComponent` ở Header để vùng thông tin người dùng bấm được và điều hướng tới `/{username}`; xử lý việc vùng này nằm trong `div` bao ngoài.
- Dựa trên ảnh tham khảo Instagram, thu gọn giao diện Edit Profile (chiều rộng form, cỡ chữ, chiều cao input, avatar, khoảng cách), rồi tăng nhẹ lại vì vòng trước nhỏ quá. Mở trang `/tquy18/edit` để xem giao diện.

### 15/10/2026 — Thay dữ liệu mock hồ sơ bằng API

- Đọc luồng client service/API và server controller/service/DTO; thay luồng hồ sơ mock bằng truy vấn hồ sơ và bài viết theo username.
- Server phân biệt hồ sơ chi tiết cho chủ tài khoản với hồ sơ công khai cho người khác; lọc thông tin và bài viết theo quyền riêng tư. Client có trạng thái tải, lỗi, hồ sơ riêng tư và không tìm thấy.
- Các tab chưa có API (bài đăng lại, ảnh được gắn thẻ, tin nổi bật) được ghi nhận là chưa nối dữ liệu thật.
- Đổi trang hồ sơ sang `useEntityQuery` cho truy vấn hồ sơ và bài viết; mở rộng hook để nhận kiểu tham số tổng quát và response envelope `RestResponseType`. ESLint qua các file liên quan; `tsc --noEmit` vẫn có lỗi ở một số khu vực khác của frontend.
- Điều tra lỗi Feign/Jackson đọc `RestResponseDTO` từ `/users/profile/{username}`. Xác nhận `/users/info` là API danh sách; hồ sơ theo username là một object trong envelope.
- Đã thử DTO envelope cục bộ cho Feign, sau đó theo yêu cầu của người dùng khôi phục kiểu response cũ và bỏ DTO mới. Lỗi giải mã Feign được để lại để người dùng tự xử lý, theo chỉ dẫn trực tiếp.
- Các build `compileJava` của UserService, ContentService và GatewayService chạy thành công trong lượt nối luồng xem hồ sơ. Frontend có lỗi TypeScript tồn tại ở admin, create-post, feed và OTP.

### 15/10/2026 — Nối chức năng cập nhật Profile Settings

- Thêm luồng xem hồ sơ chi tiết của chính mình để có thể vào trang cài đặt; người khác vẫn nhận hồ sơ công khai.
- Nối `ProfileSettingsComponent` với dữ liệu API và các API lưu hồ sơ. Tách cập nhật trang cá nhân khỏi thông tin cá nhân bằng `ProfileUpdateRequestDTO` và `ProfilePersonalInformationUpdateRequestDTO`.
- Tách request cập nhật privacy và notification theo loại trường cần đổi; không gửi detail response DTO làm request.
- Chuyển upload avatar sang `useEntityMutation` theo pattern của `MediaDialogComponent`; dùng `ValidationUtil` cho các kiểm tra null phù hợp ở server.
- Sau góp ý, khôi phục đầy đủ giao diện `BestFriendsComponent` cũ. Vì chưa có API quản lý danh sách bạn thân, component hiển thị trạng thái giữ chỗ và nút tìm người.
- Hoàn tất phần tiếp nối ở client/server; thay đổi tập trung vào các service, DTO, form profile, personal information, privacy và notification.

### 16/10/2026 — Rà soát viền các component cài đặt

- Bỏ viền bao ngoài của `BestFriendsComponent` và `PrivacyComponent`, giữ lại thiết kế và các đường viền bên trong.

### 20/10/2026 — Rà cấu trúc code và phát triển bảo mật 2FA

- Rà cấu trúc client/server theo yêu cầu giữ convention và nghiệp vụ hiện tại. Chuyển kiểu Story sang `shared/types/StoryViewType.ts`, ánh xạ response sang model giao diện ở `utils/story.ts`, và dùng `StoryMapper` trong `StoryServiceImplement` theo pattern sẵn có. Không chạy build/test cho lượt rà cấu trúc.
- Thảo luận các cơ chế bảo mật bổ sung; xác nhận ReBAC/ABAC đã được dùng trong dự án và chọn 2FA làm tính năng mới, do E2EE đã có.
- Triển khai luồng OTP cho đăng nhập: chỉ cấp access token/refresh cookie sau khi OTP hợp lệ; OTP lưu bằng AES-256-GCM, hết hạn sau 90 giây và khóa sau 5 lần nhập sai. Nối gửi OTP qua Kafka tới email/SMS, thêm cài đặt bật/tắt 2FA cần mật khẩu và OTP, thu hồi refresh session hiện tại khi bật, cùng migration và cấu hình khóa.
- Sửa `@Transactional(noRollbackOn=...)` thành `noRollbackFor` để lần nhập OTP sai vẫn ghi nhận số lần thất bại. Chưa xác nhận sửa bằng build vì Gradle không ghi được cache trong `~/.gradle`.
- Khi UserService khởi động thiếu khóa AES, bổ sung thông báo lỗi rõ hơn và hướng dẫn README; tạo khóa local 32 byte trong `otp-encryption-key.properties`, thêm file vào `.gitignore`. Đã kiểm tra khóa giải mã Base64 đủ 32 byte; chưa khởi động lại service để xác nhận.
- Tạo tài liệu quy trình 2FA ở [2FA-IMPLEMENTATION.md](./2FA-IMPLEMENTATION.md), gồm luồng, API, thay đổi database, mã hóa, cấu hình và giới hạn hiện tại.

## Trạng thái mã nguồn khi lập tài liệu

- Website có `ProfileComponent` dùng `useEntityQuery` để lấy hồ sơ theo username và `ProfileSettingsComponent` quản lý các mục cài đặt.
- Biểu mẫu chỉnh hồ sơ và thông tin cá nhân gọi API riêng; ảnh đại diện được tải bằng mutation.
- Các DTO cập nhật riêng tư/thông báo và cập nhật hồ sơ/thông tin cá nhân hiện diện ở UserService.
- `BestFriendsComponent` vẫn là giao diện giữ chỗ vì nội dung hiện tại xác nhận service quan hệ chưa có API quản lý danh sách bạn thân.
- Mã frontend nằm trong Git repository con `client/website`; thư mục gốc `vinasocial` và phần `server` không có Git metadata ở đường dẫn đang kiểm tra. Lịch sử hội thoại Codex vẫn tra được, nhưng log commit không thể dùng để xác định ngày thay đổi file.
- Task 2FA có kiểm tra build chưa hoàn tất do môi trường Gradle bị chặn quyền ghi cache; sau đó khóa local được tạo nhưng UserService chưa được khởi động lại để xác thực end-to-end.

## Các điểm cần kiểm tra tiếp khi tiếp tục

- Chạy lại luồng xem hồ sơ của chính mình và hồ sơ người khác để xác nhận response đúng loại, dữ liệu riêng tư không lộ ra ở hồ sơ công khai.
- Xử lý lỗi giải mã Feign của envelope `RestResponseDTO` theo hướng người dùng dự định; tại thời điểm ghi timeline, người dùng đã yêu cầu khôi phục kiểu cũ và tự xử lý lỗi này.
- Chạy thử cập nhật từng nhóm thông tin, quyền riêng tư, thông báo và ảnh đại diện với các service đang hoạt động.
- Nếu cần quản lý danh sách bạn thân thực sự, bổ sung API/service quan hệ trước khi thay giao diện giữ chỗ bằng dữ liệu thật.

