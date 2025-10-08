package co.com.anfega.consumer;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private String message;
    private List<T> content;
}
