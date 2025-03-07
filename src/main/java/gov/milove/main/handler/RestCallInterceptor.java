package gov.milove.main.handler;

import static gov.milove.main.constants.Constants.TRACE_ID_HEADER_NAME;
import static gov.milove.main.constants.Constants.TRACE_ID_KEY;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
public class RestCallInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String traceId = request.getHeader(TRACE_ID_HEADER_NAME);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
      log.debug("Generated a new trace id: {}", traceId);
    }

    MDC.put(TRACE_ID_KEY, traceId);
    response.addHeader(TRACE_ID_HEADER_NAME, traceId);

    log.debug("Endpoint called: {}", request.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      Exception ex) throws Exception {
    log.debug("Endpoint response status is: {}", response.getStatus());
    MDC.clear();
  }
}
