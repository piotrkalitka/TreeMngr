package pl.piotrkalitka.TreeMngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FirstItemExistsException extends RuntimeException {

    public FirstItemExistsException() {
        super("First item already exists");
    }

}
