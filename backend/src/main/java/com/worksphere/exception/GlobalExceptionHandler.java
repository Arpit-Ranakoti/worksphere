package com.worksphere.exception;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e){return body(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler({BadRequestException.class,DuplicateApplicationException.class}) ResponseEntity<Map<String,Object>> bad(RuntimeException e){return body(HttpStatus.BAD_REQUEST,e.getMessage());}
 @ExceptionHandler(UnauthorizedActionException.class) ResponseEntity<Map<String,Object>> forbidden(UnauthorizedActionException e){return body(HttpStatus.FORBIDDEN,e.getMessage());}
 @ExceptionHandler(Exception.class) ResponseEntity<Map<String,Object>> other(Exception e){return body(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()==null?"Server error":e.getMessage());}
 private ResponseEntity<Map<String,Object>> body(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("success",false,"message",m));}
}
