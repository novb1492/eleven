package com.www.eleven.Exception;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    /**
     * 400error예외
     * @param exception
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> IllegalArgumentException(IllegalArgumentException exception) {
        log.info("IllegalArgumentException:{}",exception.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }
    /**
     * 형변환 실패
     * @param exception
     * @return
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> NumberFormatException(NumberFormatException exception) {
        log.info("NumberFormatException:{}",exception.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", "형변환실패 잘못된값요청: "+exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }
    /**
     * db트랜잭션 실패시
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> SQLException(SQLException exception) {
        log.info("SQLException:{}",exception.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    /**
     * 널포인터
     * @param exception
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> NullPointerException(NullPointerException exception) {
        log.info("NullPointerException:{}",exception.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", "예상치 못한 오류발생");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    /**
     * 파일 예외
     * @param exception
     * @return
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> IOException(Exception exception) {
        log.info("IOException:{}",exception.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", "파일처리중 알 수 없는 에러 발생 ");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
}
