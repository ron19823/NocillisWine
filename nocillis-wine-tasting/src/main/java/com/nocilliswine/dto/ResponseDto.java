package com.nocilliswine.dto;

/**
 * @author Rohan Sharma
 *
 */
public class ResponseDto {
	Object response;
	String status;

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ResponseDto() {
	}

	public ResponseDto(Object response, String status) {
		super();
		this.response = response;
		this.status = status;
	}

}
