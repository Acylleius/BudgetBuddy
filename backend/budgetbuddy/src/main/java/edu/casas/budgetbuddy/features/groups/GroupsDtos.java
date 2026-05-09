package edu.casas.budgetbuddy.features.groups;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class GroupsDtos {
    private GroupsDtos() {
    }

    public record GroupRequest(@NotBlank String name, String description) {
    }

    public record AddMemberRequest(@Email @NotBlank String email) {
    }

    public record MemberDto(Long userId, String email, String firstname, String lastname, String role) {
    }

    public record GroupDto(Long id, String name, String description, Long createdBy, String role) {
    }

    public record GroupDetailDto(Long id, String name, String description, Long createdBy,
                                 List<MemberDto> members) {
    }
}
