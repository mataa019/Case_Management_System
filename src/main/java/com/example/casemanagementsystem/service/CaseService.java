package com.example.casemanagementsystem.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.casemanagementsystem.dto.CaseDto;
import com.example.casemanagementsystem.entity.CaseEntity;
import com.example.casemanagementsystem.enums.ActionType;
import com.example.casemanagementsystem.enums.CaseStatus;
import com.example.casemanagementsystem.enums.CaseType;
import com.example.casemanagementsystem.repository.CaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CaseService {

    private final CaseRepository caseRepository;
    private final RuntimeService runtimeService;
    private final LoggingService loggingService;

    public CaseDto createCase(CaseDto.CreateCaseRequest request) {
        CaseEntity caseEntity = CaseEntity.builder()
                .caseId(generateCaseId())
                .caseType(request.getCaseType())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .creatorId(request.getCreatorId())
                .status(CaseStatus.DRAFT)
                .build();

        CaseEntity savedCase = caseRepository.save(caseEntity);

        // Start Flowable process instance
        String processInstanceId = runtimeService.startProcessInstanceByKey("caseLifecycle", Map.of(
                "caseId", savedCase.getCaseId(),
                "creatorId", savedCase.getCreatorId(),
                "status", savedCase.getStatus().toString()
        )).getId();

        savedCase.setFlowableProcessInstanceId(processInstanceId);
        savedCase = caseRepository.save(savedCase);

        loggingService.logAction(request.getCreatorId(), ActionType.CASE_CREATED, "CASE", 
                savedCase.getCaseId(), "Case created: " + savedCase.getTitle());

        return convertToDto(savedCase);
    }

    public CaseDto assignCase(String caseId, String assigneeId, String userId) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        
        if (!caseEntity.getStatus().canTransitionTo(CaseStatus.READY)) {
            throw new RuntimeException("Cannot assign case in current status: " + caseEntity.getStatus());
        }

        String oldAssignee = caseEntity.getAssigneeId();
        caseEntity.setAssigneeId(assigneeId);
        caseEntity.setStatus(CaseStatus.READY);

        CaseEntity savedCase = caseRepository.save(caseEntity);

        loggingService.logAction(userId, ActionType.CASE_ASSIGNED, "CASE", caseId, 
                "Case assigned to: " + assigneeId, oldAssignee, assigneeId);

        return convertToDto(savedCase);
    }

    public CaseDto suspendCase(String caseId, String userId, String reason) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        
        if (!caseEntity.getStatus().canTransitionTo(CaseStatus.SUSPENDED)) {
            throw new RuntimeException("Cannot suspend case in current status: " + caseEntity.getStatus());
        }

        CaseStatus oldStatus = caseEntity.getStatus();
        caseEntity.setStatus(CaseStatus.SUSPENDED);

        CaseEntity savedCase = caseRepository.save(caseEntity);

        String description = reason != null ? "Case suspended: " + reason : "Case suspended";
        loggingService.logAction(userId, ActionType.CASE_SUSPENDED, "CASE", caseId, description, 
                oldStatus.toString(), CaseStatus.SUSPENDED.toString());

        return convertToDto(savedCase);
    }

    public CaseDto resumeCase(String caseId, String userId, String reason) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        
        if (!caseEntity.getStatus().canTransitionTo(CaseStatus.READY)) {
            throw new RuntimeException("Cannot resume case in current status: " + caseEntity.getStatus());
        }

        CaseStatus oldStatus = caseEntity.getStatus();
        caseEntity.setStatus(CaseStatus.READY);

        CaseEntity savedCase = caseRepository.save(caseEntity);

        String description = reason != null ? "Case resumed: " + reason : "Case resumed";
        loggingService.logAction(userId, ActionType.CASE_RESUMED, "CASE", caseId, description, 
                oldStatus.toString(), CaseStatus.READY.toString());

        return convertToDto(savedCase);
    }

    public CaseDto closeCase(String caseId, String userId, String reason) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        
        if (!caseEntity.getStatus().canTransitionTo(CaseStatus.CLOSED)) {
            throw new RuntimeException("Cannot close case in current status: " + caseEntity.getStatus());
        }

        CaseStatus oldStatus = caseEntity.getStatus();
        caseEntity.setStatus(CaseStatus.CLOSED);

        CaseEntity savedCase = caseRepository.save(caseEntity);

        String description = reason != null ? "Case closed: " + reason : "Case closed";
        loggingService.logAction(userId, ActionType.CASE_CLOSED, "CASE", caseId, description, 
                oldStatus.toString(), CaseStatus.CLOSED.toString());

        return convertToDto(savedCase);
    }

    public CaseDto updateCase(String caseId, CaseDto.UpdateCaseRequest request, String userId) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        
        // Update the case fields
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            caseEntity.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            caseEntity.setDescription(request.getDescription());
        }
        
        if (request.getPriority() != null && !request.getPriority().trim().isEmpty()) {
            caseEntity.setPriority(request.getPriority());
        }
        
        // Save the updated case
        CaseEntity updatedCase = caseRepository.save(caseEntity);
        
        // Log the action
        loggingService.logAction(userId, ActionType.CASE_UPDATED, "CASE", 
                caseEntity.getCaseId(), "Case updated: " + caseEntity.getTitle());
        
        return convertToDto(updatedCase);
    }

    public CaseDto getCase(String caseId) {
        CaseEntity caseEntity = findCaseByCaseId(caseId);
        return convertToDto(caseEntity);
    }

    public List<CaseDto> getCasesByStatus(CaseStatus status) {
        return caseRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getCasesByType(CaseType caseType) {
        return caseRepository.findByCaseType(caseType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getCasesByCreator(String creatorId) {
        return caseRepository.findByCreatorId(creatorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getCasesByAssignee(String assigneeId) {
        return caseRepository.findByAssigneeId(assigneeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getActiveCases() {
        return caseRepository.findActiveCases().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CaseEntity findCaseByCaseId(String caseId) {
        return caseRepository.findByCaseId(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + caseId));
    }

    private CaseDto convertToDto(CaseEntity caseEntity) {
        return CaseDto.builder()
                .id(caseEntity.getId())
                .caseId(caseEntity.getCaseId())
                .caseType(caseEntity.getCaseType())
                .status(caseEntity.getStatus())
                .creatorId(caseEntity.getCreatorId())
                .assigneeId(caseEntity.getAssigneeId())
                .title(caseEntity.getTitle())
                .description(caseEntity.getDescription())
                .priority(caseEntity.getPriority())
                .flowableProcessInstanceId(caseEntity.getFlowableProcessInstanceId())
                .createdAt(caseEntity.getCreatedAt())
                .updatedAt(caseEntity.getUpdatedAt())
                .build();
    }

    private String generateCaseId() {
        return "CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}