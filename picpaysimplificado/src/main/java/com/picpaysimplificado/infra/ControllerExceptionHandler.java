
package com.picpaysimplificado.infra;


import com.picpaysimplificado.dtos.ExceptionDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import com.picpaysimplificado.exceptions.TransactionAuthorizationException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity threatDuplicateEntry(DataIntegrityViolationException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO("Usuario já cadastrado", "400");
        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity threat404 (EntityNotFoundException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO("Usuario não encontrado", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(TransactionAuthorizationException.class)
    public ResponseEntity threatTransactionAuthorization(TransactionAuthorizationException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), "403");
        return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity threatHttpClientError(HttpClientErrorException exception){

        if (exception.getStatusCode() == HttpStatus.FORBIDDEN &&
                exception.getResponseBodyAsString().contains("\"authorization\":false")) {

            ExceptionDTO exceptionDTO = new ExceptionDTO("Transação não autorizada. Por favor, tente novamente.", "403");
            return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
        }

        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), String.valueOf(exception.getStatusCode().value()));
        return new ResponseEntity<>(exceptionDTO, exception.getStatusCode());
    }


    @ExceptionHandler(RestClientException.class)
    public ResponseEntity threatRestClientException(RestClientException exception) {
        // Se a mensagem contiver o JSON de falha de autorização
        if (exception.getMessage() != null && exception.getMessage().contains("{\"status\":\"fail\",\"data\":{\"authorization\":false}}")) {
            ExceptionDTO exceptionDTO = new ExceptionDTO("Transação não autorizada. Por favor, tente novamente.", "403");
            return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
        }

        ExceptionDTO exceptionDTO = new ExceptionDTO("Erro na comunicação com serviço externo: " + exception.getMessage(), "500");
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity threatGeneralException(Exception exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), "500");
        return ResponseEntity.internalServerError().body(exceptionDTO);
    }
}