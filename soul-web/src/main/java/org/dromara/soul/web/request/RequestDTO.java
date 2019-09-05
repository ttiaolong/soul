/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.dromara.soul.web.request;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.dromara.soul.common.constant.Constants;
import org.dromara.soul.common.enums.HttpMethodEnum;
import org.dromara.soul.common.enums.RpcTypeEnum;
import org.dromara.soul.common.utils.JsonUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * the soul request DTO .
 *
 * @author xiaoyu(Myth)
 */
@Data
public class RequestDTO implements Serializable {

    /**
     * is module data.
     */
    private String module;

    /**
     * is method name .
     */
    private String method;

    /**
     * is rpcType data. now we only support "http","dubbo" "springCloud".
     * {@linkplain RpcTypeEnum}
     */
    private String rpcType;

    /**
     * httpMethod now we only support "get","post" .
     * {@linkplain  HttpMethodEnum}
     */
    private String httpMethod;

    /**
     * this is dubbo params.
     */
    private String dubboParams;

    /**
     * this is sign .
     */
    private String sign;

    /**
     * timestamp .
     */
    private String timestamp;

    /**
     * appKey .
     */
    private String appKey;

    /**
     * content is json data.
     */
    private String content;

    /**
     * extInfo is json data .
     */
    private String extInfo;

    /**
     * pathVariable
     */
    private String pathVariable;

    /**
     * startDateTime
     */
    private LocalDateTime startDateTime;

    /**
     * 请求参数
     */
    private Map<String, String> requestParams;

    /**
     * ServerHttpRequest transform RequestDTO .
     *
     * @param request {@linkplain ServerHttpRequest}
     * @return RequestDTO request dto
     */
    public static RequestDTO transform(final ServerHttpRequest request) {
        String module = request.getHeaders().getFirst(Constants.MODULE);
        //先获取header里面的method，为空就获取path最后一段作为方法名
        String method = request.getHeaders().getFirst(Constants.METHOD);
        if (StringUtils.isEmpty(method)) {
            String[] split = request.getPath().value().split("/");
            if (split.length > 0) method = split[split.length -1];
        }
        String appKey = request.getHeaders().getFirst(Constants.APP_KEY);
        String httpMethod = request.getHeaders().getFirst(Constants.HTTP_METHOD);
        String rpcType = request.getHeaders().getFirst(Constants.RPC_TYPE);
        String sign = request.getHeaders().getFirst(Constants.SIGN);
        String timestamp = request.getHeaders().getFirst(Constants.TIMESTAMP);
        String pathVariable = request.getHeaders().getFirst(Constants.PATH_VARIABLE);
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setModule(StringUtils.isEmpty(module) ? "module" : module);
        requestDTO.setMethod(StringUtils.isEmpty(method) ? "method" : method);
        requestDTO.setAppKey(appKey);
        requestDTO.setHttpMethod(StringUtils.isEmpty(httpMethod) ? HttpMethodEnum.POST.getName() : httpMethod);
        requestDTO.setRpcType(StringUtils.isEmpty(rpcType) ? RpcTypeEnum.HTTP.getName() : rpcType);
        requestDTO.setSign(sign);
        requestDTO.setTimestamp(timestamp);
        requestDTO.setPathVariable(pathVariable);
        requestDTO.setStartDateTime(LocalDateTime.now());
        Map<String, String> stringStringMap = request.getQueryParams().toSingleValueMap();
        if (MapUtils.isNotEmpty(stringStringMap)) {
            requestDTO.setExtInfo(JsonUtils.toJson(stringStringMap));
        }
        return requestDTO;
    }

    public static RequestDTO transformMap(MultiValueMap<String, String> queryParams) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setModule(queryParams.getFirst(Constants.MODULE));
        requestDTO.setMethod(queryParams.getFirst(Constants.METHOD));
        requestDTO.setRpcType(queryParams.getFirst(Constants.RPC_TYPE));
        return requestDTO;
    }

}
