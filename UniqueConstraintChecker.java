package com.workflow.callback.uniqueconstraint;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Component
public class UniqueConstraintChecker {

    @Autowired
    private EntityManager entityManager;

    public void checkUniqueness(Object entity) throws IllegalAccessException, NoSuchFieldException, SecurityException {
        Class<?> clazz = entity.getClass();
        Table table = clazz.getAnnotation(Table.class);

        Field idField = null;
        // Find the field annotated with @Id
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                idField.setAccessible(true);
                break;
            }
        }

        if (idField != null) {
            Object idValue = idField.get(entity);
            if (idValue != null) {
                return;
            }
        }
        
        if (table != null) {
            for (UniqueConstraint uc : table.uniqueConstraints()) {
                String whereClause = Arrays.stream(uc.columnNames())
                    .map(columnName -> "e." + columnName + " = :fieldValue_" + columnName)
                    .collect(Collectors.joining(" AND "));

                Query query = entityManager.createQuery("SELECT COUNT(e) FROM " + clazz.getSimpleName() + " e WHERE " + whereClause);

                for (String columnName : uc.columnNames()) {
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    query.setParameter("fieldValue_" + columnName, field.get(entity));
                }

                long count = (long) query.getSingleResult();
                if (count > 0) {
                    throw new IllegalStateException("Duplicate entry found for " + clazz.getSimpleName() + " with unique constraints " + Arrays.toString(uc.columnNames()));
                }
            }
        }
    }
}
