package webProject.togetherPartyTonight.domain.member.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import webProject.togetherPartyTonight.global.common.ErrorResponse;
import webProject.togetherPartyTonight.global.error.ErrorCode;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("들어오는 경로 - {}", request.getServletPath());

        String accessToken = request.getHeader("Authorization");

        try {
            String token = jwtProvider.resolveToken(accessToken);
            log.info("token 받아내기 - {}", token);
            if (token != null && jwtProvider.validateToken(token)) {

                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }catch (SignatureException  | MalformedJwtException | IllegalStateException e) {

            ErrorCode invalidTokenError = ErrorCode.FORBIDDEN;
            inValidTokenResponse(invalidTokenError,request,response,filterChain);
        }catch (ExpiredJwtException e){

            ErrorCode invalidTokenError = ErrorCode.EXPIRED_TOKEN;
            inValidTokenResponse(invalidTokenError,request,response,filterChain);
        }

        filterChain.doFilter(request, response);
    }


    private void inValidTokenResponse(ErrorCode errorCode,HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 실패 메시지 작성
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse("false", errorCode.getHttpStatus().value(), errorCode.getMessage());

        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }

}
