package com.openfaas.function.commands.annotations;

import com.openfaas.function.commands.annotations.exceptions.HeaderRequiredException;
import com.openfaas.model.IRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RequiresHeaderAnnotation {

    public static void verify(Object object, IRequest req) throws HeaderRequiredException {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequiresHeader.class)) {
            String requiredHeader = clazz.getAnnotation(RequiresHeader.class).header();
            if (req.getHeader(requiredHeader) == null)
                throw new HeaderRequiredException("Missing header <" + requiredHeader + "> in request");
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresHeader {
        String header();
    }
}
