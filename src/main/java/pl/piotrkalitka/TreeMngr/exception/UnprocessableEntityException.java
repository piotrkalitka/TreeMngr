package pl.piotrkalitka.TreeMngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException(Object... args) {
        super(getMessage(args));
    }

    private static String getMessage(Object... args) {
        StringBuilder builder = new StringBuilder();
        builder.append("Cannot perform request for given arguments: ");
        for (Object arg : args) {
            builder.append(arg)
                    .append(", ");
        }
        return builder.toString();
    }

}
