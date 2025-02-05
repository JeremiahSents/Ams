package dev.sentomero.backend_ams.service.impl;

import dev.sentomero.backend_ams.dto.KpClientDto;
import dev.sentomero.backend_ams.models.AmsUser;
import dev.sentomero.backend_ams.models.Category;
import dev.sentomero.backend_ams.models.KpClient;
import dev.sentomero.backend_ams.repository.AmsUserRepository;
import dev.sentomero.backend_ams.repository.CategoryRepository;
import dev.sentomero.backend_ams.repository.KpClientRepository;
import dev.sentomero.backend_ams.service.KpClientService;
import dev.sentomero.backend_ams.service.SerialNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KpClientServiceImpl implements KpClientService {

    private final KpClientRepository kpClientRepository;
    private final AmsUserRepository amsUserRepository;
    private final CategoryRepository categoryRepository;
    private final SerialNumberService serialNumberService;

    @Autowired
    public KpClientServiceImpl(KpClientRepository kpClientRepository,
                               AmsUserRepository amsUserRepository,
                               CategoryRepository categoryRepository,
                               SerialNumberService serialNumberService) {
        this.kpClientRepository = kpClientRepository;
        this.amsUserRepository = amsUserRepository;
        this.categoryRepository = categoryRepository;
        this.serialNumberService = serialNumberService;
    }

    private KpClientDto convertToDto(KpClient client) {
        KpClientDto dto = new KpClientDto();
        dto.setKpClientId(client.getKpClientId());
        dto.setKpClientFName(client.getKpClientFName());
        dto.setKpClientLName(client.getKpClientLName());
        dto.setKpClientSerialNumber(client.getKpClientSerialNumber());
        dto.setRegisteredBy(client.getRegisteredBy().getAmsUsername());
        
        System.out.println("Converting client ID: " + client.getKpClientId());
        if (client.getCategory() != null) {
            System.out.println("Category found: " + client.getCategory().getName());
            dto.setCategoryRegistered(client.getCategory().getName());
        } else {
            System.out.println("No category found for client");
        }
        
        dto.setKpClientTimeAssigned(client.getKpClientTimeAssigned());
        return dto;
    }

    @Override
    public KpClientDto saveClient(KpClientDto clientDto) {
        KpClient client = new KpClient();
        client.setKpClientFName(clientDto.getKpClientFName());
        client.setKpClientLName(clientDto.getKpClientLName());
        client.setKpClientSerialNumber(clientDto.getKpClientSerialNumber());
        
        // Find user by username
        AmsUser registeredBy = amsUserRepository.findByAmsUsername(clientDto.getRegisteredBy())
            .orElseThrow(() -> new RuntimeException("User not found with username: " + clientDto.getRegisteredBy()));
        client.setRegisteredBy(registeredBy);
        
        // Set category if provided
        if (clientDto.getCategoryRegistered() != null && !clientDto.getCategoryRegistered().isEmpty()) {
            Category category = categoryRepository.findByName(clientDto.getCategoryRegistered())
                .orElseThrow(() -> new RuntimeException("Category not found: " + clientDto.getCategoryRegistered()));
            client.setCategory(category);
        }
        
        client.setKpClientTimeAssigned(LocalDateTime.now());
        
        KpClient savedClient = kpClientRepository.save(client);
        return convertToDto(savedClient);
    }

    @Override
    public List<KpClientDto> getAllClients() {
        List<KpClient> clients = kpClientRepository.findAll();
        System.out.println("Found " + clients.size() + " clients");
        
        return clients.stream()
            .peek(client -> {
                System.out.println("Processing client ID: " + client.getKpClientId());
                System.out.println("Category: " + (client.getCategory() != null ? 
                    client.getCategory().getName() : "null"));
            })
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public KpClientDto getKpClientById(int id) {
        KpClient kpClient = kpClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
        return convertToDto(kpClient);
    }

    @Override
    public KpClientDto getKpClientBySerialNumber(long serialNumber) {
        return convertToDto(kpClientRepository.findByKpClientSerialNumber(serialNumber)
            .orElseThrow(() -> new RuntimeException("Client not found with serial number: " + serialNumber)));
    }

    @Override
    public KpClientDto updateKpClient(int id, KpClientDto clientDto) {
        KpClient existingClient = kpClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        existingClient.setKpClientFName(clientDto.getKpClientFName());
        existingClient.setKpClientLName(clientDto.getKpClientLName());
        
        // Update category if provided
        if (clientDto.getCategoryRegistered() != null && 
            !clientDto.getCategoryRegistered().isEmpty()) {
            Category category = categoryRepository.findByName(clientDto.getCategoryRegistered())
                .orElseThrow(() -> new RuntimeException("Category not found: " + clientDto.getCategoryRegistered()));
            existingClient.setCategory(category);
        }

        return convertToDto(kpClientRepository.save(existingClient));
    }

    @Override
    public void deleteKpClient(int id) {
        KpClient existingClient = kpClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
        kpClientRepository.delete(existingClient);
    }
}