package dev.sentomero.backend_ams.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class KpClientDto {
    private Integer kpClientId;
    private String kpClientFName;
    private String kpClientLName;
    private Long kpClientSerialNumber;
    private String registeredBy;
    private String categoryRegistered;
    private LocalDateTime kpClientTimeAssigned;

    public KpClientDto(Integer kpClientId, String kpClientFName, String kpClientLName, Long kpClientSerialNumber, String registeredBy, String categoryRegistered, LocalDateTime kpClientTimeAssigned) {
        this.kpClientId = kpClientId;
        this.kpClientFName = kpClientFName;
        this.kpClientLName = kpClientLName;
        this.kpClientSerialNumber = kpClientSerialNumber;
        this.registeredBy = registeredBy;
        this.categoryRegistered = categoryRegistered;
        this.kpClientTimeAssigned = kpClientTimeAssigned;
    }
}
