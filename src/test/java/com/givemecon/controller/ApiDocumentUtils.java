package com.givemecon.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.QueryParametersSnippet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiDocumentUtils {

    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                modifyHeaders().remove("Host"),
                prettyPrint());
    }

    public static OperationRequestPreprocessor getDocumentRequestWithAuth() {
        return preprocessRequest(
                modifyHeaders()
                        .remove("Host")
                        .set("Authorization", "Bearer {ACCESS-TOKEN}"),
                prettyPrint());
    }

    public static OperationRequestPreprocessor getDocumentRequestWithRefreshToken() {
        return preprocessRequest(
                modifyHeaders()
                        .remove("Host")
                        .set("Authorization", "Bearer {ACCESS-TOKEN}")
                        .set("Refresh-Token", "Bearer {REFRESH-TOKEN}"),
                prettyPrint());
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
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

    public static QueryParametersSnippet pagingQueryParameters(ParameterDescriptor... parameterDescriptors) {
        List<ParameterDescriptor> params = new ArrayList<>(
                Arrays.asList(
                        parameterWithName("page").optional().description("페이지 번호 (기본값 = 0)"),
                        parameterWithName("size").optional().description("페이지 크기 (기본값 = 10)"),
                        parameterWithName("sort").optional().description("정렬 기준 (기본값 = id)")
                )
        );

        params.addAll(Arrays.asList(parameterDescriptors));
        return queryParameters(params);
    }
}
