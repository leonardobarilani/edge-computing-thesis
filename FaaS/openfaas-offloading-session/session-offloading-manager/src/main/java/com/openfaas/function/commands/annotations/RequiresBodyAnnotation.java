package com.openfaas.function.commands.annotations;

import com.openfaas.function.commands.annotations.exceptions.BodyRequiredException;
import com.openfaas.model.IRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RequiresBodyAnnotation {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresBody {
    }

    public static void verify (Object object, IRequest req) throws BodyRequiredException {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequiresBody.class)) {
            if (req.getBody() == null)
                throw new BodyRequiredException("Missing body in request");
        }
    }
}
