package tracz.userservice.config.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tracz.userservice.config.ExceptionMessages;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default ExceptionMessages.PASSWORD_CONSTRAINT;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
