package com.yappy.search_engine.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

public class Util {
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    public static String loadAsString(final String path) {
        try {
            Resource resource = new ClassPathResource(path);
            byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return new String(bdata, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
