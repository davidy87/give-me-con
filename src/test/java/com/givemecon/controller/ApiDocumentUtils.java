package com.givemecon.controller;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface ApiDocumentUtils {

    static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                modifyHeaders().remove("Host"),
                prettyPrint());
    }

    static OperationRequestPreprocessor getDocumentRequestWithAuth() {
        return preprocessRequest(
                modifyHeaders()
                        .remove("Host")
                        .set("Authorization", "{ACCESS-TOKEN}"),
                prettyPrint());
    }

    static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(
                modifyHeaders()
                        .remove("Vary")
                        .remove("X-Content-Type-Options")
                        .remove("X-XSS-Protection")
                        .remove("Cache-Control")
                        .remove("Pragma")
                        .remove("Expires"),
                prettyPrint());
    }
}
