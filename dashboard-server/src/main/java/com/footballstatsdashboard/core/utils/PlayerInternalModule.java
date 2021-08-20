package com.footballstatsdashboard.core.utils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Jackson module that filter out properties annotated with @InternalField
 */
@Provider
public class PlayerInternalModule extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerInternalModule.class);

    @Override
    public String getModuleName() { return "PlayerInternalModule"; }

    @Override
    public Version version() {
        return new Version(1, 0, 0, "SNAPSHOT", "com.footballstatsdashboard",
                "football-dashboard-parent");
    }

    @Override
    public void setupModule(SetupContext setupContext) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("initializing PlayerInternalModule");
        }

        setupContext.addBeanSerializerModifier(new PlayerInternalBeanSerializerModifier());
        setupContext.addBeanDeserializerModifier(new PlayerInternalBeanDeserializerModifier());
    }

    /**
     * Filters out properties marked with @InternalField during Serialization
     */
    public static class PlayerInternalBeanSerializerModifier extends BeanSerializerModifier {

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDescription,
                                                         List<BeanPropertyWriter> beanPropertyWriters) {
            return beanPropertyWriters.stream()
                    .filter(beanPropertyWriter -> beanPropertyWriter.getAnnotation(InternalField.class) == null)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Filters out properties marked with @InternalField during Deserialization
     */
    public static class PlayerInternalBeanDeserializerModifier extends BeanDeserializerModifier {

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig deserializationConfig,
                                                     BeanDescription beanDescription, BeanDeserializerBuilder builder) {
            Iterator<SettableBeanProperty> propertyIterator = builder.getProperties();
            List<SettableBeanProperty> propertiesToRemove = new ArrayList<>();
            while(propertyIterator.hasNext()) {
                SettableBeanProperty property = propertyIterator.next();
                if (property.getAnnotation(InternalField.class) != null) {
                    propertiesToRemove.add(property);
                }
            }

            propertiesToRemove.forEach(property -> builder.removeProperty(property.getFullName()));
            return builder;
        }
    }
}