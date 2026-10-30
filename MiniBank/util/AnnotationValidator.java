package util;

import java.lang.reflect.Field;

import model.annotation.MaxLength;
import model.annotation.Positive;

public class AnnotationValidator {

    public static String[] validate(Object obj) {

        java.util.ArrayList<String> errors =
                new java.util.ArrayList<>();

        Class<?> currentClass = obj.getClass();

        while (currentClass != null) {

            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {

                try {
                    field.setAccessible(true);

                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {

                        Positive positive =
                                field.getAnnotation(Positive.class);

                        if (value instanceof Number) {

                            Number number = (Number) value;

                            if (number.doubleValue() <= 0) {
                                errors.add(
                                        field.getName()
                                        + " "
                                        + positive.message()
                                );
                            }
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {

                        MaxLength maxLength =
                                field.getAnnotation(MaxLength.class);

                        if (value instanceof String) {

                            String text = (String) value;

                            if (text.length() > maxLength.value()) {
                                errors.add(
                                        field.getName()
                                        + " must have maximum "
                                        + maxLength.value()
                                        + " characters"
                                );
                            }
                        }
                    }

                } catch (IllegalAccessException e) {

                    errors.add(
                            "Could not access field: "
                            + field.getName()
                    );
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}