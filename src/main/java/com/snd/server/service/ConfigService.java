package com.snd.server.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.snd.server.dto.request.ConfigDetailRequest;
import com.snd.server.dto.request.ConfigRequest;
import com.snd.server.dto.response.ConfigDetailResponse;
import com.snd.server.dto.response.ConfigResponse;
import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.ConfigMapper;
import com.snd.server.model.Config;
import com.snd.server.model.ConfigDetail;
import com.snd.server.repository.ConfigDetailRepository;
import com.snd.server.repository.ConfigRepository;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;
    private final ConfigDetailRepository configDetailRepository;
    private final ConfigMapper configMapper;

    public ConfigService(ConfigRepository configRepository, ConfigDetailRepository configDetailRepository,
            ConfigMapper configMapper) {
        this.configRepository = configRepository;
        this.configDetailRepository = configDetailRepository;
        this.configMapper = configMapper;
    }

    public List<ConfigResponse> getAllConfigs() {
        return configRepository.findAll().stream()
                .map(configMapper::configToDto)
                .toList();
    }

    public List<ConfigResponse> getConfigsByIds(List<Long> ids) {
        return configRepository.findAllById(ids).stream()
                .map(configMapper::configToDto)
                .toList();
    }

    public ConfigResponse createConfig(ConfigRequest request) {
        Config savedConfig = configMapper.configToEntity(request);
        configRepository.save(savedConfig);
        return configMapper.configToDto(savedConfig);
    }

    public ConfigResponse updateConfig(Long id, ConfigRequest request) {
        Config exConfig = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        configMapper.configToUpdated(exConfig, request);
        return configMapper.configToDto(exConfig);
    }

    public void hiddenConfig(Long id) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        config.markAsConfigDetail(EntityStatusEnum.HIDDEN);
        configRepository.save(config);
    }

    public void trashConfig(Long id) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        config.markAsConfigDetail(EntityStatusEnum.TRASH);
        configRepository.save(config);
    }

    public void restoreConfig(Long id) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        config.markAsConfigDetail(EntityStatusEnum.ACTIVE);
        configRepository.save(config);
    }

    public void deleteConfig(Long id) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        config.markAsConfigDetail(EntityStatusEnum.DELETED);
        configRepository.save(config);
    }

    public ConfigDetailResponse getBranch(Long id) {
        ConfigDetail configDetail = configDetailRepository.findById(id)
                .orElseThrow(() -> new AppException("ConfigDetail not found"));
        return configMapper.configDetailToDto(configDetail);
    }

    public ConfigDetailResponse addBranch(Long configId, ConfigDetailRequest request) {
        Config config = configRepository.findById(configId)
                .orElseThrow(() -> new AppException("Config not found"));
        ConfigDetail configDetail = configMapper.configDetailToEntity(config, request);
        configDetailRepository.save(configDetail);
        return configMapper.configDetailToDto(configDetail);
    }

    public ConfigDetailResponse updateBranch(Long id, ConfigDetailRequest request) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new AppException("Config not found"));
        ConfigDetail exConfigDetail = configDetailRepository.findById(id)
                .orElseThrow(() -> new AppException("ConfigDetail not found"));
        ConfigDetail configDetail = configMapper.configDetailToUpdated(config, exConfigDetail, request);
        configDetailRepository.save(configDetail);
        return configMapper.configDetailToDto(configDetail);
    }

    public void hiddenBranches(Set<Long> branchIds) {
        List<ConfigDetail> branches = configDetailRepository.findAllById(branchIds);
        if (branches.isEmpty()) {
            throw new AppException("No ConfigDetail found for given ids");
        }
        branches.forEach(branch -> branch.setStatus(EntityStatusEnum.HIDDEN));
        configDetailRepository.saveAll(branches);
    }

    public void restoreBranches(Set<Long> branchIds) {
        List<ConfigDetail> branches = configDetailRepository.findAllById(branchIds);
        if (branches.isEmpty()) {
            throw new AppException("No ConfigDetail found for given ids");
        }
        branches.forEach(branch -> branch.setStatus(EntityStatusEnum.ACTIVE));
        configDetailRepository.saveAll(branches);
    }

    public void trashBranches(Set<Long> branchIds) {
        List<ConfigDetail> branches = configDetailRepository.findAllById(branchIds);
        if (branches.isEmpty()) {
            throw new AppException("No ConfigDetail found for given ids");
        }
        branches.forEach(branch -> branch.setStatus(EntityStatusEnum.TRASH));
        configDetailRepository.saveAll(branches);
    }

    public void deleteBranches(Set<Long> branchIds) {
        List<ConfigDetail> branches = configDetailRepository.findAllById(branchIds);
        if (branches.isEmpty()) {
            throw new AppException("No ConfigDetail found for given ids");
        }
        branches.forEach(branch -> branch.setStatus(EntityStatusEnum.DELETED));
        configDetailRepository.saveAll(branches);
    }

}
