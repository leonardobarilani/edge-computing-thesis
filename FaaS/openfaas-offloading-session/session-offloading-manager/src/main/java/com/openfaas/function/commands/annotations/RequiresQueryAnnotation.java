package com.openfaas.function.commands.annotations;

import com.openfaas.function.commands.annotations.exceptions.QueryRequiredException;

import com.openfaas.model.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RequiresQueryAnnotation {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresQuery {
        String query();
    }

    public static void verify (Object object, IRequest req) throws QueryRequiredException {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequiresQuery.class)) {
            String requiredQuery = clazz.getAnnotation(RequiresQuery.class).query();
            if (req.getQuery().get(requiredQuery) == null)
                throw new QueryRequiredException("Missing query <" + requiredQuery + "> in request");
        }
    }
}
