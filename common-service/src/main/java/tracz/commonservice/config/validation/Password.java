package tracz.commonservice.config.validation;


import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tracz.commonservice.config.ExceptionMessages;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default ExceptionMessages.PASSWORD_CONSTRAINT;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
