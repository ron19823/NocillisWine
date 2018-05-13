package com.nocilliswine.exceptionhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nocilliswine.dto.ResponseDto;
import com.nocilliswine.exception.BadRequestException;

/**
 * @author Rohan Sharma
 *
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ResponseDto> exceptionHandler(Exception ex) {
		LOGGER.info("Excepton {} being handled by global exception handler", ex.getClass());
		if (ex instanceof BadRequestException) {
			return new ResponseEntity<ResponseDto>(new ResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.toString()),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ResponseDto>(
				new ResponseDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
