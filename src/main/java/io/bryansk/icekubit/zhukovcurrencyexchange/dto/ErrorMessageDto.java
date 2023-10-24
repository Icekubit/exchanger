package io.bryansk.icekubit.zhukovcurrencyexchange.dto;

public class ErrorMessageDto {
    private String message;

    public ErrorMessageDto() {
    }

    public ErrorMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
