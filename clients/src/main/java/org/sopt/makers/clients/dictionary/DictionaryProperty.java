package org.sopt.makers.clients.dictionary;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dictionary")
public record DictionaryProperty(String key) {}
