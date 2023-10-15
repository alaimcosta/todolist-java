package br.com.alaim.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.alaim.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if (servletPath.equals("/tasks/")) {
            //pegar a autenticação
            var authorization = request.getHeader("Authorization");



            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] autoDecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(autoDecode);
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Authorization");
            System.out.println(username);
            System.out.println(password);

            //valida usuário
            var user = this.userRepository.findByUsername(username);
            if (user ==null){
                response.sendError(401);
            }else {
                //valida senha
                var passwordVerrify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerrify.verified) {
                    filterChain.doFilter(request, response);
                }else {
                    response.sendError(401);
                }

                //segue execução
            }
        }else {
            filterChain.doFilter(request, response);
        }






    }
}
