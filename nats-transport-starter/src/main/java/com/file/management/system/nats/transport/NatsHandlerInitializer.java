package com.file.management.system.nats.transport;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

import com.file.management.system.nats.transport.domain.Request;
import com.file.management.system.nats.transport.domain.Response;
import com.file.management.system.nats.transport.exception.NatsTransportException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsHandlerInitializer implements BeanPostProcessor {

    private final SyncNatsTransport syncNatsTransport;
    private final Validator validator;
    private final StringValueResolver stringValueResolver;

    @Override
    @SuppressWarnings("rawtypes, unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        var beanClass = getBeanClass(bean);
        if (beanClass.isAnnotationPresent(NatsHandler.class)) {
            var methods = Arrays.stream(beanClass.getDeclaredMethods())
              .filter((m) -> m.isAnnotationPresent(NatsHandlerMapping.class))
              .toList();

            methods.forEach(method -> {
                var natsHandlerMapping = method.getAnnotation(NatsHandlerMapping.class);
                var natsTopic = stringValueResolver.resolveStringValue(natsHandlerMapping.natsTopic());
                var responseClass = method.getReturnType();
                var parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    String errorMessage = String.format("Can`t subscribe to %s because method %s has invalid count of arguments", natsTopic, method.getName());
                    throw new NatsTransportException(errorMessage);
                }
                var requestClass = parameterTypes[0];

                NatsMessageHandler handler = (request) -> handleRequest(request, requestClass, responseClass, method, bean);
                syncNatsTransport.subscribe(natsTopic, handler, requestClass);
            });
        }

        return bean;
    }

    @SuppressWarnings("rawtypes")
    private Class getBeanClass(Object bean) {
        return bean instanceof Advised ? ((Advised) bean).getTargetClass() : bean.getClass();
    }

    private <T, R> Response<T> handleRequest(Request<R> request, Class<R> requestClass, Class<T> responseClass, Method method, Object bean) {
        Response<T> response;
        try {
            R requestBody = requestClass.cast(request.body());
            Set<ConstraintViolation<R>> violations = validator.validate(requestBody);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder(String.format("Request %s failed due to validation error: ", requestBody));
                for (ConstraintViolation<R> constraintViolation : violations) {
                    sb.append(constraintViolation.getPropertyPath());
                    sb.append(" field: ");
                    sb.append(constraintViolation.getMessage());
                    sb.append(';');
                }
                throw new ConstraintViolationException(sb.toString(), violations);
            }

            Object[] args = new Object[] {requestBody};

            Object result = method.invoke(bean, args);
            T responseBody = responseClass.cast(result);
            response = new Response<>(responseBody, HTTP_OK);
        } catch (ConstraintViolationException ex) {
            log.warn(ex.getMessage());
            response = new Response<>(null, HTTP_BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Handle request failed due to: {}", ex.getMessage(), ex);
            response = new Response<>(null, HTTP_INTERNAL_ERROR);
        }

        return response;
    }

}
