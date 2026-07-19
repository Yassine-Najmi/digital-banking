package ma.enset.ebankbackend.dtos;

public record TransferRequestDTO(
        String accountSource,
        String accountDestination,
        double amount,
        String description
) {
}
