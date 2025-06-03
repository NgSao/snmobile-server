package com.snd.server.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snd.server.dto.request.ConfigDetailRequest;
import com.snd.server.dto.request.ConfigRequest;
import com.snd.server.dto.response.ConfigDetailResponse;
import com.snd.server.dto.response.ConfigResponse;
import com.snd.server.model.Config;
import com.snd.server.model.ConfigDetail;

@Component
public class ConfigMapper {
        public ConfigResponse configToDto(Config config) {
                ConfigResponse dto = new ConfigResponse();
                dto.setId(config.getId());
                dto.setStoreName(config.getStoreName());
                dto.setStoreEmail(config.getStoreEmail());
                dto.setStorePhone(config.getStorePhone());
                dto.setStoreAddress(config.getStoreAddress());
                dto.setStoreLogo(config.getStoreLogo());
                dto.setStoreDescription(config.getStoreDescription());
                dto.setFreeShippingThreshold(config.getFreeShippingThreshold());
                dto.setCreatedAt(config.getCreatedAt());
                dto.setUpdatedAt(config.getUpdatedAt());
                dto.setCreatedBy(config.getCreatedBy());
                dto.setUpdatedBy(config.getUpdatedBy());
                dto.setStatus(config.getStatus());
                dto.setStatusName(config.getStatus().getStatusName());
                dto.setBranches(config.getBranches().stream()
                                .map(branch -> this.configDetailToDto(branch))
                                .collect(Collectors.toSet()));
                return dto;
        }

        public ConfigDetailResponse configDetailToDto(ConfigDetail detail) {
                ConfigDetailResponse dto = new ConfigDetailResponse();
                dto.setId(detail.getId());
                dto.setStorePhone(detail.getStorePhone());
                dto.setStoreAddress(detail.getStoreAddress());
                dto.setDistrictName(detail.getDistrictName());
                dto.setWardName(detail.getWardName());
                dto.setCity(detail.getCity());
                dto.setDistrictId(detail.getDistrictId());
                dto.setWardCode(detail.getWardCode());
                dto.setLatitude(detail.getLatitude());
                dto.setLongitude(detail.getLongitude());
                dto.setConfigId(detail.getConfig().getId());
                dto.setCreatedAt(detail.getCreatedAt());
                dto.setUpdatedAt(detail.getUpdatedAt());
                dto.setCreatedBy(detail.getCreatedBy());
                dto.setUpdatedBy(detail.getUpdatedBy());
                dto.setStatus(detail.getStatus());
                dto.setStatusName(detail.getStatus().getStatusName());

                return dto;
        }

        public Config configToEntity(ConfigRequest request) {
                Config config = new Config();
                config.setStoreName(request.getStoreName());
                config.setStoreEmail(request.getStoreEmail());
                config.setStorePhone(request.getStorePhone());
                config.setStoreAddress(request.getStoreAddress());
                config.setStoreDescription(request.getStoreDescription());
                config.setFreeShippingThreshold(request.getFreeShippingThreshold());
                config.setStoreLogo(request.getStoreLogo());

                if (request.getStatus() != null) {
                        config.setStatus(request.getStatus());
                }
                if (request.getBranches() != null) {
                        config.setBranches(request.getBranches().stream()
                                        .map(branch -> this.configDetailToEntity(config, branch))
                                        .collect(Collectors.toSet()));
                }
                return config;
        }

        public ConfigDetail configDetailToEntity(Config config, ConfigDetailRequest request) {
                ConfigDetail entity = new ConfigDetail();
                entity.setStorePhone(request.getStorePhone());
                entity.setStoreAddress(request.getStoreAddress());
                entity.setDistrictName(request.getDistrictName());
                entity.setWardName(request.getWardName());
                entity.setCity(request.getCity());
                entity.setDistrictId(request.getDistrictId());
                entity.setWardCode(request.getWardCode());
                entity.setLatitude(request.getLatitude());
                entity.setLongitude(request.getLongitude());
                entity.setConfig(config);
                if (request.getStatus() != null) {
                        entity.setStatus(request.getStatus());
                }
                return entity;
        }

        public Config configToUpdated(Config config, ConfigRequest request) {
                config.setStoreName(request.getStoreName() != null ? request.getStoreName() : config.getStoreName());
                config.setStoreEmail(
                                request.getStoreEmail() != null ? request.getStoreEmail() : config.getStoreEmail());
                config.setStorePhone(
                                request.getStorePhone() != null ? request.getStorePhone() : config.getStorePhone());
                config.setStoreAddress(
                                request.getStoreAddress() != null ? request.getStoreAddress()
                                                : config.getStoreAddress());
                config.setStoreLogo(request.getStoreLogo() != null ? request.getStoreLogo() : config.getStoreLogo());
                config.setStoreDescription(
                                request.getStoreDescription() != null ? request.getStoreDescription()
                                                : config.getStoreDescription());
                config.setFreeShippingThreshold(
                                request.getFreeShippingThreshold() != null ? request.getFreeShippingThreshold()
                                                : config.getFreeShippingThreshold());

                if (request.getBranches() != null) {
                        config.setBranches(
                                        request.getBranches().stream()
                                                        .map(branch -> this.configDetailToUpdated(config, null, branch))
                                                        .collect(Collectors.toSet()));
                }
                return config;
        }

        public ConfigDetail configDetailToUpdated(Config config, ConfigDetail configDetail,
                        ConfigDetailRequest request) {
                configDetail.setStorePhone(
                                request.getStorePhone() != null ? request.getStorePhone()
                                                : configDetail.getStorePhone());
                configDetail.setStoreAddress(
                                request.getStoreAddress() != null ? request.getStoreAddress()
                                                : configDetail.getStoreAddress());
                configDetail.setDistrictName(
                                request.getDistrictName() != null ? request.getDistrictName()
                                                : configDetail.getDistrictName());
                configDetail.setWardName(
                                request.getWardName() != null ? request.getWardName() : configDetail.getWardName());
                configDetail.setCity(request.getCity() != null ? request.getCity() : configDetail.getCity());
                configDetail.setDistrictId(
                                request.getDistrictId() != null ? request.getDistrictId()
                                                : configDetail.getDistrictId());
                configDetail.setWardCode(
                                request.getWardCode() != null ? request.getWardCode() : configDetail.getWardCode());
                configDetail.setLatitude(
                                request.getLatitude() != null ? request.getLatitude() : configDetail.getLatitude());
                configDetail
                                .setLongitude(request.getLongitude() != null ? request.getLongitude()
                                                : configDetail.getLongitude());
                if (config != null) {
                        configDetail.setConfig(config);
                }
                return configDetail;
        }

}
