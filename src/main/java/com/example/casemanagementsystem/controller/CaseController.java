package com.example.casemanagementsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.casemanagementsystem.dto.CaseDto;
import com.example.casemanagementsystem.enums.CaseStatus;
import com.example.casemanagementsystem.enums.CaseType;
import com.example.casemanagementsystem.service.CaseService;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    // Constructor injection
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<CaseDto> createCase(@RequestBody CaseDto.CreateCaseRequest request) {
        CaseDto createdCase = caseService.createCase(request);
        return ResponseEntity.ok(createdCase);
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<CaseDto> getCase(@PathVariable String caseId) {
        CaseDto caseDto = caseService.getCase(caseId);
        return ResponseEntity.ok(caseDto);
    }

    @PutMapping("/{caseId}/assign")
    public ResponseEntity<CaseDto> assignCase(
            @PathVariable String caseId,
            @RequestBody CaseDto.AssignCaseRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CaseDto assignedCase = caseService.assignCase(caseId, request.getAssigneeId(), userId);
        return ResponseEntity.ok(assignedCase);
    }

    @PutMapping("/{caseId}/suspend")
    public ResponseEntity<CaseDto> suspendCase(
            @PathVariable String caseId,
            @RequestBody CaseDto.UpdateCaseStatusRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CaseDto suspendedCase = caseService.suspendCase(caseId, userId, request.getReason());
        return ResponseEntity.ok(suspendedCase);
    }

    @PutMapping("/{caseId}/resume")
    public ResponseEntity<CaseDto> resumeCase(
            @PathVariable String caseId,
            @RequestBody CaseDto.UpdateCaseStatusRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CaseDto resumedCase = caseService.resumeCase(caseId, userId, request.getReason());
        return ResponseEntity.ok(resumedCase);
    }

    @PutMapping("/{caseId}/close")
    public ResponseEntity<CaseDto> closeCase(
            @PathVariable String caseId,
            @RequestBody CaseDto.UpdateCaseStatusRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CaseDto closedCase = caseService.closeCase(caseId, userId, request.getReason());
        return ResponseEntity.ok(closedCase);
    }

    @PutMapping("/{caseId}")
    public ResponseEntity<CaseDto> updateCase(
            @PathVariable String caseId,
            @RequestBody CaseDto.UpdateCaseRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CaseDto updatedCase = caseService.updateCase(caseId, request, userId);
        return ResponseEntity.ok(updatedCase);
    }

    @GetMapping
    public ResponseEntity<List<CaseDto>> getCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) CaseType caseType,
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        
        List<CaseDto> cases;
        
        if (activeOnly) {
            cases = caseService.getActiveCases();
        } else if (status != null) {
            cases = caseService.getCasesByStatus(status);
        } else if (caseType != null) {
            cases = caseService.getCasesByType(caseType);
        } else if (creatorId != null) {
            cases = caseService.getCasesByCreator(creatorId);
        } else if (assigneeId != null) {
            cases = caseService.getCasesByAssignee(assigneeId);
        } else {
            cases = caseService.getActiveCases(); // Default to active cases
        }
        
        return ResponseEntity.ok(cases);
    }
}