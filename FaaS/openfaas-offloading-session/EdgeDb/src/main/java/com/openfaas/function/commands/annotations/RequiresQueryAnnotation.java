package com.openfaas.function.commands.annotations;

import com.openfaas.function.commands.annotations.exceptions.QueryRequiredException;
import com.openfaas.model.IRequest;

import java.lang.annotation.*;

public class RequiresQueryAnnotation {

    public static void verify(Object object, IRequest req) throws QueryRequiredException {
        Class<?> clazz = object.getClass();
        RequiresQuery[] requiresQueries = clazz.getAnnotationsByType(RequiresQuery.class);
        for (RequiresQuery requiredQuery : requiresQueries) {
            String query = requiredQuery.query();
            if (req.getQuery().get(query) == null) {
                throw new QueryRequiredException("Missing query <" + query + "> in request");
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(RequiresQueries.class)
    public @interface RequiresQuery {
        String query();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresQueries {
        RequiresQuery[] value();
    }
}
