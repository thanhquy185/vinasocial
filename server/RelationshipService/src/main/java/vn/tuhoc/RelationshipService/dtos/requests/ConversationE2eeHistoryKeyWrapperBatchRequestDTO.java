package vn.tuhoc.RelationshipService.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eeHistoryKeyWrapperBatchRequestDTO {
    @NotEmpty
    @Size(max = 50)
    List<@Valid ConversationE2eeHistoryKeyWrapperItemRequestDTO> wrappers;
}
