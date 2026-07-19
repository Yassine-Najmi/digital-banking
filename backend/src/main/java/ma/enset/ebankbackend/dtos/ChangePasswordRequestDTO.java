package ma.enset.ebankbackend.dtos;

public record ChangePasswordRequestDTO(String oldPassword, String newPassword) {
}
