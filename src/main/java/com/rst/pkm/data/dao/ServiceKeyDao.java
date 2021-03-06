package com.rst.pkm.data.dao;

import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Converter;
import com.rst.pkm.common.FileUtil;
import com.rst.pkm.data.entity.ServiceKey;
import com.google.gson.Gson;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hujia
 */
@Component
public class ServiceKeyDao {
    private final Logger logger = LoggerFactory.getLogger(ServiceKeyDao.class);
    private Map<String, ServiceKey> serviceKeyMap;
    @Value("${pkm.data-path}")
    private String savePath;

    public static final String FILE_NAME = "service-key";

    @Data
    private class FileData {
        private List<ServiceKey> serviceKeys = new ArrayList<>();
    }

    @PostConstruct
    void init() {
        serviceKeyMap = new HashMap<>();

        if (!savePath.endsWith(File.separator)) {
            savePath += File.separator;
        }


        byte[] content = FileUtil.safeLoad(savePath + FILE_NAME);
        if (content == null || content.length <= 0) {
            logger.info("warning:none local data!");
            return;
        }

        content = Converter.hexStringToByteArray(new String(content));
        content = AESUtil.aesDecrypt(content, Constant.ADMIN_KEY);

        if (content == null) {
            logger.error("local data or password incorrect!");
            return;
        }

        FileData fileData = new Gson().fromJson(new String(content), FileData.class);

        if (fileData == null || fileData.getServiceKeys() == null) {
            return;
        }

        fileData.getServiceKeys().forEach(
                serviceKey -> serviceKeyMap.put(serviceKey.getPubKeyHash(), serviceKey));
    }

    public boolean save(ServiceKey serviceKey) {
        serviceKeyMap.put(serviceKey.getPubKeyHash(), serviceKey);
        FileData data = new FileData();
        data.getServiceKeys().addAll(serviceKeyMap.values());
        boolean success;
        byte[] content = AESUtil.aesEncrypt(new Gson().toJson(data).getBytes(), Constant.ADMIN_KEY);

        return FileUtil.safeSave(Converter.byteArrayToHexString(content), savePath + FILE_NAME);
    }

    public ServiceKey findByPubHash(String pubHash) {
        return serviceKeyMap.get(pubHash);
    }
}
