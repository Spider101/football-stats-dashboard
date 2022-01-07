package com.footballstatsdashboard.core.utils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DashboardReadonlyModule extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardReadonlyModule.class);

    @Override
    public String getModuleName() {
        return "DashboardReadonlyModule";
    }

    @Override
    public Version version() {
        return new Version(1, 0, 0, "SNAPSHOT", "com.footballstatsdashboard",
                "football-dashboard-parent");
    }

    @Override
    public void setupModule(SetupContext setupContext) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("initializing DashboardReadonlyModule");
        }

        setupContext.addBeanDeserializerModifier(new DashboardReadonlyBeanDeserializerModifier());
    }

    /**
     * Filters out properties marked with @Readonly during Deserialization
     */
    public static class DashboardReadonlyBeanDeserializerModifier extends BeanDeserializerModifier {

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig deserializationConfig,
                                                     BeanDescription beanDescription, BeanDeserializerBuilder builder) {
            Iterator<SettableBeanProperty> propertyIterator = builder.getProperties();
            List<SettableBeanProperty> propertiesToRemove = new ArrayList<>();
            while (propertyIterator.hasNext()) {
                SettableBeanProperty property = propertyIterator.next();
                if (property.getAnnotation(Readonly.class) != null) {
                    propertiesToRemove.add(property);
                }
            }

            propertiesToRemove.forEach(property -> builder.removeProperty(property.getFullName()));
            return builder;
        }
    }
}